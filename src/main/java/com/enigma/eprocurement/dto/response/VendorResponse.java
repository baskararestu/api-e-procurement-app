package com.enigma.eprocurement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class VendorResponse {
    private String id;
    private String noSiup;
    private String vendorName;
    private String address;
    private String mobilPhone;
    private String email;
}
