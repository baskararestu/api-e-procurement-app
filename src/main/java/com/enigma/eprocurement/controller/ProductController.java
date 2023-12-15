package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.ProductRequest;
import com.enigma.eprocurement.dto.response.CommonResponse;
import com.enigma.eprocurement.dto.response.ProductResponse;
import com.enigma.eprocurement.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.PRODUCT)
public class ProductController {
    private final ProductService productService;
    @PostMapping
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse  productResponse = productService.createProductCategoryAndProductPrice(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ProductResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created new product")
                        .data(productResponse)
                        .build());
    }
}
