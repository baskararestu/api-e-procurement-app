package com.enigma.eprocurement.repository;

import com.enigma.eprocurement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//Store = class, String = id karena pake uuid
public interface VendorRepository extends JpaRepository<Vendor,String> {
}
