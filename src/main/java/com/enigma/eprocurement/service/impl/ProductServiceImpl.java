package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.dto.request.ProductRequest;
import com.enigma.eprocurement.dto.response.ProductResponse;
import com.enigma.eprocurement.dto.response.VendorResponse;
import com.enigma.eprocurement.entity.Category;
import com.enigma.eprocurement.entity.Product;
import com.enigma.eprocurement.entity.ProductPrice;
import com.enigma.eprocurement.entity.Vendor;
import com.enigma.eprocurement.exeception.ProductAlreadyExistsException;
import com.enigma.eprocurement.repository.ProductPriceRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRespository productRespository;
    private final ProductPriceRepository productPriceRepository;
    private final VendorService vendorService;
    private final ProductPriceService productPriceService;
    private final CategoryService categoryService;

    @Override
    public List<ProductResponse> getAll() {
        List<Product> products = productRespository.findAll();
        return products.stream().map(product -> ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .priceList(product.getProductPrices())
                .productCategory(product.getCategory().getName())
                .build()).collect(Collectors.toList());
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public void delete(String id) {
        productRespository.deleteById(id);
    }

    @Override
    public ProductResponse getById(String id) {
        Product product = productRespository.findById(id).orElse(null);
        if (product != null) {
            return ProductResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .priceList(product.getProductPrices())
                    .productCategory(product.getCategory().getName())
                    .build();
        }
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse createProductCategoryAndProductPrice(ProductRequest productRequest) {
        VendorResponse vendorResponse = vendorService.getById(productRequest.getVendorId().getId());
        boolean getNameAndCategoryProduct =
                getByNameAndCategory(productRequest.getProductName(),
                        productRequest.getCategory());

        if (getNameAndCategoryProduct) {
            throw new ProductAlreadyExistsException("Product with the same name and category already exists");
        }

        return createOrUpdateProduct(null, productRequest, vendorResponse);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product existingProduct = productRespository.findById(productId).orElse(null);
        if (existingProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        VendorResponse vendorResponse = vendorService.getById(productRequest.getVendorId().getId());

        return createOrUpdateProduct(productId, productRequest, vendorResponse);
    }
    @Override
    public Page<ProductResponse> getAllByNameOrPrice(String name, Long maxPrice, Integer page, Integer size) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            Join<Product, ProductPrice> productPrices = root.join("productPrices");
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(productPrices.get("price"), maxPrice));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRespository.findAll(specification, pageable);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.getContent()) {
            Optional<ProductPrice> productPrice = product.getProductPrices()
                    .stream()
                    .filter(ProductPrice::isActive).findFirst();
            if (productPrice.isEmpty())
                continue;

            Vendor vendor = productPrice.get().getVendor();
            productResponses.add(toProductResponse(product, productPrice.get(), vendor));
        }
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Override
    public boolean getByNameAndCategory(String productName, String productCategory) {
        Optional<Product> product = productRespository.findByNameAndCategory_Name(productName, productCategory);
        return product.isPresent();
    }

    private ProductResponse createOrUpdateProduct(String productId, ProductRequest productRequest, VendorResponse vendorResponse) {
        Product existingProduct = productId != null ? productRespository.findById(productId).orElse(null) : null;

        if (productId == null) {
            Category category = Category.builder()
                    .name(productRequest.getCategory())
                    .build();
            category = categoryService.getOrSave(category);

            Product product = Product.builder()
                    .name(productRequest.getProductName())
                    .category(category)
                    .build();
            product = productRespository.saveAndFlush(product);

            // Create the initial product price
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

            return toProductResponse(product, productPrice, vendorResponse);
        }

        if (!isUpdateNeeded(productId, productRequest)) {
            return null;
        }
        Category category = Category.builder()
                .name(productRequest.getCategory())
                .build();
        category = categoryService.getOrSave(category);

        Product product = Product.builder()
                .id(productId)
                .name(productRequest.getProductName())
                .category(category)
                .build();
        productRespository.saveAndFlush(product);

        Optional<ProductPrice> existingActivePrice =
                productPriceRepository.findByProduct_IdAndVendorIdAndIsActive
                        (productId, productRequest.getVendorId().getId(), true);

        if (existingActivePrice.isPresent()) {
            if (!existingActivePrice.get().getStock().equals(productRequest.getStock()) &&
                    !existingActivePrice.get().getPrice().equals(productRequest.getPrice())) {

                deactivatePreviousActivePrices(productId, productRequest.getVendorId().getId());

                ProductPrice productPrice = ProductPrice.builder()
                        .price(productRequest.getPrice())
                        .stock(productRequest.getStock())
                        .isActive(true)
                        .product(existingProduct)
                        .vendor(Vendor.builder()
                                .id(existingActivePrice.get().getVendor().getId())
                                .build())
                        .build();

                productPriceService.create(productPrice);

                return toProductResponse(existingProduct, productPrice, vendorResponse);
            } else {
                existingActivePrice.get().setStock(productRequest.getStock());
                productPriceRepository.saveAndFlush(existingActivePrice.get());

                return toProductResponse(existingProduct, existingActivePrice.orElse(null), vendorResponse);
            }
        } else {
            return null;
        }
    }

    private void deactivatePreviousActivePrices(String productId, String vendorId) {
        productPriceRepository.findByProduct_IdAndVendorIdAndIsActive(productId, vendorId, true)
                .ifPresent(previousPrice -> {
                    previousPrice.setActive(false);
                    productPriceRepository.saveAndFlush(previousPrice);
                });
    }

    private boolean isUpdateNeeded(String productId, ProductRequest productRequest) {
        Optional<Product> existingProduct = productRespository.findById(productId);

        Optional<ProductPrice> existingActivePrice = productPriceRepository.findByProduct_IdAndVendorIdAndIsActive(productId, productRequest.getVendorId().getId(), true);

        return existingProduct.isPresent() &&
                existingActivePrice.isPresent() &&
                (!existingProduct.get().getName().equals(productRequest.getProductName()) ||
                        !existingActivePrice.get().getPrice().equals(productRequest.getPrice()) ||
                        !existingActivePrice.get().getStock().equals(productRequest.getStock()));
    }

    private static ProductResponse toProductResponse(Product product, ProductPrice productPrice, Object vendor) {
        VendorResponse vendorResponse;
        if (vendor instanceof Vendor actualVendor) {
            vendorResponse = VendorResponse.builder()
                    .id(actualVendor.getId())
                    .noSiup(actualVendor.getNoSiup())
                    .vendorName(actualVendor.getName())
                    .mobilPhone(actualVendor.getMobilePhone())
                    .address(actualVendor.getAddress())
                    .build();
        } else if (vendor instanceof VendorResponse) {
            vendorResponse = (VendorResponse) vendor;
        } else {
            vendorResponse = null;
        }

        return ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productCategory(product.getCategory().getName())
                .price(productPrice != null ? productPrice.getPrice() : null)
                .stock(productPrice != null ? productPrice.getStock() : null)
                .vendor(vendorResponse)
                .build();
    }
}
