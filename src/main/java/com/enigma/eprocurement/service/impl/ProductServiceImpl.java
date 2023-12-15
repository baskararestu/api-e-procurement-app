package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.dto.request.ProductRequest;
import com.enigma.eprocurement.dto.response.ProductResponse;
import com.enigma.eprocurement.dto.response.VendorResponse;
import com.enigma.eprocurement.entity.Category;
import com.enigma.eprocurement.entity.Product;
import com.enigma.eprocurement.entity.ProductPrice;
import com.enigma.eprocurement.entity.Vendor;
import com.enigma.eprocurement.repository.ProductRespository;
import com.enigma.eprocurement.service.CategoryService;
import com.enigma.eprocurement.service.ProductPriceService;
import com.enigma.eprocurement.service.ProductService;
import com.enigma.eprocurement.service.VendorService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRespository productRespository;
    private final VendorService vendorService;
    private final ProductPriceService productPriceService;
    private final CategoryService categoryService;

    @Override
    public Product create(Product product) {
        return productRespository.save(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        List<Product> products = productRespository.findAll();
        return products.stream().map(product -> ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .priceList(product.getProductPrices())
                .build()).collect(Collectors.toList());
    }


    @Override
    public void delete(String id) {
        productRespository.deleteById(id);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse createProductCategoryAndProductPrice(ProductRequest productRequest) {
        VendorResponse vendorResponse = vendorService.getById(productRequest.getVendorId().getId());
//        Vendor vendor = new Vendor();
        Category category = Category.builder()
                .name(productRequest.getCategory())
                .build();
        category = categoryService.getOrSave(category);

        Product product = Product.builder()
                .name(productRequest.getProductName())
                .category(category)
                .build();
        productRespository.saveAndFlush(product);
        ProductPrice productPrice = ProductPrice.builder()
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .isActive(true)
                .product(product)
                .vendor(Vendor.builder()
                        .id(vendorResponse.getId())
                        .build())
                .build();
        productPriceService.create(productPrice);
        return ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productCategory(product.getCategory().getName())
                .stock(productRequest.getStock())
                .price(productPrice.getPrice())
                .stock(productPrice.getStock())
                .vendor(VendorResponse.builder()
                        .id(vendorResponse.getId())
                        .noSiup(vendorResponse.getNoSiup())
                        .vendorName(vendorResponse.getVendorName())
                        .mobilPhone(vendorResponse.getMobilPhone())
                        .address(vendorResponse.getAddress())
                        .build())
                .build();
    }

    @Override
    public Page<ProductResponse> getAllByNameOrPrice(String name, Long maxPrice, Integer page, Integer size) {
    return null;
    }
}
