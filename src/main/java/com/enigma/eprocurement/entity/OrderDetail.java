package com.enigma.eprocurement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_order_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_price_id")
    private ProductPrice productPrice;
    private Integer quantity;
}
