package com.enigma.hotelbookingapp.Entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class HotelMinPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelMinPriceId;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; //Hotel Min price on a particular day

    @UpdateTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;


    public HotelMinPrice(Hotel hotel, BigDecimal price, LocalDateTime date) {
        this.hotel = hotel;
        this.price = price;
        this.date = date;
    }

    public HotelMinPrice() {

    }
}
