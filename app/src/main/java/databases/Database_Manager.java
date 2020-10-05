package databases;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.noman_000.android_recipe_maker.Interfaces.Backup_recipe_status;
import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;
import com.example.noman_000.android_recipe_maker.Interfaces.OnIngredientsDownloaded;
import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.SQL_delete_recipe;
import com.example.noman_000.android_recipe_maker.Interfaces.SQLite_Recipe_Status;
import com.example.noman_000.android_recipe_maker.Interfaces.ValidateLogin;
import com.example.noman_000.android_recipe_maker.Interfaces.ValidateUserSignup;
import com.example.noman_000.android_recipe_maker.Interfaces.ViewBackup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class Database_Manager {
    private Firebase_Helper_Class firebaseConnection;
    private SQLite_Db_Helper sqlLiteConnection;
    private static Database_Manager databaseManager;

    private Database_Manager(Context context){
        firebaseConnection = new Firebase_Helper_Class(context);
        sqlLiteConnection = new SQLite_Db_Helper(context);
    }
    public static Database_Manager getDatabaseManagerInstance(Context context){
        if(databaseManager == null){
            databaseManager = new Database_Manager(context);
        }
        return databaseManager;
    }
    public void registerUser(String email, String password, @NonNull ValidateUserSignup validateCallback){
        encodeUserEMailAndPassword(email, password, validateCallback, null);
    }
    public void userLogin(String email, String password, @NonNull ValidateLogin loginCallback){
        encodeUserEMailAndPassword(email, password, null, loginCallback);
    }
    public void fetchIngredients(OnIngredientsDownloaded onIngredientsDownloaded){
        firebaseConnection.fetchIngredients(onIngredientsDownloaded);
    }
    private void encodeUserEMailAndPassword(String email, String password,
                                            ValidateUserSignup validateUserSignup,
                                            ValidateLogin validateLogin){
        String encodedEMail = Base64.encodeToString(email.getBytes(), Base64.DEFAULT);
        String encodedPassword = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
        if(validateUserSignup != null){
            firebaseConnection.saveUser(encodedEMail, encodedPassword, validateUserSignup);
        }
        else if(validateLogin != null){
            firebaseConnection.loginUser(encodedEMail, encodedPassword, validateLogin);
        }
    }
    public void searchRecipes(List<String> ingredients, NotifyUser notifyUser){
        firebaseConnection.fetchRecipesFromAllCollection(ingredients);
        firebaseConnection.setNotifyUser(notifyUser);
    }
    public void searchRecipes(HashMap<String, ArrayList<String>> userSelection, NotifyUser notifyUser){
        ArrayList<String> ingredients = null;
        int listSize = 0;
        int counter = 0;
        List<String> teMpList = null;
        for(String key : userSelection.keySet()){
            listSize = userSelection.get(key).size();
            if(listSize % 10 != 0){
                counter += listSize / 10 + 1;
            }
            else{
                counter += listSize / 10;
            }
        }
        setCounterLiMit(counter);
        firebaseConnection.setNotifyUser(notifyUser);
        for (String key : userSelection.keySet()) {
            ingredients = userSelection.get(key);
            switch(key){
                case "Dinner ingredients":
                    divideList(key, ingredients);
                    break;
                case "Lunch ingredients":
                    divideList(key, ingredients);
                    break;
                case "Breakfast ingredients":
                    divideList(key, ingredients);
                    break;
                case "Appetizers ingredients":
                    divideList(key, ingredients);
                    break;
                default:
                    divideList(key, ingredients);
            }
        }
    }
    private void divideList(String recipeCategory, ArrayList<String> ingredients){
        int listSize = ingredients.size();
        int start = 0;
        int end = 0;
        List<String> teMpList;
        if(listSize % 10 != 0 && listSize > 10){
            for(int i = 0; i < listSize / 10 + 1; i++){
                if(listSize > end){
                    teMpList = ingredients.subList(start, end);
                    start = end;
                    end += 10;
                }
                else{
                    teMpList = ingredients.subList(start, listSize);
                }
                switch(recipeCategory){
                    case "Dinner ingredients":
                        firebaseConnection.fetchDinnerRecipes(teMpList);
                        break;
                    case "Lunch ingredients":
                        firebaseConnection.fetchLunchRecipes(teMpList);
                        break;
                    case "Breakfast ingredients":
                        firebaseConnection.fetchBreakfastRecipes(teMpList);
                        break;
                    case "Appetizers ingredients":
                        firebaseConnection.fetchAppetizersRecipes(teMpList);
                        break;
                    default:
                        firebaseConnection.fetchSaladRecipes(teMpList);
                }
            }
        }
        else{
            if(listSize <= 10){
                switch(recipeCategory){
                    case "Dinner ingredients":
                        firebaseConnection.fetchDinnerRecipes(ingredients);
                        break;
                    case "Lunch ingredients":
                        firebaseConnection.fetchLunchRecipes(ingredients);
                        break;
                    case "Breakfast ingredients":
                        firebaseConnection.fetchBreakfastRecipes(ingredients);
                        break;
                    case "Appetizers ingredients":
                        firebaseConnection.fetchAppetizersRecipes(ingredients);
                        break;
                    default:
                        firebaseConnection.fetchSaladRecipes(ingredients);
                }
            }
            else{
                for(int i = 0; i < listSize / 10; i++){
                    teMpList = ingredients.subList(start, end);
                    switch(recipeCategory){
                        case "Dinner ingredients":
                            firebaseConnection.fetchDinnerRecipes(teMpList);
                            break;
                        case "Lunch ingredients":
                            firebaseConnection.fetchLunchRecipes(teMpList);
                            break;
                        case "Breakfast ingredients":
                            firebaseConnection.fetchBreakfastRecipes(teMpList);
                            break;
                        case "Appetizers ingredients":
                            firebaseConnection.fetchAppetizersRecipes(teMpList);
                            break;
                        default:
                            firebaseConnection.fetchSaladRecipes(teMpList);
                    }
                    start = end;
                    end += 10;
                }
            }
        }
    }
    public void setCounterLiMit(int counterLiMit){
        firebaseConnection.setCounterLiMit(counterLiMit);
    }
    public void saveRecipesToSQLite(Recipe_Detail recipe_detail, SQLite_Recipe_Status sqLite_recipe_status, byte[] convertedIMage){
        Log.d("recipes", "saveRecipesToSQLite: " + recipe_detail.getId() + " " + recipe_detail.getRecipeTitle());
        sqlLiteConnection.saveRecipe(recipe_detail, sqLite_recipe_status, convertedIMage);
    }
    public void fetchRecipesFroMSQLite(NotifyUser notifyUser){
        sqlLiteConnection.fetchRecipes(notifyUser);
    }
    public void deleteRecipesFroMSqLite(String encodedRecipe, Recipe_Detail recipe_detail, SQL_delete_recipe sql_delete_recipe, String sectionTitle, int position){
        firebaseConnection.checkRecipeExistenceOnBackup(encodedRecipe, sqlLiteConnection, recipe_detail, sql_delete_recipe, sectionTitle, position);
    }
    public void fetchLatestRecipes(NotifyUser notifyUser){
        firebaseConnection.fetchLatestRecipes(notifyUser);
    }
    public void setCounter(int counter){
        firebaseConnection.setCounter(counter);
    }
    public void backupRecipes(List<String> recipes, Backup_recipe_status backup_recipe_status){
        firebaseConnection.backupRecipes(recipes, backup_recipe_status);
    }
    /*public void deleteRecipesFromBackup(List<String> recipes, Backup_recipe_status backup_recipe_status){
        firebaseConnection.deleteRecipeFromBackup(recipes, backup_recipe_status);
    }*/
    public void startListeningForUpdates(){
        firebaseConnection.startListeningForUpdates();
    }
    public void setEMail(String eMail){
        SQLite_Db_Helper.TABLE.seteMail(eMail);
    }
    public void fetchBackupFroMFirebase(ViewBackup viewBackup){
        firebaseConnection.fetchBackup(viewBackup);
    }
    public void restoreBackupFroMFirebase(Backup_recipe_status backup_recipe_status){
        firebaseConnection.restoreBackup(backup_recipe_status, sqlLiteConnection);
    }
}
