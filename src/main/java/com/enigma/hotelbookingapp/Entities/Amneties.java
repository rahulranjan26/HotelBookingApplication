package com.enigma.hotelbookingapp.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "amneties")
public class Amneties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amnetieId;

    private String name;

    @ManyToOne
    @JoinColumn(name="hotel_amneties_id")
    @JsonIgnore
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name="room_amneties_id")
    @JsonIgnore
    private Room room;


}
