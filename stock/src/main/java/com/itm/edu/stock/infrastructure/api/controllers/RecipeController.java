package com.itm.edu.stock.infrastructure.api.controllers;

import com.itm.edu.stock.application.ports.input.RecipeUseCase;
import com.itm.edu.stock.infrastructure.api.dto.CreateRecipeRequestDto;
import com.itm.edu.stock.infrastructure.api.dto.RecipeResponseDto;
import com.itm.edu.stock.infrastructure.api.dto.ErrorResponse;
import com.itm.edu.stock.infrastructure.api.dto.UpdateRecipeRequestDto;
import com.itm.edu.stock.infrastructure.api.mapper.RecipeApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
@Tag(name = "Recetas", description = "API para la gestión de recetas")
public class RecipeController {

    private final RecipeUseCase recipeUseCase;
    private final RecipeApiMapper recipeApiMapper;

    @Operation(summary = "Crear una nueva receta", description = "Crea una nueva receta con los ingredientes especificados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Receta creada exitosamente",
            content = @Content(schema = @Schema(implementation = RecipeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de la receta inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RecipeResponseDto> createRecipe(@Valid @RequestBody CreateRecipeRequestDto request) {
        var command = recipeApiMapper.toCommand(request);
        var response = recipeUseCase.createRecipe(command);
        return ResponseEntity.ok(recipeApiMapper.toDto(response));
    }

    @Operation(summary = "Obtener todas las recetas", description = "Retorna una lista de todas las recetas disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de recetas obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = RecipeResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipes() {
        var responses = recipeUseCase.getAllRecipes();
        var dtos = responses.stream()
                .map(recipeApiMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una receta por ID", description = "Retorna una receta específica basada en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = RecipeResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDto> getRecipeById(@PathVariable UUID id) {
        var response = recipeUseCase.getRecipeById(id);
        return ResponseEntity.ok(recipeApiMapper.toDto(response));
    }

    @Operation(summary = "Actualizar una receta", description = "Actualiza una receta existente con nuevos datos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = RecipeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de la receta inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponseDto> updateRecipe(
            @Parameter(description = "ID de la receta", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRecipeRequestDto request) {
        var command = recipeApiMapper.toCommand(request);
        var response = recipeUseCase.updateRecipe(id, command);
        return ResponseEntity.ok(recipeApiMapper.toDto(response));
    }

    @Operation(summary = "Obtener recetas por dificultad", description = "Retorna una lista de recetas filtradas por nivel de dificultad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de recetas obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = RecipeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Nivel de dificultad inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByDifficulty(@PathVariable String difficulty) {
        var responses = recipeUseCase.getRecipesByDifficulty(difficulty);
        var dtos = responses.stream()
                .map(recipeApiMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Eliminar una receta", description = "Elimina una receta específica basada en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Receta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        recipeUseCase.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
} 