package com.example.noman_000.android_recipe_maker.Interfaces;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;

public interface SQL_delete_recipe {
    void deleteRecipeStatus(String sectionTitle, int position, Recipe_Detail recipe_detail, boolean recipeExistenceOnBackup);
}
