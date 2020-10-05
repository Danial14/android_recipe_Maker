package com.example.noman_000.android_recipe_maker.Interfaces;

import java.util.ArrayList;

public interface OnIngredientsDownloaded {
    void onBreakfastIngredientsDownloaded(ArrayList<String> breakfastIngredients);
    void onDinnerIngredientsDownloaded(ArrayList<String> dinnerIngredients);
    void onAppetizersIngredientsDownloaded(ArrayList<String> appetizersIngredients);
    void onLunchIngredientsDownloaded(ArrayList<String> lunchIngredients);
    void onSaladIngredientsDownloaded(ArrayList<String> saladIngredients);
}
