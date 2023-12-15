package com.enigma.eprocurement.service.impl;

import com.enigma.eprocurement.entity.Category;
import com.enigma.eprocurement.repository.CategoryRepository;
import com.enigma.eprocurement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
private final CategoryRepository categoryRepository;
    @Override
    public Category getOrSave(Category category) {
        Optional<Category> existingCategory = categoryRepository.findById(category.getId());

        return existingCategory.orElseGet(() -> categoryRepository.save(category));
    }
}
