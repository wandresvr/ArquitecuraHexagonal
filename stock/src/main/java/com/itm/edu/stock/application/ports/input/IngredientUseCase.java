package com.itm.edu.stock.application.ports.input;

import com.itm.edu.stock.application.dto.CreateIngredientCommand;
import com.itm.edu.stock.application.dto.IngredientResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IngredientUseCase {
    IngredientResponse createIngredient(CreateIngredientCommand command);
    IngredientResponse getIngredientById(UUID id);
    List<IngredientResponse> getAllIngredients();
    IngredientResponse updateIngredient(UUID id, CreateIngredientCommand command);
    void deleteIngredient(UUID id);
    List<IngredientResponse> getIngredientsBySupplier(String supplier);
    void updateIngredientQuantity(UUID id, BigDecimal quantity);
} 