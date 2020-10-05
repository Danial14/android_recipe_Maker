package com.example.noman_000.android_recipe_maker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import databases.Database_Manager;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchRecipe extends AppCompatActivity implements NotifyUser {

    private SearchView searchView;
    private ProgressBar progressBar;
    private Database_Manager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);
        progressBar = findViewById(R.id.progressBar);
        databaseManager = Database_Manager.getDatabaseManagerInstance(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.searchRecipe).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                databaseManager.setCounterLiMit(0);
                databaseManager.setCounter(0);
                List<String> list = null;
                if(query.matches("^[a-zA-Z ]+(,[a-zA-Z ]+)+$")){
                    progressBar.setVisibility(View.VISIBLE);
                    list = Arrays.asList(query.split(","));
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, reMoveAdditionalSpaces(list.get(i)).toLowerCase());
                        Log.d("clear ", list.get(i));
                    }
                    if(list.size() <= 10) {
                        Log.d("counter liMit ", "onQueryTextSubmit: " + "5");
                        databaseManager.setCounterLiMit(5);
                        databaseManager.searchRecipes(list, SearchRecipe.this);
                    }
                    else{
                        int start = 0;
                        int end = 10;
                        int listSize = list.size();
                        List<String> teMpList = null;
                        if(listSize % 10 != 0){
                            databaseManager.setCounterLiMit(5 * (listSize / 10 + 1));
                            Log.d("counter liMit ", "onQueryTextSubmit: " + "5 <");
                            for(int i = 0; i <= listSize / 10; i++){
                                if(listSize > end){
                                    teMpList = list.subList(start, end);
                                    start = end;
                                    end += 10;
                                }
                                else{
                                    teMpList = list.subList(start, listSize);
                                }
                                databaseManager.searchRecipes(teMpList, SearchRecipe.this);
                            }
                        }
                        else{
                            databaseManager.setCounterLiMit(5 * listSize / 10);
                            for(int i = 0; i < listSize / 10; i++){
                                teMpList = list.subList(start, end);
                                start = end;
                                end += 10;
                                databaseManager.searchRecipes(teMpList, SearchRecipe.this);
                            }
                        }
                    }
                }
                else if(query.matches("^[a-zA-Z ]+$")){
                    progressBar.setVisibility(View.VISIBLE);
                    list = new ArrayList<>();
                    list.add(reMoveAdditionalSpaces(query).toLowerCase());
                    databaseManager.setCounterLiMit(5);
                    databaseManager.searchRecipes(list, SearchRecipe.this);
                    Log.d("query", "onQueryTextSubmit: zero space " + query);
                }
                else{
                    Toast.makeText(SearchRecipe.this, "Your query forMat is invalid", Toast.LENGTH_LONG).
                            show();
                    if(progressBar.getVisibility() == View.VISIBLE){
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("queryTextChanged", newText);
                return true;
            }
        });
        return true;
    }

    private void handleVoiceSearch(Intent intent){
        if(intent != null && "android.intent.action.SEARCH".equals(intent.getAction())){

            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("Search recipe", "onNewIntent: " + query);
            databaseManager.setCounter(0);
            databaseManager.setCounterLiMit(5);
            List<String> list = Arrays.asList(query.split(" "));
            if(list.size() <= 10) {
                databaseManager.searchRecipes(list, SearchRecipe.this);
            }
            else{
                List<String> teMpList = list.subList(0, 10);
                databaseManager.searchRecipes(teMpList, SearchRecipe.this);
            }
            searchView.clearFocus();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleVoiceSearch(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes) {
        if(recipes.size() > 0) {
            Intent intent = new Intent(this, Recipes.class);
            intent.putExtra("Recipes", recipes);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "No recipe found", Toast.LENGTH_LONG).show();
        }
    }
    private String reMoveAdditionalSpaces(String ingredient){
        ingredient = ingredient.trim();
        if(ingredient.contains("  ")){
            Pattern pattern = Pattern.compile("\\s+");
            Matcher matcher = pattern.matcher(ingredient);
            ingredient = matcher.replaceAll(" ");
        }
        return ingredient;
    }
}
