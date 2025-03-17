package com.enigma.hotelbookingapp.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "photos")
public class Photos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;


    private String url;

    @ManyToOne
    @JoinColumn(name = "hotel_photo_id")
    @JsonIgnore
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_photo_id")
    @JsonIgnore
    private Room room;



}
