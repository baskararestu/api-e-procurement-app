package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.dto.request.OrderRequest;
import com.enigma.eprocurement.dto.response.*;
import com.enigma.eprocurement.entity.Admin;
import com.enigma.eprocurement.entity.Order;
import com.enigma.eprocurement.entity.OrderDetail;
import com.enigma.eprocurement.entity.ProductPrice;
import com.enigma.eprocurement.repository.OrderRepository;
import com.enigma.eprocurement.service.AdminService;
import com.enigma.eprocurement.service.OrderService;
import com.enigma.eprocurement.service.ProductPriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AdminService adminService;
    private final ProductPriceService productPriceService;
    @Transactional(rollbackOn = Exception.class)
    @Override
    public OrderResponse createNewOrder(OrderRequest orderRequest) {
        AdminResponse adminResponse = adminService.getById(orderRequest.getAdminId());

        List<OrderDetail> orderDetails = orderRequest.getOrderDetails().stream().map(orderDetailRequest -> {
            ProductPrice productPrice = productPriceService.getById(orderDetailRequest.getProductPriceId());
            return OrderDetail.builder()
                    .productPrice(productPrice)
                    .quantity(orderDetailRequest.getQuantity())
                    .build();
        }).toList();

        Order order = Order.builder()
                .admin(Admin.builder()
                        .id(adminResponse.getId())
                        .build())
                .transDate(LocalDateTime.now())
                .orderDetails(orderDetails)
                .build();
        orderRepository.saveAndFlush(order);

        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {

            orderDetail.setOrder(order);
            System.out.println(order);

            ProductPrice currentProductPrice = orderDetail.getProductPrice();
            currentProductPrice.setStock(currentProductPrice.getStock() - orderDetail.getQuantity());
            return OrderDetailResponse.builder()
                    .orderDetailId(orderDetail.getId())
                    .quantity(orderDetail.getQuantity())
                    .product(ProductResponse.builder()
                            .productId(currentProductPrice.getProduct().getId())
                            .productName(currentProductPrice.getProduct().getName())
                            .productCategory(currentProductPrice.getProduct().getCategory().getName())
                            .stock(currentProductPrice.getStock())
                            .price(currentProductPrice.getPrice())
                            .vendor(VendorResponse.builder()
                                    .id(currentProductPrice.getVendor().getId())
                                    .vendorName(currentProductPrice.getVendor().getName())
                                    .noSiup(currentProductPrice.getVendor().getNoSiup())
                                    .address(currentProductPrice.getVendor().getAddress())
                                    .mobilPhone(currentProductPrice.getVendor().getMobilePhone())
                                    .build())
                            .build())
                    .build();
        }).toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .transDate(order.getTransDate())
                .admin(adminResponse)
                .orderDetails(orderDetailResponses)
                .build();
    }

    @Override
    public OrderResponse getOrderById(String id) {
        return null;
    }

    @Override
    public List<OrderResponse> getAllOrder() {
        return null;
    }
}
