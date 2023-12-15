package com.enigma.eprocurement.dto.response;

import com.enigma.eprocurement.entity.ProductPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProductResponse {
    private String productId;
    private String productName;
    private String productCategory;
    private Long price;
    private Integer stock;
    private VendorResponse vendor;
    private List<ProductPrice>priceList;
}
