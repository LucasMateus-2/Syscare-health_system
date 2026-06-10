package com.polaris.syscare_backend.domain.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class GlobalExeptionHandler extends RuntimeException
{
    @RestControllerAdvice
    public class GlobalExceptionHandler
    {

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Void> handleBadCredentials(BadCredentialsException ex)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
