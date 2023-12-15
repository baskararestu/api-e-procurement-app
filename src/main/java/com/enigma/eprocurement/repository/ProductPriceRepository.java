package com.enigma.eprocurement.repository;

import com.enigma.eprocurement.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, String> {
    //query method
    Optional<ProductPrice> findByProduct_IdAndVendorIdAndIsActive(String productId,String vendorid,Boolean active);
//    boolean existsByVendorId(String vendorId);

}
