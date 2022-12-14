package com.mycompany.app.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="RECIPES")
public class Recipe {
    @Id
    @GeneratedValue
    @Column(name = "recipe_id")
    private long id;

    @Column(name = "recipe_name")
    private String recipeName;

    @OneToMany(mappedBy = "recipe")
    private Set<FoodRecipe> foodRecipes = new HashSet<FoodRecipe>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Set<FoodRecipe> getFoodRecipes() {
        return foodRecipes;
    }

    public void setFoodRecipes(Set<FoodRecipe> foodRecipes) {
        this.foodRecipes = foodRecipes;
    }
}
