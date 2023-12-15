package com.enigma.eprocurement.service;

import com.enigma.eprocurement.entity.ProductPrice;

public interface ProductPriceService {
    ProductPrice create(ProductPrice productPrice);

    ProductPrice getById(String id);
    ProductPrice findProductPriceIsActive(String productId,String vendorId, Boolean isActive);
}
