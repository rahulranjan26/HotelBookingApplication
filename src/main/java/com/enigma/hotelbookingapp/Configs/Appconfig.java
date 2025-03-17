package com.enigma.hotelbookingapp.Configs;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Appconfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
