package com.example.noman_000.android_recipe_maker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noman_000.android_recipe_maker.Adapters.Recipe_ing_adapter;
import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.SQL_delete_recipe;
import com.example.noman_000.android_recipe_maker.Interfaces.SQLite_Recipe_Status;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databases.Database_Manager;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class Recipe_Section extends Section {
    private String title;
    private List<Recipe_Detail> recipes;
    boolean cookbook;
    boolean latest;
    int viewBackup;
    private SQL_delete_recipe sql_delete_recipe;
    static Context context;
    Recipe_Section(@NonNull String title, @NonNull List<Recipe_Detail> recipes, Context context, int viewBackup){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.view_backup)
                .headerResourceId(R.layout.recipe_category)
                .build());

        this.title = title;
        this.recipes = recipes;
        this.context = context;
        this.viewBackup = viewBackup;
    }
    Recipe_Section(@NonNull String title, @NonNull List<Recipe_Detail> recipes, Context context){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.recipe)
                .headerResourceId(R.layout.recipe_category)
                .build());

        this.title = title;
        this.recipes = recipes;
        this.context = context;
    }
    Recipe_Section(@NonNull String title, @NonNull List<Recipe_Detail> recipes, Context context,
                   int cookbookResource, SQL_delete_recipe sql_delete_recipe){
        super(SectionParameters.builder()
                .itemResourceId(cookbookResource)
                .headerResourceId(R.layout.recipe_category)
                .build());

        this.title = title;
        this.sql_delete_recipe = sql_delete_recipe;
        this.recipes = recipes;
        this.context = context;
        cookbook = true;
    }
    Recipe_Section(@NonNull String title, @NonNull List<Recipe_Detail> recipes, Context context,
                   boolean latest){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.late_reci)
                .headerResourceId(R.layout.recipe_category)
                .build());

        this.title = title;
        this.recipes = recipes;
        this.context = context;
        this.latest = latest;

    }
    void updateData(int position, DecreMentSectionSize decreMentSectionSize){
        recipes.remove(position);
        if(recipes.size() == 0){
            decreMentSectionSize.decreaseSizeOfSection(title);
        }
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new RecipeItemViewHolder(view, this);
    }

    private String MakeCookingStepS(String cookingMethod){
        StringBuilder builder = new StringBuilder();
        String[] StepS = cookingMethod.split("\\.");
        for(int i = 0; i < StepS.length; i++){
            builder.append("(").append(i + 1).append(") ").append(StepS[i]).append("\n\n");
            Log.d("StepS", "MakeCookingStepS: " + StepS[i]);
        }

        return builder.toString();
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final RecipeItemViewHolder recipeItemViewHolder = (RecipeItemViewHolder) viewHolder;
        final Recipe_Detail recipe = recipes.get(position);
        recipeItemViewHolder.recipeTitle.setText(recipe.getRecipeTitle());
        recipeItemViewHolder.cookingMethod.setText(MakeCookingStepS(recipe.getCookingMethod()));
        recipeItemViewHolder.ingredients.setHasFixedSize(true);
        recipeItemViewHolder.ingredients.setLayoutManager(new LinearLayoutManager(context));
        recipeItemViewHolder.ingredients.setAdapter(new Recipe_ing_adapter(recipe.getIngredients(),context));
        if(viewBackup == 1){
            Picasso.with(context).load(recipe.getServing()).
                    placeholder(R.drawable.placeholder).error(R.drawable.error).
                    into(recipeItemViewHolder.iMage);
        }
        else if(cookbook){
            recipeItemViewHolder.iMage.setImageBitmap(recipe.getServingTwo());
            recipeItemViewHolder.deleteRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Recipe_Detail recipe = recipes.get(position);
                    Database_Manager.getDatabaseManagerInstance(Recipe_Section.context).
                            deleteRecipesFroMSqLite(encodeRecipe(recipe), recipe, sql_delete_recipe, title, position);
                }
            });
            Log.d("onBindItem", "onBindItemViewHolder: iMage retrieved froM SQLite");
            recipeItemViewHolder.shareRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(recipe.getServing())).setQuote(recipe.getCaption())
                            .build();
                    if(ShareDialog.canShow(ShareLinkContent.class)){
                        Home_Page.shareDialog.show(content);
                    }
                    else{
                        Toast.makeText(context, "There is soMe probleM in sharing please check weather you have installed facebook app or not if facebook app is not installed than install it", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        else{
            Picasso.with(context).load(recipe.getServing()).
                    placeholder(R.drawable.placeholder).error(R.drawable.error).
                    into(recipeItemViewHolder.iMage);

            recipeItemViewHolder.saveRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byte[] convertedIMage = convertIMageToBytes(convertIMageToBitMap(recipeItemViewHolder));
                    Database_Manager.getDatabaseManagerInstance(Recipe_Section.context).
                            saveRecipesToSQLite(recipes.get(position), new SQLite_Recipe_Status() {
                                @Override
                                public void saveRecipeStatus(boolean status) {
                                    if (status) {
                                        Toast.makeText(context, "Recipe successfully saved", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Recipe already exists", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, convertedIMage);
                }
            });
        }
    }
    private byte[] convertIMageToBytes(Bitmap iMage){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        iMage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private Bitmap convertIMageToBitMap(RecipeItemViewHolder viewHolder){
        ImageView iMage = viewHolder.iMage;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) iMage.getDrawable();
        return bitmapDrawable.getBitmap();
    }
    private String encodeRecipe(Recipe_Detail recipe){
        return Base64.encodeToString(recipe.toString().getBytes(), Base64.DEFAULT);
    }

    @Override
    public int getContentItemsTotal() {
        return recipes.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new RecipeCategoryViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);
        RecipeCategoryViewHolder recipeCategoryViewHolder = (RecipeCategoryViewHolder) holder;
        recipeCategoryViewHolder.textView.setText(title);
        if(cookbook || latest){
            recipeCategoryViewHolder.textView.setTextColor(context.getResources().getColor(R.color.black));
        }
    }
}
class RecipeItemViewHolder extends RecyclerView.ViewHolder{
    TextView recipeTitle;
    TextView cookingMethod;
    ImageView iMage;
    RecyclerView ingredients;
    Button saveRecipe;
    Button deleteRecipe;
    Button shareRecipe;

    RecipeItemViewHolder(@NonNull View itemView, Recipe_Section recipe_section) {
        super(itemView);
        if(recipe_section.viewBackup == 1){
            recipeTitle = itemView.findViewById(R.id.viewbackup_title_recipe);
            cookingMethod = itemView.findViewById(R.id.viewbackup_cooking_Method);
            iMage = itemView.findViewById(R.id.viewbackup_serving_recipe);
            ingredients = itemView.findViewById(R.id.viewbackup_ingredients_recipe);
        }
        else if(recipe_section.cookbook){
            deleteRecipe = itemView.findViewById(R.id.recipe_delete);
            shareRecipe = itemView.findViewById(R.id.recipe_share);
            recipeTitle = itemView.findViewById(R.id.title_recipe);
            cookingMethod = itemView.findViewById(R.id.cooking_Method);
            iMage = itemView.findViewById(R.id.serving_recipe);
            ingredients = itemView.findViewById(R.id.ingredients_recipe);
        }
        else if(recipe_section.latest){
            recipeTitle = itemView.findViewById(R.id.late_title_recipe);
            cookingMethod = itemView.findViewById(R.id.late_cooking_Method);
            iMage = itemView.findViewById(R.id.late_serving_recipe);
            ingredients = itemView.findViewById(R.id.late_ingredients_recipe);
            saveRecipe = itemView.findViewById(R.id.save_late_recipe);
        }
        else {
            saveRecipe = itemView.findViewById(R.id.save);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            cookingMethod = itemView.findViewById(R.id.cookingMethod);
            iMage = itemView.findViewById(R.id.recipe_iMage);
            ingredients = itemView.findViewById(R.id.recipe_Ingredients);
        }

    }
}
class RecipeCategoryViewHolder extends RecyclerView.ViewHolder{
    final TextView textView;
    RecipeCategoryViewHolder(@NonNull View header){
        super(header);
        textView = header.findViewById(R.id.category);
    }
}
