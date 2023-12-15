package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.constant.ERole;
import com.enigma.eprocurement.dto.request.AuthRequest;
import com.enigma.eprocurement.dto.response.LoginResponse;
import com.enigma.eprocurement.dto.response.RegisterResponse;
import com.enigma.eprocurement.entity.*;
import com.enigma.eprocurement.repository.UserCredentialRepository;
import com.enigma.eprocurement.security.JwtUtil;
import com.enigma.eprocurement.service.AdminService;
import com.enigma.eprocurement.service.AuthService;
import com.enigma.eprocurement.service.RoleService;
import com.enigma.eprocurement.service.VendorService;
import com.enigma.eprocurement.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final VendorService vendorService;
    private final RoleService roleService;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;


    @Override
    public LoginResponse login(AuthRequest authRequest) {
        //tempat untuk logic login
        validationUtil.validate(authRequest);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername().toLowerCase(),
                        authRequest.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //object AppUser
        AppUser appUser = (AppUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(appUser);
        return LoginResponse.builder()
                .username(authRequest.getUsername())
                .token(token)
                .role(appUser.getRole().name())
                .build();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerAdmin(AuthRequest authRequest) {
        try {
            //TODO 1: set role
            Role role = Role.builder()
                    .roleName(ERole.ROLE_ADMIN)
                    .build();
            role = roleService.getOrSave(role);
            //TODO 2: set credential
            UserCredential userCredential = UserCredential.builder()
                    .username(authRequest.getUsername())
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .role(role)
                    .build();
            userCredentialRepository.saveAndFlush(userCredential);
            //TODO 3: set admin
            Admin admin = Admin.builder()
                    .userCredential(userCredential)
                    .name(authRequest.getName())
                    .email(authRequest.getEmail())
                    .phoneNumber(authRequest.getMobilePhone())
                    .build();
            adminService.create(admin);
            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .name(authRequest.getName())
                    .role(userCredential.getRole().getRoleName().toString())
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user admin already exist");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerVendor(AuthRequest authRequest) {
        try {
            Role role = Role.builder()
                    .roleName(ERole.ROLE_VENDOR)
                    .build();
            role = roleService.getOrSave(role);
            UserCredential userCredential = UserCredential.builder()
                    .username(authRequest.getUsername())
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .role(role)
                    .build();
            userCredentialRepository.saveAndFlush(userCredential);
            Vendor vendor = Vendor.builder()
                    .userCredential(userCredential)
                    .noSiup(authRequest.getNoSiup())
                    .name(authRequest.getName())
                    .address(authRequest.getAddress())
                    .mobilePhone(authRequest.getMobilePhone())
                    .build();
            vendorService.create(vendor);
            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .name(authRequest.getName())
                    .role(userCredential.getRole().getRoleName().toString())
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor already exist");
        }
    }
}
