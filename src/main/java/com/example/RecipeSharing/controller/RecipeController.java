package com.example.RecipeSharing.controller;

import com.example.RecipeSharing.model.Recipe;
import com.example.RecipeSharing.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    RecipeRepository recipeRepository;

    @PostMapping("/add")
    private ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe){
         recipe.setId(null);
         Recipe saveRecipe= recipeRepository.save(recipe);
         return ResponseEntity.ok(saveRecipe);
    }

    @GetMapping
    private List<Recipe> getAllRecipe(){
        return recipeRepository.findAll();
    }

    @GetMapping("/{id}")
    private ResponseEntity<Recipe> getRecipeById(@PathVariable String id){
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("/{id}")
    private ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe){
        return recipeRepository.findById(id).
                map(existing -> {
                    updatedRecipe.setId(id);
                    Recipe saveRecipe = recipeRepository.save(updatedRecipe);
                    return ResponseEntity.ok(saveRecipe);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteRecipe(@PathVariable String id){

        if(!recipeRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        recipeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
