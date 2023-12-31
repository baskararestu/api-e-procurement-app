package com.enigma.eprocurement.service;

import com.enigma.eprocurement.dto.request.ProductRequest;
import com.enigma.eprocurement.dto.response.ProductResponse;
import com.enigma.eprocurement.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();

    void delete(String id);
    ProductResponse getById(String id);
    boolean getByNameAndCategory(String productName,String productCategory);

    ProductResponse createProductCategoryAndProductPrice(ProductRequest productRequest);
    Page<ProductResponse> getAllByNameOrPrice(String name, Long maxPrice, Integer page, Integer size);

    ProductResponse updateProduct(String productId,ProductRequest productRequest);
}
