package com.example.RecipeSharing.controller;

import java.util.List;
import java.util.Optional;

import com.example.RecipeSharing.payloads.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.RecipeSharing.model.Recipe;
import com.example.RecipeSharing.repository.RecipeRepository;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);
    
    @Autowired
    RecipeRepository recipeRepository;

    @PostMapping("/add")
    private ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe){
         recipe.setId(null);
         Recipe saveRecipe= recipeRepository.save(recipe);
         return ResponseEntity.ok(saveRecipe);
    }

    @GetMapping("getAllRecipe")
    private List<Recipe> getAllRecipe(){
        logger.info("=== GET ALL RECIPES REQUEST ===");

        try {
            List<Recipe> recipes = recipeRepository.findAll();
            logger.info("Retrieved {} recipes from database", recipes.size());
            return recipes;
        } catch (Exception e) {
            logger.error("Error retrieving all recipes: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<Recipe> getRecipeById(@PathVariable String id){
        logger.info("=== GET RECIPE BY ID REQUEST ===");
        logger.info("Recipe ID: {}", id);

        try {
            Optional<Recipe> recipe = recipeRepository.findById(id);
            if (recipe.isPresent()) {
                logger.info("Recipe found: {}", recipe.get().getTitle());
                return ResponseEntity.ok(recipe.get());
            } else {
                logger.warn("Recipe not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving recipe by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/update/{id}")
    private ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe, @ModelAttribute("ValidateJwtToken") JwtResponse jwtResponse){
        logger.info("=== UPDATE RECIPE REQUEST ===");
        logger.info("Recipe ID: {}", id);
        logger.info("Updated recipe title: {}", updatedRecipe.getTitle());
        String userId=jwtResponse.getId();
        try {
            if(updatedRecipe.getUserId().equalsIgnoreCase(userId)) {
                return recipeRepository.findById(id)
                        .map(existing -> {
                            logger.info("Updating existing recipe: {}", existing.getTitle());
                            updatedRecipe.setId(id);
                            Recipe saveRecipe = recipeRepository.save(updatedRecipe);
                            logger.info("Recipe updated successfully");
                            return ResponseEntity.ok(saveRecipe);
                        }).orElseGet(() -> {
                            logger.warn("Recipe not found for update with ID: {}", id);
                            return ResponseEntity.notFound().build();
                        });
            }
            else{
                logger.warn("You are not the correct user to update this recipe");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            logger.error("Error updating recipe with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deleteRecipe(@PathVariable String id, @ModelAttribute JwtResponse jwtResponse){
        logger.info("=== DELETE RECIPE REQUEST ===");
        logger.info("Recipe ID: {}", id);

        try {
            if(jwtResponse.getId().equalsIgnoreCase(recipeRepository.findRecipeById(id).getUserId())) {
                if (!recipeRepository.existsById(id)) {
                    logger.warn("Recipe not found for deletion with ID: {}", id);
                    return ResponseEntity.notFound().build();
                }

                recipeRepository.deleteById(id);
                logger.info("Recipe deleted successfully with ID: {}", id);
                return ResponseEntity.ok().build();
            }
            else{
                logger.info("You are not a right user to delete this recipe");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            logger.error("Error deleting recipe with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
