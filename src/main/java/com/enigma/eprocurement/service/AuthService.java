package com.enigma.eprocurement.service;

import com.enigma.eprocurement.dto.request.AuthRequest;
import com.enigma.eprocurement.dto.request.AuthVendorRequest;
import com.enigma.eprocurement.dto.response.LoginResponse;
import com.enigma.eprocurement.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse login (AuthRequest authRequest);
    RegisterResponse registerAdmin(AuthRequest authRequest);
    RegisterResponse registerVendor(AuthRequest authRequest);

}
