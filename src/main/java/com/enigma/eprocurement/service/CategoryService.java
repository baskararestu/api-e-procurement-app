package com.enigma.eprocurement.service;

import com.enigma.eprocurement.entity.Category;

public interface CategoryService {
    Category getOrSave (Category category);
}
