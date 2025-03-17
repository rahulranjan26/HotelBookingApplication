package com.enigma.hotelbookingapp.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTO {
    private String email;
    private Long userId;
    private String name;
}
