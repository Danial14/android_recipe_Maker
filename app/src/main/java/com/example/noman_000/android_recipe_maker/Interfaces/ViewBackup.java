package com.example.noman_000.android_recipe_maker.Interfaces;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewBackup {
    void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes, boolean status);
}
