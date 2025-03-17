package com.enigma.hotelbookingapp.DTO;

import com.enigma.hotelbookingapp.Entities.Enums.Gender;
import com.enigma.hotelbookingapp.Entities.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@NoArgsConstructor
public class GuestDTO {

    private Long guestId;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
