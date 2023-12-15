package com.enigma.eprocurement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class VendorRequest {
    private String id;
    private String name;
    private String mobilePhone;
    private String noSiup;
    private String address;
}
