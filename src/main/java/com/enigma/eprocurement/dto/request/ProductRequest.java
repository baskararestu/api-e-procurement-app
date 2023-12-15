package com.enigma.eprocurement.dto.request;

import com.enigma.eprocurement.dto.response.VendorResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProductRequest {
    private String productId;
    @NotBlank(message = "product name is required")
    private String productName;
    @NotBlank(message = "description is required")
    private String category;
    @NotBlank(message = "product price is required")
    @Min(value = 0,message = "product price must be greater than 0")
    private Long price;
    @NotBlank(message = "product stock is required")
    @Min(value = 0,message = "stock must be greater than 0")
    private Integer stock;
    @NotBlank(message = "vendorId is required")
    private VendorResponse vendorId;
}
