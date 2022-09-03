package com.mycompany.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "FoodRecipes",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"product_id", "recipe_id"}))
public class FoodRecipe {

    @Id
    @GeneratedValue
    @Column(name = "FOOD_RECIPE_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(name = "quantity")
    private double quantity;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
