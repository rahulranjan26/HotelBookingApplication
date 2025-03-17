package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.Entities.Booking;
import com.enigma.hotelbookingapp.Entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    @Query(
            """
                    select b
                    from Booking b
                    where b.hotel.hotelId=:hotelId
                    """
    )
    List<Booking> findByHotel(
            @Param("hotelId") Long hotelId
    );

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDate, LocalDateTime endDate);
}
