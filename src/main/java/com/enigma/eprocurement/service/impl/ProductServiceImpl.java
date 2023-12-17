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
        //specification menentukan kriteria pencarian,disini kriteria pencarian tindakan dengan root,root yang dimaksud adalah entity product
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            Join<Product, ProductPrice> productPrices = root.join("productPrices");
            //Prediction digunakan untuk mengunakan LIKE dimana nanti kita akan menggunakan kondisi pencarian parameter
            //disini kita akan mencari nama produk atau harga yang sama atau harga dibawahnya, makannya menggunakan lessthanorequal
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(productPrices.get("price"), maxPrice));
            }
            //kode return mengembalikan query dimana pada dasarnya kita membangun klausa where yang sudah ditentukan dari predicate atau kriteria
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRespository.findAll(specification, pageable);
        //ini digunakan untuk menyimpan response product yang baru
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.getContent()) {
            //digunakan untuk mengiterasi data product yang disimpan dalam objek
            Optional<ProductPrice> productPrice = product.getProductPrices()
                    .stream()
                    .filter(ProductPrice::isActive).findFirst();//optional ini untk mencari harga yang aktif
            if (productPrice.isEmpty())//kondisi ini digunakan untuk memerika pakah productPricenya kosong atau tidak, jika tidak maka di skip
                continue;

            Vendor vendor = productPrice.get().getVendor();//ini digunakan untuk jika harga product yang aktif ditemukan distore
            productResponses.add(toProductResponse(product, productPrice.get(), vendor));
        }
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse updateProductPrice(String productId, ProductRequest productRequest) {
        if (!isUpdateNeeded(productId, productRequest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No changes in the update request");
        }
        deactivatePreviousActivePrices(productId, productRequest.getVendorId().getId());
        VendorResponse vendorResponse = vendorService.getById(productRequest.getVendorId().getId());
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
    public boolean getByNameAndCategory(String productName, String productCategory) {
        Optional<Product> product = productRespository.findByNameAndCategory_Name(productName, productCategory);
        return product.isPresent();
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

    private static ProductResponse toProductResponse
            (Product product, ProductPrice productPrice, Vendor vendor) {
        return ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productCategory(product.getCategory().getName())
                .stock(productPrice.getStock())
                .price(productPrice.getPrice())
                .stock(productPrice.getStock())
                .vendor(VendorResponse.builder()
                        .id(vendor.getId())
                        .noSiup(vendor.getNoSiup())
                        .vendorName(vendor.getName())
                        .mobilPhone(vendor.getMobilePhone())
                        .address(vendor.getAddress())
                        .build())
                .build();
    }
}
