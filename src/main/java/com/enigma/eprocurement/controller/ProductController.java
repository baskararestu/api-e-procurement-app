package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.ProductRequest;
import com.enigma.eprocurement.dto.response.CommonResponse;
import com.enigma.eprocurement.dto.response.PagingResponse;
import com.enigma.eprocurement.dto.response.ProductResponse;
import com.enigma.eprocurement.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.PRODUCT)
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProductCategoryAndProductPrice(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ProductResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created new product")
                        .data(productResponse)
                        .build());
    }

    @GetMapping
    public List<ProductResponse> getAllProduct() {
        return productService.getAll();
    }

    @GetMapping("/page")
    public ResponseEntity<?> getAllProductPage(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size
    ) {
        Page<ProductResponse> productResponses = productService.getAllByNameOrPrice(name, maxPrice, page, size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(productResponses.getTotalPages())
                .size(size)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(productResponses.getContent())
                        .pagingResponse(pagingResponse)
                        .build());
    }
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> updateProductPrice(
            @PathVariable String productId,
            @RequestBody ProductRequest productRequest
    ) {
        ProductResponse updatedProduct = productService.updateProductPrice(productId, productRequest);
        return ResponseEntity.ok(CommonResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product price updated successfully")
                .data(updatedProduct)
                .build());
    }
}
