package com.enigma.hotelbookingapp.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignUpRequestDTO {
    private String email;
    private String password;
    private String name;
}
