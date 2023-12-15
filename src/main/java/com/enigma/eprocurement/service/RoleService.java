package com.enigma.eprocurement.service;

import com.enigma.eprocurement.entity.Role;

public interface RoleService {
    Role getOrSave(Role role);
}
