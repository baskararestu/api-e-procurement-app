package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.entity.ProductPrice;
import com.enigma.eprocurement.repository.ProductPriceRepository;
import com.enigma.eprocurement.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductPriceServiceImpl implements ProductPriceService {
    private final ProductPriceRepository productPriceRepository;

    @Override
    public ProductPrice create(ProductPrice productPrice) {
        return productPriceRepository.save(productPrice);
    }

    @Override
    public ProductPrice getById(String id) {
        return productPriceRepository.findById(id).orElse(null);
    }

    @Override
    public ProductPrice findProductPriceIsActive(String productId, Boolean active) {
        return productPriceRepository.findByProduct_IdAndIsActive(productId,active)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "product not found"));
    }
}
