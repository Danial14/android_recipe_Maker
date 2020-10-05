package com.example.noman_000.android_recipe_maker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databases.Database_Manager;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import android.os.Bundle;
import android.util.Log;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;

import java.util.ArrayList;
import java.util.HashMap;

public class Latest_Recipe extends AppCompatActivity implements NotifyUser {
    private RecyclerView recyclerView;
    private SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest__recipe);
        recyclerView = findViewById(R.id.latest_ingredients_recipe);
        recyclerView.setNestedScrollingEnabled(false);
        Database_Manager.getDatabaseManagerInstance(this).fetchLatestRecipes(this);

    }

    @Override
    public void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes) {
        if(recipes != null){
            Log.d("Recipes", "onCreate: recipes not null");
            sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
            for(String recipeCategory : recipes.keySet()){
                sectionedRecyclerViewAdapter.
                        addSection(new Recipe_Section(recipeCategory, recipes.get(recipeCategory), this,
                                true));
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
    }
}
