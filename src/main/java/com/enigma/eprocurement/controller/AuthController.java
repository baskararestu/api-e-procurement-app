package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.AuthRequest;
import com.enigma.eprocurement.dto.request.AuthVendorRequest;
import com.enigma.eprocurement.dto.response.CommonResponse;
import com.enigma.eprocurement.dto.response.LoginResponse;
import com.enigma.eprocurement.dto.response.RegisterResponse;
import com.enigma.eprocurement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/admins")
    public ResponseEntity createAdminAccount(@RequestBody AuthRequest authRequest) {
        RegisterResponse registerResponse = authService.registerAdmin(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully create admin account")
                        .data(registerResponse)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody AuthRequest authRequest) {
        LoginResponse loginResponse = authService.login(authRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully login into app")
                        .data(loginResponse)
                        .build());
    }

    @PostMapping("/vendors")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createVendorAccount(@RequestBody AuthRequest authRequest) {
        RegisterResponse registerResponse = authService.registerVendor(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully create vendor account")
                        .data(registerResponse)
                        .build());
    }
}
