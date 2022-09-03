package com.mycompany.app.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="FOOD",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"product_name"}))
public class Food {

    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "measurement_unit")
    private String measurementUnit;

    @Column(name = "stock_quantity")
    private double stockQuantity;

    @OneToMany(mappedBy = "food")
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

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Set<FoodRecipe> getFoodRecipes() {
        return foodRecipes;
    }

    public void setFoodRecipes(Set<FoodRecipe> foodRecipes) {
        this.foodRecipes = foodRecipes;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "Food{" +
                "productName='" + productName + '\'' +
                ", measurementUnit='" + measurementUnit + '\'' +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
