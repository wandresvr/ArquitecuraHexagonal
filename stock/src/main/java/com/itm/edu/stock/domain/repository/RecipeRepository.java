package com.itm.edu.stock.domain.repository;

import com.itm.edu.stock.domain.entities.Recipe;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository {
    Recipe save(Recipe recipe);
    Optional<Recipe> findById(UUID id);
    List<Recipe> findAll();
    void deleteById(UUID id);
    List<Recipe> findByDifficulty(String difficulty);
} 