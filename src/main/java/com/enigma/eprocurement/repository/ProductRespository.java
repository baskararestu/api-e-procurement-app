package com.enigma.eprocurement.repository;

import com.enigma.eprocurement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRespository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
//    boolean existsByNameAndCategory(String name,String category);
}
