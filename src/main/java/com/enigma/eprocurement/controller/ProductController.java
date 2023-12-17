package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.ProductRequest;
import com.enigma.eprocurement.dto.response.CommonResponse;
import com.enigma.eprocurement.dto.response.DefaultResponse;
import com.enigma.eprocurement.dto.response.PagingResponse;
import com.enigma.eprocurement.dto.response.ProductResponse;
import com.enigma.eprocurement.exeception.ProductAlreadyExistsException;
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
        try {
            ProductResponse productResponse = productService.createProductCategoryAndProductPrice(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DefaultResponse.builder()
                            .statusCode(HttpStatus.CREATED.value())
                            .message("Successfully created new product")
                            .data(productResponse)
                            .build());
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(DefaultResponse.builder()
                            .statusCode(HttpStatus.CONFLIC.value())
                            .message("Product with the same name and category already exists")
                            .data(null)
                            .build());
        }
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
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully getting data")
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
        if (updatedProduct == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DefaultResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message("No changes in the update request")
                            .data(null)
                            .build());
        }
        return ResponseEntity.ok(DefaultResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product price updated successfully")
                .data(updatedProduct)
                .build());
    }
}
