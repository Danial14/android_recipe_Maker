package databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;
import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.SQL_delete_recipe;
import com.example.noman_000.android_recipe_maker.Interfaces.SQLite_Recipe_Status;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class SQLite_Db_Helper implements RestoreBackup, DeleteRecipe {
    private SQLiteDatabase sqLiteDatabase;

    SQLite_Db_Helper(Context context){
        sqLiteDatabase = context.openOrCreateDatabase(TABLE.DB_NAME, Context.MODE_PRIVATE, null);
        TABLE.createTable(sqLiteDatabase);
    }

    private boolean checkRecipeExistence(Recipe_Detail recipe){
        Cursor result = sqLiteDatabase.query(TABLE.TABLE_NAME, new String[]{TABLE.RECIPE_TITLE, TABLE.USER_EMAIL}, TABLE.RECIPE_TITLE + "=?" + " AND " + TABLE.USER_EMAIL + "=?",
                new String[]{recipe.getRecipeTitle(), TABLE.eMail}, null, null, null);
        if(result.moveToNext()){
            result.close();
            return true;
        }
        result.close();
        return false;
    }
    void saveRecipe(Recipe_Detail recipe, SQLite_Recipe_Status sqLite_recipe_status, byte[] convertedIMage){
        if(checkRecipeExistence(recipe)){
            sqLite_recipe_status.saveRecipeStatus(false);
        }
        else{
            insertRecipe(recipe, convertedIMage);
            Log.d("IMage added", "saveRecipe: iMage successfully added to SQLite");
            sqLite_recipe_status.saveRecipeStatus(true);
        }
    }
    public void deleteRecipe(Recipe_Detail recipe, SQL_delete_recipe sql_delete_recipe, String sectionTitle, int position, boolean recipeExistenceOnBackup){
        sqLiteDatabase.delete(TABLE.TABLE_NAME, TABLE.ID + "=?" + " AND " + TABLE.USER_EMAIL + "=?", new String[]{recipe.getId(), TABLE.eMail});
        sql_delete_recipe.deleteRecipeStatus(sectionTitle, position, recipe, recipeExistenceOnBackup);
    }
    void fetchRecipes(NotifyUser notifyUser){
        HashMap<String, ArrayList<Recipe_Detail>> recipes = new HashMap<>();
        ArrayList<Recipe_Detail> breakfastRecipes = new ArrayList<>();
        ArrayList<Recipe_Detail> appetizersRecipes = new ArrayList<>();
        ArrayList<Recipe_Detail> dinnerRecipes = new ArrayList<>();
        ArrayList<Recipe_Detail> lunchRecipes = new ArrayList<>();
        ArrayList<Recipe_Detail> saladRecipes = new ArrayList<>();
        Cursor result = sqLiteDatabase.query(TABLE.TABLE_NAME, new String[]{TABLE.ID, TABLE.RECIPE_TITLE, TABLE.COOKING_METHOD,
                TABLE.INGREDIENTS, TABLE.SERVING, TABLE.IMAGE_URL, TABLE.CATEGORY}, TABLE.USER_EMAIL + "=?", new String[]{TABLE.eMail}, null, null, null);
        while(result.moveToNext()){
            Log.d("recipe found", "fetchRecipes: recipe found");
            String category = result.getString(result.getColumnIndex(TABLE.CATEGORY));
            Recipe_Detail recipe = new Recipe_Detail();
            switch(category){
                case "Salad":
                    if(recipes.containsKey(category)){
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        recipes.get(category).add(recipe);
                    }
                    else{
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        saladRecipes.add(recipe);
                        recipes.put(category, saladRecipes);
                    }
                    break;
                case "Lunch":
                    if(recipes.containsKey(category)){
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        recipes.get(category).add(recipe);
                    }
                    else{
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        lunchRecipes.add(recipe);
                        recipes.put(category, lunchRecipes);
                    }
                    break;
                case "Appetizers":
                    if(recipes.containsKey(category)){
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        recipes.get(category).add(recipe);
                    }
                    else{
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        appetizersRecipes.add(recipe);
                        recipes.put(category, appetizersRecipes);
                    }
                    break;
                case "Dinner":
                    if(recipes.containsKey(category)){
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        recipes.get(category).add(recipe);
                    }
                    else{
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        dinnerRecipes.add(recipe);
                        recipes.put(category, dinnerRecipes);
                    }
                    break;
                default:
                    if(recipes.containsKey(category)){
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        recipes.get(category).add(recipe);
                    }
                    else{
                        recipe.setCategory(category);
                        recipe.setId(result.getString(result.getColumnIndex(TABLE.ID)));
                        recipe.setRecipeTitle(result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE)));
                        recipe.setCookingMethod(result.getString(result.getColumnIndex(TABLE.COOKING_METHOD)));
                        recipe.setIngredients(Arrays.asList(result.getString(result.getColumnIndex(TABLE.INGREDIENTS)).split(",")));
                        byte[] iMage = result.getBlob(result.getColumnIndex(TABLE.SERVING));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(iMage, 0, iMage.length);
                        recipe.setServingTwo(bitmap);
                        recipe.setServing(result.getString(result.getColumnIndex(TABLE.IMAGE_URL)));
                        breakfastRecipes.add(recipe);
                        recipes.put(category, breakfastRecipes);
                    }
            }
        }
        result.close();
        notifyUser.searchRecipeStatus(recipes);
    }
    public void restoreRecipesBackup(List<Recipe_Detail> recipes){
        checkRecipesExistence(recipes);
        for (Recipe_Detail recipe : recipes) {
            Bitmap recipeIMage = recipe.getServingTwo();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            recipeIMage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] iMageBytes = byteArrayOutputStream.toByteArray();
            insertRecipe(recipe, iMageBytes);
            }
    }
    private void insertRecipe(Recipe_Detail recipe, byte[] convertedIMage){
        ContentValues values = new ContentValues();
        values.put(TABLE.ID, recipe.getId());
        values.put(TABLE.RECIPE_TITLE, recipe.getRecipeTitle());
        values.put(TABLE.COOKING_METHOD, recipe.getCookingMethod());
        values.put(TABLE.INGREDIENTS, Arrays.
                toString(recipe.getIngredients().toArray(new String[recipe.getIngredients().size()])));
        values.put(TABLE.SERVING, convertedIMage);
        values.put(TABLE.IMAGE_URL, recipe.getServing());
        values.put(TABLE.CATEGORY, recipe.getCategory());
        values.put(TABLE.USER_EMAIL, TABLE.eMail);
        sqLiteDatabase.insert(TABLE.TABLE_NAME, null, values);
    }
    private String generateQuery(int size){
        StringBuilder builder = new StringBuilder(TABLE.USER_EMAIL + "=?" + " and (" + TABLE.RECIPE_TITLE + "=?");
        for(int i = 0; i < size - 1; i++){
            builder.append(" or " + TABLE.RECIPE_TITLE + "=?");
        }
        builder.append(")");
        return builder.toString();
    }
    private void checkRecipesExistence(List<Recipe_Detail> recipes){
        String query = generateQuery(recipes.size());
        String[] selectionArgs = new String[recipes.size() + 1];
        selectionArgs[0] = TABLE.eMail;
        for(int i = 1; i < selectionArgs.length; i++){
            selectionArgs[i] = recipes.get(i - 1).getRecipeTitle();
        }
        Cursor result = sqLiteDatabase.query(TABLE.TABLE_NAME, new String[]{TABLE.RECIPE_TITLE}, query, selectionArgs, null, null, null);
        while(result.moveToNext()){
            String recipeTitle = result.getString(result.getColumnIndex(TABLE.RECIPE_TITLE));
            for(Recipe_Detail recipe : recipes){
                if(recipe.getRecipeTitle().equals(recipeTitle)){
                    recipes.remove(recipe);
                    break;
                }
            }
        }
        result.close();
    }
     static class TABLE{
        private static final String DB_NAME = "favouriate_recipes.db";
        private static final String TABLE_NAME = "My_favouriate_recipes";
        private static final String ID = "ID";
        private static final String RECIPE_TITLE = "recipetitle";
        private static final String COOKING_METHOD = "cookingMethod";
        private static final String INGREDIENTS = "ingredients";
        private static final String SERVING = "serving";
        private static final String CATEGORY = "category";
        private static final String USER_EMAIL = "eMail";
        private static final String IMAGE_URL = "iMageUrl";
        private static String eMail;
        private static void createTable(SQLiteDatabase sqLiteDatabase){
            String sqlTable = "CREATE TABLE IF NOT EXISTS "  + TABLE_NAME +
                    "(record INTEGER PRIMARY KEY AUTOINCREMENT,ID TEXT, recipetitle TEXT, cookingMethod TEXT, ingredients TEXT, serving BLOB NOT NULL, iMageUrl TEXT, category TEXT, eMail TEXT);";
            sqLiteDatabase.execSQL(sqlTable);
        }

        public static void seteMail(String eMail) {
            TABLE.eMail = eMail;
        }
    }

}
interface RestoreBackup{
    void restoreRecipesBackup(List<Recipe_Detail> recipes);
}
interface DeleteRecipe{
    void deleteRecipe(Recipe_Detail recipe, SQL_delete_recipe sql_delete_recipe, String sectionTitle, int position, boolean recipeExistenceOnBackup);
}
