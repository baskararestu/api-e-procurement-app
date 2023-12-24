package com.enigma.eprocurement.mapper;

import com.enigma.eprocurement.dto.response.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityMapper {
    public static ResponseEntity<?> getResponseEntity(String errorMessage, HttpStatus httpStatus, Object object) {
        return ResponseEntity.status(httpStatus)
                .body(DefaultResponse.builder()
                        .statusCode(httpStatus.value())
                        .message(errorMessage)
                        .data(object)
                        .build());
    }
}
