package com.enigma.eprocurement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "m_product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
