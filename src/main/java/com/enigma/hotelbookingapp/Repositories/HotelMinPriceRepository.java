package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.DTO.HotelPriceDTO;
import com.enigma.hotelbookingapp.Entities.Hotel;
import com.enigma.hotelbookingapp.Entities.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Optional;

public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice,Long> {

    @Query("""
             SELECT new com.enigma.hotelbookingapp.DTO.HotelPriceDTO(i.hotel.hotelId, i.hotel.name, i.hotel.city, AVG(i.price))
             from HotelMinPrice i
             WHERE i.hotel.city=:city
             AND i.date BETWEEN :startDate AND :endDate
             AND i.hotel.active=true
             GROUP BY i.hotel.hotelId, i.hotel.name, i.hotel.city
            """
    )
    Page<HotelPriceDTO> findHotelBySearchParams(
            @Param("city") String city,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    @Override
    Optional<HotelMinPrice> findById(Long hotelId);

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDateTime date);


}
