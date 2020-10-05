package com.example.noman_000.android_recipe_maker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;

import java.util.ArrayList;
import java.util.HashMap;


public class Recipes extends AppCompatActivity{
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        recyclerView = findViewById(R.id.recipesContainer);
        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = null;
        Intent intent = getIntent();
        if(intent != null){
            Log.d("Recipes", "onCreate: intent not null");
            HashMap<String, ArrayList<Recipe_Detail>> recipes =
                    (HashMap<String, ArrayList<Recipe_Detail>>) intent.
                            getSerializableExtra("Recipes");
            if(recipes != null){
                Log.d("Recipes", "onCreate: recipes not null");
                if(intent.getBooleanExtra("View backup", false)){
                    sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
                    for (String recipeCategory : recipes.keySet()) {
                        sectionedRecyclerViewAdapter.
                                addSection(new Recipe_Section(recipeCategory, recipes.get(recipeCategory), this, 1));
                    }
                }
                else {
                    sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
                    for (String recipeCategory : recipes.keySet()) {
                        sectionedRecyclerViewAdapter.
                                addSection(new Recipe_Section(recipeCategory, recipes.get(recipeCategory), this));
                    }
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
    }
}
