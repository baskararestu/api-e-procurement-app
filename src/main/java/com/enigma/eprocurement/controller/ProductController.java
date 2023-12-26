package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.ProductRequest;
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

import static com.enigma.eprocurement.mapper.ResponseControllerMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.PRODUCT)
public class ProductController {
    private final ProductService productService;
    private String message;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        try {
            ProductResponse productResponse = productService.createProductCategoryAndProductPrice(productRequest);
            message = "Successfully created new product";
            return getResponseEntity(message, HttpStatus.CREATED, productResponse);
        } catch (ProductAlreadyExistsException e) {
            message = "Product with the same name and category already exists";
            return getResponseEntity(message, HttpStatus.CONFLICT, null);
        } catch (Exception e) {
            return getResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProduct() {
        try {
            message = "Successfully getting data";
            List<ProductResponse> productResponses = productService.getAll();
            return getResponseEntity(message, HttpStatus.OK, productResponses);
        } catch (Exception e) {
            return getResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<?> getAllProductPage(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size
    ) {
        try {
            Page<ProductResponse> productResponses = productService.getAllByNameOrPrice(name, maxPrice, page, size);
            message = "Successfully getting data";
            PagingResponse pagingResponse = getPagingResponse(page, size, productResponses);
            return getResponseEntityPaging(message, HttpStatus.OK, productResponses.getContent(), pagingResponse);
        } catch (Exception e) {
            return getResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> updateProductPrice(
            @PathVariable String productId,
            @RequestBody ProductRequest productRequest
    ) {
        try {
            ProductResponse updatedProduct = productService.updateProduct(productId, productRequest);
            if (updatedProduct == null) {
                message = "No changes in the update request";
                return getResponseEntity(message, HttpStatus.OK, null);
            }
            message = "Product price updated successfully";
            return getResponseEntity(message, HttpStatus.OK, updatedProduct);
        } catch (Exception e) {
            return getResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        try {
            message = "Successfully getting data";
            return getResponseEntity(message, HttpStatus.OK, productService.getById(productId));
        }catch (Exception e){
            return getResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,null);
        }
    }
}
