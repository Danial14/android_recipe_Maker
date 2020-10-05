package com.example.noman_000.android_recipe_maker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.noman_000.android_recipe_maker.Adapters.Ingredients_Adapter;
import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;
import com.example.noman_000.android_recipe_maker.Interfaces.OnIngredientsDownloaded;
import com.example.noman_000.android_recipe_maker.Interfaces.SetCheckbox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databases.Database_Manager;

public class Ingredients_List_Search extends Fragment implements OnIngredientsDownloaded, NotifyUser, SetCheckbox {
    private FloatingActionButton fab;
    private RecyclerView breakfastIngredientsListContainer;
    private RecyclerView lunchIngredientsListContainer;
    private RecyclerView dinnerIngredientsListContainer;
    private RecyclerView appetizersIngredientsListContainer;
    private RecyclerView saladIngredientsListContainer;
    private Context context;
    private boolean unCheckCheckBoxes;
    private ArrayList<String> breakfastIngredients;
    private ArrayList<String> dinnerIngredients;
    private ArrayList<String> lunchIngredients;
    private ArrayList<String> appetizersIngredients;
    private ArrayList<String> saladIngredients;
    private ArrayList<CompoundButton> checkboxes;
    public static HashMap<String, ArrayList<String>> userSelection;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredients_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = view.findViewById(R.id.search_recipe);
        dinnerIngredientsListContainer = view.findViewById(R.id.dinnerIngredients);
        lunchIngredientsListContainer = view.findViewById(R.id.lunchIngredients);
        breakfastIngredientsListContainer = view.findViewById(R.id.breakfastIngredients);
        appetizersIngredientsListContainer = view.findViewById(R.id.apptizersIngredients);
        saladIngredientsListContainer = view.findViewById(R.id.saladIngredients);
        saladIngredientsListContainer.setLayoutManager(new LinearLayoutManager(context));
        breakfastIngredientsListContainer.setLayoutManager(new LinearLayoutManager(context));
        appetizersIngredientsListContainer.setLayoutManager(new LinearLayoutManager(context));
        dinnerIngredientsListContainer.setLayoutManager(new LinearLayoutManager(context));
        lunchIngredientsListContainer.setLayoutManager(new LinearLayoutManager(context));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSelection.size() > 0) {
                    Database_Manager.getDatabaseManagerInstance(context).searchRecipes(userSelection, Ingredients_List_Search.this);
                    unCheckCheckBoxes = true;
                }
            }
        });
        userSelection = new HashMap<>();
        checkboxes = new ArrayList<>();
        if(savedInstanceState == null) {
            Log.d("Ingre list", "onViewCreated: null");
            Database_Manager.getDatabaseManagerInstance(context).fetchIngredients(this);
        }
        else{
            Log.d("Ingre list", "onViewCreated: not null");
           breakfastIngredients = savedInstanceState.getStringArrayList("breakfast ing");
           saladIngredients = savedInstanceState.getStringArrayList("salad ing");
           appetizersIngredients = savedInstanceState.getStringArrayList("appetizers ing");
           dinnerIngredients = savedInstanceState.getStringArrayList("dinner ing");
           lunchIngredients = savedInstanceState.getStringArrayList("lunch ing");
           updateView();
        }
    }


    @Override
    public void onBreakfastIngredientsDownloaded(ArrayList<String> breakfastIngredients) {
        this.breakfastIngredients = breakfastIngredients;
        breakfastIngredientsListContainer.setAdapter(new Ingredients_Adapter(breakfastIngredients, context, this));
    }
    @Override
    public void onDinnerIngredientsDownloaded(ArrayList<String> dinnerIngredients) {
        this.dinnerIngredients = dinnerIngredients;
        dinnerIngredientsListContainer.setAdapter(new Ingredients_Adapter(dinnerIngredients, context, this));
    }
    @Override
    public void onAppetizersIngredientsDownloaded(ArrayList<String> appetizersIngredients) {
        this.appetizersIngredients = appetizersIngredients;
        appetizersIngredientsListContainer.setAdapter(new Ingredients_Adapter(appetizersIngredients, context, this));
    }

    @Override
    public void onLunchIngredientsDownloaded(ArrayList<String> lunchIngredients) {
        this.lunchIngredients = lunchIngredients;
        lunchIngredientsListContainer.setAdapter(new Ingredients_Adapter(lunchIngredients, context, this));
    }

    @Override
    public void onSaladIngredientsDownloaded(ArrayList<String> saladIngredients) {
        this.saladIngredients = saladIngredients;
        saladIngredientsListContainer.setAdapter(new Ingredients_Adapter(saladIngredients, context, this));
    }
    @Override
    public void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes) {
        Intent intent = new Intent(context, Recipes.class);
        intent.putExtra("Recipes", recipes);
        startActivity(intent);
        uncheckCheckboxes();
    }
    private void uncheckCheckboxes(){
        if(unCheckCheckBoxes){
            for(final CompoundButton compoundButton : checkboxes){
                compoundButton.post(new Runnable() {
                    @Override
                    public void run() {
                        compoundButton.setChecked(false);
                    }
                });
            }
        }
        unCheckCheckBoxes = false;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("breakfast ing", breakfastIngredients);
        outState.putStringArrayList("salad ing", saladIngredients);
        outState.putStringArrayList("appetizers ing", appetizersIngredients);
        outState.putStringArrayList("dinner ing", dinnerIngredients);
        outState.putStringArrayList("lunch ing", lunchIngredients);
    }

    @Override
    public void setCheckBox(CompoundButton checkbox) {
        checkboxes.add(checkbox);
    }

    @Override
    public void reMoveCheckbox(CompoundButton checkbox) {
        checkboxes.remove(checkbox);
    }

    private void updateView(){
        breakfastIngredientsListContainer.setAdapter(new Ingredients_Adapter(breakfastIngredients, context, this));
        dinnerIngredientsListContainer.setAdapter(new Ingredients_Adapter(dinnerIngredients, context, this));
        appetizersIngredientsListContainer.setAdapter(new Ingredients_Adapter(appetizersIngredients, context, this));
        lunchIngredientsListContainer.setAdapter(new Ingredients_Adapter(lunchIngredients, context, this));
        saladIngredientsListContainer.setAdapter(new Ingredients_Adapter(saladIngredients, context, this));
    }
}
