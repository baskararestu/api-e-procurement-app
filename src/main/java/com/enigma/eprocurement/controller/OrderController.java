package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.OrderRequest;
import com.enigma.eprocurement.dto.response.CommonResponse;
import com.enigma.eprocurement.dto.response.OrderResponse;
import com.enigma.eprocurement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.ORDER)
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createNewOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created new order")
                        .data(orderResponse)
                        .build());
    }
}
