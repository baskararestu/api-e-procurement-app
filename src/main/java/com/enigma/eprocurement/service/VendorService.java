package com.enigma.eprocurement.service;

import com.enigma.eprocurement.dto.response.VendorResponse;
import com.enigma.eprocurement.entity.Vendor;

public interface VendorService {
    VendorResponse create(Vendor vendor);
}
