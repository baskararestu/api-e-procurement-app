package com.enigma.eprocurement.repository;

import com.enigma.eprocurement.constant.ERole;
import com.enigma.eprocurement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByRoleName(ERole roleName);
}
