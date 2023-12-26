package com.enigma.eprocurement.controller;

import com.enigma.eprocurement.constant.AppPath;
import com.enigma.eprocurement.dto.request.AuthRequest;
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
import org.springframework.web.server.ResponseStatusException;

import static com.enigma.eprocurement.mapper.ResponseControllerMapper.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.AUTH)
public class AuthController {
    private final AuthService authService;
    private String message;

    @PostMapping("/admins")
    public ResponseEntity<?> createAdminAccount(@RequestBody AuthRequest authRequest) {
        try {
            RegisterResponse registerResponse = authService.registerAdmin(authRequest);
            message = "Successfully create admin account";
            return getResponseEntity(message, HttpStatus.CREATED, registerResponse);
        } catch (ResponseStatusException e) {
            message = e.getReason();
            return getResponseEntity(message, HttpStatus.CONFLICT, null);
        } catch (Exception e) {
            message = e.getMessage();
            return getResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {
        try {
            LoginResponse loginResponse = authService.login(authRequest);
            message = "Successfully login into app";
            return getResponseEntity(message, HttpStatus.OK, loginResponse);
        } catch (Exception e) {
            message = "Username or password are invalid";
            return getResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/vendors")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createVendorAccount(@RequestBody AuthRequest authRequest) {
        try {
            RegisterResponse registerResponse = authService.registerVendor(authRequest);
            message = "Successfully create vendor account";
            return getResponseEntity(message,HttpStatus.CREATED,registerResponse);
        } catch (ResponseStatusException e) {
            message = e.getReason();
            return getResponseEntity(message, HttpStatus.CONFLICT, null);
        } catch (Exception e) {
            message = e.getMessage();
            return getResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
