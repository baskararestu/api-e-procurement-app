package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.dto.response.AdminResponse;
import com.enigma.eprocurement.entity.Admin;
import com.enigma.eprocurement.repository.AdminRepository;
import com.enigma.eprocurement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override
    public AdminResponse create(Admin admin) {
        adminRepository.saveAndFlush(admin);
        return AdminResponse.builder()
                .name(admin.getName())
                .email(admin.getEmail())
                .phoneNumber(admin.getPhoneNumber())
                .build();
    }

    @Override
    public AdminResponse getById(String id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            return AdminResponse.builder()
                    .id(admin.getId())
                    .name(admin.getName())
                    .email(admin.getEmail())
                    .phoneNumber(admin.getPhoneNumber())
                    .build();
        }
        return null;
    }
}
