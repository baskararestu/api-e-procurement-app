package com.enigma.eprocurement.mapper;

import com.enigma.eprocurement.constant.ERole;
import com.enigma.eprocurement.dto.request.AuthRequest;
import com.enigma.eprocurement.dto.response.LoginResponse;
import com.enigma.eprocurement.dto.response.RegisterResponse;
import com.enigma.eprocurement.entity.*;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthMapper {
    public static Role getRole(ERole eRole) {
        return Role.builder()
                .roleName(eRole)
                .build();
    }
    public static UserCredential getUserCredential
            (AuthRequest authRequest, Role role,PasswordEncoder passwordEncoder) {
        return UserCredential.builder()
                .username(authRequest.getUsername())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .role(role)
                .build();
    }


    public static Admin getAdmin(AuthRequest authRequest, UserCredential userCredential) {
        return Admin.builder()
                .userCredential(userCredential)
                .name(authRequest.getName())
                .email(authRequest.getEmail())
                .phoneNumber(authRequest.getMobilePhone())
                .build();
    }

    public static Vendor getVendor(AuthRequest authRequest, UserCredential userCredential) {
        return Vendor.builder()
                .userCredential(userCredential)
                .noSiup(authRequest.getNoSiup())
                .name(authRequest.getName())
                .address(authRequest.getAddress())
                .mobilePhone(authRequest.getMobilePhone())
                .build();
    }

    public static RegisterResponse getRegisterResponse(AuthRequest authRequest, UserCredential userCredential) {
        return RegisterResponse.builder()
                .username(userCredential.getUsername())
                .name(authRequest.getName())
                .role(userCredential.getRole().getRoleName().toString())
                .build();
    }
    public static LoginResponse getLoginResponse(AuthRequest authRequest, String token, AppUser appUser) {
        return LoginResponse.builder()
                .username(authRequest.getUsername())
                .token(token)
                .role(appUser.getRole().name())
                .build();
    }
}
