package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.constant.ERole;
import com.enigma.eprocurement.dto.request.AuthRequest;
import com.enigma.eprocurement.dto.response.LoginResponse;
import com.enigma.eprocurement.dto.response.RegisterResponse;
import com.enigma.eprocurement.entity.*;
import com.enigma.eprocurement.mapper.AuthMapper;
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
        validationUtil.validate(authRequest);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername().toLowerCase(),
                        authRequest.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(appUser);

        return AuthMapper.getLoginResponse(authRequest, token, appUser);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerAdmin(AuthRequest authRequest) {
        try {
            Role role = AuthMapper.getRole(ERole.ROLE_ADMIN);
            role = roleService.getOrSave(role);

            UserCredential userCredential = AuthMapper.getUserCredential(authRequest, role, passwordEncoder);
            userCredentialRepository.saveAndFlush(userCredential);

            Admin admin = AuthMapper.getAdmin(authRequest, userCredential);
            adminService.create(admin);

            return AuthMapper.getRegisterResponse(authRequest, userCredential);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user admin already exist");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerVendor(AuthRequest authRequest) {
        try {
            Role role = AuthMapper.getRole(ERole.ROLE_VENDOR);
            role = roleService.getOrSave(role);

            UserCredential userCredential =
                    AuthMapper.getUserCredential(authRequest, role, passwordEncoder);
            userCredentialRepository.saveAndFlush(userCredential);

            Vendor vendor = AuthMapper.getVendor(authRequest, userCredential);
            vendorService.create(vendor);

            return AuthMapper.getRegisterResponse(authRequest, userCredential);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor already exist");
        }
    }
}
