package com.enigma.eprocurement.entity;

import com.enigma.eprocurement.constant.ERole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "m_role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private ERole roleName;
}
