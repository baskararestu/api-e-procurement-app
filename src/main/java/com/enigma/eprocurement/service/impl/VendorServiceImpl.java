package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.dto.response.VendorResponse;
import com.enigma.eprocurement.entity.Vendor;
import com.enigma.eprocurement.repository.VendorRepository;
import com.enigma.eprocurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;
    @Override
    public VendorResponse create(Vendor vendor) {
        vendorRepository.saveAndFlush(vendor);
        return VendorResponse.builder()
                .id(vendor.getId())
                .noSiup(vendor.getNoSiup())
                .vendorName(vendor.getName())
                .address(vendor.getAddress())
                .mobilPhone(vendor.getMobilePhone())
                .build();
    }
}
