package com.enigma.hotelbookingapp.Advices;


import com.enigma.hotelbookingapp.Exceptions.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFound(ResourceNotFoundException e) {
        APIError error = APIError
                .builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(new APIResponse(error), error.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception e) {
        APIError error = APIError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(new APIResponse(error), error.getStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIResponse> handleAuthenticationException(AuthenticationException e) {
        APIError apiError = APIError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(new APIResponse(apiError), apiError.getStatus());

    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<APIResponse> handleJwtException(JwtException e) {
        APIError apiError = APIError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(new APIResponse(apiError), apiError.getStatus());

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse> handleAcessDeniedException(AccessDeniedException e) {
        APIError apiError = APIError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(new APIResponse(apiError), apiError.getStatus());

    }

}
