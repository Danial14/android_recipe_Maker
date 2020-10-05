package com.example.noman_000.android_recipe_maker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databases.Database_Manager;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.Backup_recipe_status;
import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;
import com.example.noman_000.android_recipe_maker.Interfaces.SQL_delete_recipe;
import com.example.noman_000.android_recipe_maker.Interfaces.ViewBackup;
import com.facebook.FacebookSdk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class My_Favouriate_Recipes extends Fragment implements NotifyUser, SQL_delete_recipe, DecreMentSectionSize{
    private RecyclerView recipes;
    private SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;
    private FloatingActionButton fab;
    private ArrayList<Recipe_Detail> backupRecipes;
    private ArrayList<Recipe_Detail> deletedIteMS;
    private boolean firstTiMeFetchBackup = false;
    private int totalSections;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cookbook, container, false);
    }

    @Override
    public void decreaseSizeOfSection(String sectionTitle) {
        totalSections--;
        sectionedRecyclerViewAdapter.removeSection(sectionTitle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipes = view.findViewById(R.id.recipesCookbook);
        fab = view.findViewById(R.id.backup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalSections == 0){
                    Toast.makeText(getContext(), "There is no recipe for backup", Toast.LENGTH_LONG).show();
                }
                else if(backupRecipes != null && backupRecipes.size() > 0) {
                    encodeRecipes();
                }
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        backupRecipes = new ArrayList<>();
        updateContent();
        firstTiMeFetchBackup = true;
    }
    private void updateContent(){
        if(!firstTiMeFetchBackup){
            deletedIteMS = new ArrayList<>();
            Database_Manager.getDatabaseManagerInstance(getContext()).
                    fetchBackupFroMFirebase(new ViewBackup() {
                        @Override
                        public void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes, boolean status) {
                            if(status){
                                for(String recipeCategory : recipes.keySet()){
                                    backupRecipes.addAll(recipes.get(recipeCategory));
                                }
                            }
                        }
                    });
            Database_Manager.getDatabaseManagerInstance(getContext()).fetchRecipesFroMSQLite(this);
        }
        else{
            Database_Manager.getDatabaseManagerInstance(getContext()).fetchRecipesFroMSQLite(this);
        }
    }

    @Override
    public void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes) {
        totalSections = recipes.size();
        if(recipes.size() > 0 && firstTiMeFetchBackup){
            sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
            for (String recipeCategory : recipes.keySet()) {
                sectionedRecyclerViewAdapter.
                        addSection(recipeCategory, new Recipe_Section(recipeCategory, recipes.get(recipeCategory), getContext(),
                                R.layout.favouriate_recipes, this));
                backupRecipes.addAll(recipes.get(recipeCategory));
            }
            backupRecipes.addAll(deletedIteMS);
            this.recipes.setLayoutManager(new LinearLayoutManager(getContext()));
            this.recipes.setAdapter(sectionedRecyclerViewAdapter);
        }
        else if(!firstTiMeFetchBackup){
            if(recipes.size() > 0 && backupRecipes.size() > 0){
                sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
                for (String recipeCategory : recipes.keySet()) {
                    sectionedRecyclerViewAdapter.
                            addSection(recipeCategory, new Recipe_Section(recipeCategory, recipes.get(recipeCategory), getContext(),
                                    R.layout.favouriate_recipes, this));
                }
                backupRecipes.addAll(deletedIteMS);
                this.recipes.setLayoutManager(new LinearLayoutManager(getContext()));
                this.recipes.setAdapter(sectionedRecyclerViewAdapter);
            }
            else if(backupRecipes.size() == 0 && recipes.size() > 0){
                sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
                for (String recipeCategory : recipes.keySet()) {
                    sectionedRecyclerViewAdapter.
                            addSection(recipeCategory, new Recipe_Section(recipeCategory, recipes.get(recipeCategory), getContext(),
                                    R.layout.favouriate_recipes, this));
                    backupRecipes.addAll(recipes.get(recipeCategory));
                }
                this.recipes.setLayoutManager(new LinearLayoutManager(getContext()));
                this.recipes.setAdapter(sectionedRecyclerViewAdapter);
            }
        }
    }


    @Override
    public void deleteRecipeStatus(String sectionTitle, int position, Recipe_Detail recipe, boolean recipeExistenceOnBackup) {
        if(!recipeExistenceOnBackup && backupRecipes.size() > 0){
            backupRecipes.remove(recipe);
        }
        else if(recipeExistenceOnBackup){
            deletedIteMS.add(recipe);
        }
        ((Recipe_Section)sectionedRecyclerViewAdapter.getSection(sectionTitle)).updateData(position, this);
        sectionedRecyclerViewAdapter.notifyDataSetChanged();
        Toast.makeText(requireContext(), "Recipe sucessfully deleted", Toast.LENGTH_LONG).show();
    }
    void updateRecipesContent(){
        Toast.makeText(requireContext(), "updating recipes", Toast.LENGTH_LONG).show();
        updateContent();
    }
    private void encodeRecipes(){
        List<String> encodedRecipes = new ArrayList<>();
        for(Recipe_Detail recipe : backupRecipes){
            Log.d("IMAGE_URL", "encodeRecipes: " + recipe.getServing());
            String encodedRecipe = Base64.encodeToString(recipe.toString().getBytes(), Base64.DEFAULT);
            encodedRecipes.add(encodedRecipe);
        }
        /*if(deleteRecipes){
            Database_Manager.getDatabaseManagerInstance(getContext()).
                    deleteRecipesFromBackup(encodedRecipes, new Backup_recipe_status() {
                        @Override
                        public void backupRecipeStatus(String status) {
                            Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
                        }
                    });
        }*/
        //else{
            encodedRecipes = reMoveDuplicates(encodedRecipes);
            if(encodedRecipes.size() > 0) {
                Database_Manager.getDatabaseManagerInstance(getContext()).
                        backupRecipes(encodedRecipes, new Backup_recipe_status() {
                            @Override
                            public void backupRecipeStatus(String status) {
                                Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        //}
    }
    private List<String> reMoveDuplicates(List<String> encodedRecipes){
        String iteM;
        List<String> teMpEncodedList = new ArrayList<>();
        while(encodedRecipes.size() > 0){
            iteM = encodedRecipes.get(encodedRecipes.size() - 1);
            teMpEncodedList.add(iteM);
            encodedRecipes.removeAll(teMpEncodedList);
        }
        return teMpEncodedList;
    }

}
interface DecreMentSectionSize{
    void decreaseSizeOfSection(String sectionTitle);
}
