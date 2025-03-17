package com.enigma.hotelbookingapp.Advices;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class APIError {
    private String message;
    private HttpStatus status;
    private List<String> subErrors;
}
