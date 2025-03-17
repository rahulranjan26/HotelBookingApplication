package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.DTO.BookingDTO;
import com.enigma.hotelbookingapp.DTO.BookingRequest;
import com.enigma.hotelbookingapp.DTO.GuestDTO;
import com.enigma.hotelbookingapp.DTO.HotelReportDTO;
import com.enigma.hotelbookingapp.Entities.*;
import com.enigma.hotelbookingapp.Entities.Enums.BookingStatus;
import com.enigma.hotelbookingapp.Exceptions.ResourceNotFoundException;
import com.enigma.hotelbookingapp.Exceptions.UnAuthorisedException;
import com.enigma.hotelbookingapp.Repositories.*;
import com.enigma.hotelbookingapp.Strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final CheckoutService checkoutService;

    @Value("${frontend.url}")
    private String frontEndurl;


    @Override
    @Transactional
    public BookingDTO initialiseBooking(BookingRequest bookingRequest) {

        Hotel hotel = hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("The hotel with Id : " + bookingRequest.getHotelId() + " not found in db"));

        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("The room with Id: " + bookingRequest.getRoomId() + " not found in db"));

        List<Inventory> inventories = inventoryRepository
                .findAndLockAvailableInventory(bookingRequest.getRoomId(),
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate(),
                        bookingRequest.getRoomsCount());

        long totalCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (totalCount != inventories.size())
            throw new IllegalStateException("The inventory is not present for the dates provided");

        for (Inventory inventory : inventories) {
            inventory.setBookedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventories);

        BigDecimal inventoryPricing = pricingService.calculateTotalPrice(inventories);

        BigDecimal finalPrice = inventoryPricing.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));


        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel).room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getUser()).roomCount(bookingRequest.getRoomsCount())
                .amount(finalPrice)
                .build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDTO.class);

    }

    @Override
    @Transactional
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guests) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("The hotel with Id : " + bookingId + " not found in db"));

        User user = getUser();

        if (!user.equals(booking.getUser()))
            throw new UnAuthorisedException("Booking doesnt belong to this user: " + user.getName());


        if (!booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
            throw new IllegalStateException("The Booking is invalid as the seats are not reserved");
        }
        if (hasBookingExpired(booking))
            throw new IllegalStateException("The booking has expired. Please move ahead for a new booking");

        for (GuestDTO guest : guests) {
            Guest changedGuest = modelMapper.map(guest, Guest.class);
            changedGuest.setUser(getUser());
            Guest savedGuest = guestRepository.save(changedGuest);
            booking.getGuest().add(savedGuest);
        }
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        Booking savedBooking = bookingRepository.save(booking);


        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("The booking with booking Id : " + bookingId + " not found in the DB"));

        User user = getUser();
        if (!user.equals(booking.getUser()))
            throw new UnAuthorisedException("Booking doesnt belong to this user: " + user.getName());

        if (hasBookingExpired(booking))
            throw new IllegalStateException("The booking has expired. Please move ahead for a new booking");


        String sessionUrl = checkoutService.getCheckoutSession(booking, frontEndurl + "/payments/success", frontEndurl + "/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturepayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session == null) return;
            String sessionId = session.getId();

            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(
                    () -> new ResourceNotFoundException("The booking with booking Id not found"));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getRoomId(),
                    booking.getCheckInDate(), booking.getCheckOutDate(),
                    booking.getRoomCount());
            inventoryRepository.confirmBooking(booking.getRoom().getRoomId(),
                    booking.getCheckInDate(), booking.getCheckOutDate(),
                    booking.getRoomCount());

            log.info("Booking for booking id: {} is Successfully", booking.getBookingId());
        }
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("The hotel with Id : " + bookingId + " not found in db"));

        User user = getUser();

        if (!user.equals(booking.getUser()))
            throw new UnAuthorisedException("Booking doesnt belong to this user: " + user.getName());


        if (!booking.getBookingStatus().equals(BookingStatus.CONFIRMED)) {
            throw new IllegalStateException("The Booking is invalid as the booking status is not confirmed");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getRoomId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomCount());

        inventoryRepository.cancelBooking(booking.getRoom().getRoomId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomCount());

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        log.info("The booking with booking id : {} has been cancelled", bookingId);

    }

    @Override
    public HotelReportDTO getReports(Long hotelId, LocalDateTime startDate, LocalDateTime endDate) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("the Hotel with Id :" + hotelId + " note present in the DB"));

        User user = getUser();
        if (!user.equals(hotel.getOwner()))
            throw new UnAuthorisedException("Booking doesnt belong to this user: " + user.getName());

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDate, endDate);

        Long totalBooking = (long) bookings.size();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Booking booking : bookings)
            totalAmount.add(booking.getAmount());

        BigDecimal average = totalBooking == 0 ? BigDecimal.ZERO : totalAmount.divide(BigDecimal.valueOf(totalBooking), RoundingMode.valueOf(2));


        return new HotelReportDTO(totalBooking, totalAmount, average);


    }


    private boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(100L).isBefore(LocalDateTime.now());
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
