package com.enigma.hotelbookingapp.Advices;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class APIResponse<T> {

    private LocalDateTime time;
    private T data;

    private APIError error;

    public APIResponse(T data) {
        this();
        this.data = data;
    }

    public APIResponse() {
        this.time = LocalDateTime.now();
    }

    public APIResponse(APIError error) {
        this();
        this.error = error;
    }
}
