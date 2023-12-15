package com.enigma.eprocurement.service;

import com.enigma.eprocurement.dto.response.AdminResponse;
import com.enigma.eprocurement.entity.Admin;

public interface AdminService {
    AdminResponse create(Admin admin);

}
