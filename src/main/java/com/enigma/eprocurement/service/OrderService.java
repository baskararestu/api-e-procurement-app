package com.enigma.eprocurement.service;

import com.enigma.eprocurement.dto.request.OrderRequest;
import com.enigma.eprocurement.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createNewOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(String id);
    List<OrderResponse> getAllOrder();
}
