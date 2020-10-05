package com.example.noman_000.android_recipe_maker.DataModels;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

public class Recipe_Detail implements Serializable {
    private static final long serialVersionUID= 1L;
    private String id;
    private String recipeTitle;
    private String cookingMethod;
    private List<String> ingredients;
    private String serving;
    private String category;
    private Bitmap servingTwo;
    public Recipe_Detail(){

    }

    public Bitmap getServingTwo() {
        return servingTwo;
    }

    public void setServingTwo(Bitmap servingTwo) {
        this.servingTwo = servingTwo;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getCookingMethod() {
        return cookingMethod;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getServing() {
        return serving;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCookingMethod(String cookingMethod) {
        this.cookingMethod = cookingMethod;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public String toString() {
        return id + "!!axz" + recipeTitle + "!!axz" + cookingMethod + "!!axz" + ingredients.toString() +
                "!!axz" + serving + "!!axz" + category;
    }
    public String getCaption(){
        return "Recipe title : " + recipeTitle + "\n" + "Cooking Method : " + cookingMethod + "\n" + "Ingredients : " + ingredients.toString();
    }
}
