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
    private String productName;

    @OneToMany(mappedBy = "recipe")
    private Set<FoodRecipe> foodRecipes = new HashSet<FoodRecipe>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Set<FoodRecipe> getFoodRecipes() {
        return foodRecipes;
    }

    public void setFoodRecipes(Set<FoodRecipe> foodRecipes) {
        this.foodRecipes = foodRecipes;
    }
}
