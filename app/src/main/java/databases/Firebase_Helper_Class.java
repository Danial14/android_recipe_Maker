package databases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import com.example.noman_000.android_recipe_maker.Interfaces.Backup_recipe_status;
import com.example.noman_000.android_recipe_maker.Interfaces.NotifyUser;
import com.example.noman_000.android_recipe_maker.Interfaces.OnIngredientsDownloaded;
import com.example.noman_000.android_recipe_maker.R;
import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Recipes;
import com.example.noman_000.android_recipe_maker.Interfaces.SQL_delete_recipe;
import com.example.noman_000.android_recipe_maker.Interfaces.ValidateLogin;
import com.example.noman_000.android_recipe_maker.Interfaces.ValidateUserSignup;
import com.example.noman_000.android_recipe_maker.Interfaces.ViewBackup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

class Firebase_Helper_Class {
    private static final String CHANNEL_ID = "12";
    private static final int REQUEST_CODE = 11;
    private Context context;
    private FirebaseFirestore firestore;
    private String userDcumentId;
    private int counter;
    private int counterLiMit;
    private NotifyUser notifyUser;
    private HashMap<String, ArrayList<Recipe_Detail>> searchRecipes;
    private boolean breakfastPushNotificationEnabled;
    private boolean saladPushNotificationEnabled;
    private boolean appetizersPushNotificationEnabled;
    private boolean dinnerPushNotificationEnabled;
    private boolean lunchPushNotificationEnabled;
    private boolean firsttiMeLatestRecipesFetch = true;
    private int totalLunchRecipes;
    private int totalSaladRecipes;
    private int totalAppetizersRecipes;
    private int totalBreakfastRecipes;
    private int totalDinnerRecipes;
    private int totalLatestLunchRecipes;
    private int totalLatestSaladRecipes;
    private int totalLatestAppetizersRecipes;
    private int totalLatestDinnerRecipes;
    private int totalLatestBreakfastRecipes;
    Firebase_Helper_Class(Context context){
         firestore = FirebaseFirestore.getInstance();
         searchRecipes = new HashMap<>();
         this.context = context;
    }
    void saveUser(final String email, final String password, @NonNull final ValidateUserSignup validateUserSignup){
        firestore.collection("Users").whereEqualTo("eMail", email).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Map<String, Object> user = new HashMap<>();
                            user.put("eMail", email);
                            user.put("password", password);
                            user.put("favouriate recipes", new ArrayList<String>());
                            firestore.collection("Users").document().set(user).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                validateUserSignup.onSuccessfulSignup();
                                            }
                                            else{
                                                validateUserSignup.signupFailed();
                                            }
                                        }
                                    });
                        }
                        else{
                            validateUserSignup.userExists();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                validateUserSignup.signupFailed();
            }
        });

    }
    void loginUser(String email, final String password, @NonNull final ValidateLogin validateLogin){
        firestore.collection("Users").whereEqualTo("eMail", email).
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    validateLogin.incorrectEMailOrPassword();
                }
                else{
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        if(password.equals(documentSnapshot.get("password"))){
                            validateLogin.onSuccessfulLogin();
                            userDcumentId = documentSnapshot.getId();
                            Log.d("loginuser", "onSuccess: " + userDcumentId);
                        }
                        else{
                            validateLogin.incorrectEMailOrPassword();
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                validateLogin.loginFailed();
            }
        });
    }
    void fetchIngredients(final OnIngredientsDownloaded onIngredientsDownloaded){
        firestore.collection("Recipes ingredients").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    switch(documentSnapshot.getId()){
                        case "Breakfast ingredients" :
                            onIngredientsDownloaded.onBreakfastIngredientsDownloaded(
                                    sortIngredients(
                                            (ArrayList<String>) documentSnapshot.get("ingredients"))
                            );
                            break;
                        case "Appetizers ingredients" :
                            onIngredientsDownloaded.onAppetizersIngredientsDownloaded(
                                    sortIngredients(
                                            (ArrayList<String>) documentSnapshot.get("ingredients"))
                            );
                            break;
                        case "Salad ingredients" :
                            onIngredientsDownloaded.onSaladIngredientsDownloaded(
                                    sortIngredients((ArrayList<String>) documentSnapshot.get("ingredients")));
                            break;
                        case "Dinner ingredients" :
                            onIngredientsDownloaded.onDinnerIngredientsDownloaded(
                                    sortIngredients((ArrayList<String>) documentSnapshot.get("ingredients"))
                            );
                            break;
                        default :
                            onIngredientsDownloaded.onLunchIngredientsDownloaded(
                                    sortIngredients((ArrayList<String>) documentSnapshot.get("ingredients"))
                            );
                    }
                }
            }
        });
    }
    private ArrayList<String> sortIngredients(ArrayList<String> ingredients){
        HashSet<String> set = new HashSet<>(ingredients);
        return new ArrayList<>(set);
    }
    void fetchRecipesFromAllCollection(final List<String> ingredients){
        searchRecipes = new HashMap<>();
        fetchBreakfastRecipes(ingredients);
        fetchDinnerRecipes(ingredients);
        fetchAppetizersRecipes(ingredients);
        fetchLunchRecipes(ingredients);
        fetchSaladRecipes(ingredients);
    }
    void fetchBreakfastRecipes(final List<String> ingredients){
        firestore.collection("Breakfast recipes").
                whereArrayContainsAny("Match ingredients", ingredients).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                            if(!querySnapshot.isEmpty()){
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Recipe_Detail recipe = new Recipe_Detail();
                                    recipe.setCategory("Breakfast");
                                    recipe.setId(document.getId());
                                    recipe.setRecipeTitle((String) document.get("recipe title"));
                                    recipe.setIngredients((ArrayList<String>) document.get("ingredients"));
                                    recipe.setCookingMethod((String) document.get("cooking Method"));
                                    recipe.setServing((String) document.get("serving"));
                                    recipes.add(recipe);
                                }
                                if(!searchRecipes.containsKey("Breakfast recipes")) {
                                    searchRecipes.put("Breakfast recipes", recipes);
                                }
                                else{
                                    searchRecipes.get("Breakfast recipes").addAll(recipes);
                                }
                            }
                        }
                        updateCounter();
                    }
                });
    }
    void fetchDinnerRecipes(final List<String> ingredients){
        firestore.collection("Dinner recipes").
                whereArrayContainsAny("Match ingredients", ingredients).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                            if(!querySnapshot.isEmpty()){
                                for(DocumentSnapshot document : querySnapshot.getDocuments()){
                                    Recipe_Detail recipe = new Recipe_Detail();
                                    recipe.setId(document.getId());
                                    recipe.setCategory("Dinner");
                                    recipe.setRecipeTitle((String) document.get("recipe title"));
                                    recipe.setIngredients((ArrayList<String>) document.get("ingredients"));
                                    recipe.setCookingMethod((String) document.get("cooking Method"));
                                    recipe.setServing((String) document.get("serving"));
                                    recipes.add(recipe);
                                }
                                if(!searchRecipes.containsKey("Dinner recipes")) {
                                    searchRecipes.put("Dinner recipes", recipes);
                                }
                                else{
                                    searchRecipes.get("Dinner recipes").addAll(recipes);
                                }
                            }
                        }
                        updateCounter();
                    }
                });
    }
    void fetchAppetizersRecipes(final List<String> ingredients){
        firestore.collection("Appetizers recipes").
                whereArrayContainsAny("Match ingredients", ingredients).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                            if(!querySnapshot.isEmpty()){
                                for(DocumentSnapshot document : querySnapshot.getDocuments()){
                                    Recipe_Detail recipe = new Recipe_Detail();
                                    recipe.setId(document.getId());
                                    recipe.setCategory("Appetizers");
                                    recipe.setRecipeTitle((String) document.get("recipe title"));
                                    recipe.setIngredients((ArrayList<String>) document.get("ingredients"));
                                    recipe.setCookingMethod((String) document.get("cooking Method"));
                                    recipe.setServing((String) document.get("serving"));
                                    recipes.add(recipe);
                                }
                                if(!searchRecipes.containsKey("Appetizers recipes")) {
                                    searchRecipes.put("Appetizers recipes", recipes);
                                }
                                else{
                                    searchRecipes.get("Appetizers recipes").addAll(recipes);
                                }
                            }
                        }
                        updateCounter();
                    }
                });
    }
    void fetchLunchRecipes(final List<String> ingredients){
        firestore.collection("Lunch recipes").
                whereArrayContainsAny("Match ingredients", ingredients).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                            if(!querySnapshot.isEmpty()){
                                for(DocumentSnapshot document : querySnapshot.getDocuments()){
                                    Recipe_Detail recipe = new Recipe_Detail();
                                    recipe.setId(document.getId());
                                    recipe.setCategory("Lunch");
                                    recipe.setRecipeTitle((String) document.get("recipe title"));
                                    recipe.setIngredients((ArrayList<String>) document.get("ingredients"));
                                    recipe.setCookingMethod((String) document.get("cooking Method"));
                                    recipe.setServing((String) document.get("serving"));
                                    recipes.add(recipe);
                                }
                                if(!searchRecipes.containsKey("Lunch recipes")) {
                                    searchRecipes.put("Lunch recipes", recipes);
                                }
                                else{
                                    searchRecipes.get("Lunch recipes").addAll(recipes);
                                }
                            }
                        }
                        updateCounter();
                    }
                });
    }
    void fetchSaladRecipes(final List<String> ingredients){
        firestore.collection("Salad recipes").
                whereArrayContainsAny("Match ingredients", ingredients).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()){
                                ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                                for(DocumentSnapshot document : querySnapshot.getDocuments()){
                                    Recipe_Detail recipe = new Recipe_Detail();
                                    recipe.setId(document.getId());
                                    recipe.setCategory("Salad");
                                    recipe.setRecipeTitle((String) document.get("recipe title"));
                                    recipe.setIngredients((ArrayList<String>) document.get("ingredients"));
                                    recipe.setCookingMethod((String) document.get("cooking Method"));
                                    recipe.setServing((String) document.get("serving"));
                                    recipes.add(recipe);
                                }
                                if(!searchRecipes.containsKey("Salad recipes")) {
                                    searchRecipes.put("Salad recipes", recipes);
                                }
                                else{
                                    searchRecipes.get("Salad recipes").addAll(recipes);
                                }
                            }
                        }
                        updateCounter();
                    }
                });
    }
    private void updateCounter(){
        counter++;
        Log.d("counter ", "updateCounter: " + counter + " counter liMit " + counterLiMit);
        if(counter == counterLiMit){
            counter = 0;
            counterLiMit = 0;
            notifyUser.searchRecipeStatus(searchRecipes);
            searchRecipes = new HashMap<>();
        }
    }
    void setNotifyUser(NotifyUser notifyUser){
        this.notifyUser = notifyUser;
    }
    void setCounterLiMit(int counterLiMit){
        this.counterLiMit = counterLiMit;
    }

    void setCounter(int counter) {
        this.counter = counter;
    }

    void startListeningForUpdates(){
        firestore.collection("Lunch recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int totalRecipes = queryDocumentSnapshots.size();
                if(totalRecipes >= totalLunchRecipes && totalLunchRecipes > 0) {
                    Recipe_Detail recipe_detail = null;
                    ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot docu = doc.getDocument();
                        recipe_detail = new Recipe_Detail();
                        recipe_detail.setId(docu.getId());
                        recipe_detail.setCategory("Lunch");
                        recipe_detail.setRecipeTitle((String) docu.get("recipe title"));
                        recipe_detail.setIngredients((List<String>) docu.get("ingredients"));
                        recipe_detail.setServing((String) docu.get("serving"));
                        recipe_detail.setCookingMethod((String) docu.get("cooking Method"));
                        recipes.add(recipe_detail);
                        Log.d("docuMent changes", docu.getId());
                    }
                    setLunchPushNotificationEnabled(recipes);
                }
                else if(totalLunchRecipes == 0){
                    setLunchPushNotificationEnabled(null);
                }
                totalLunchRecipes = totalRecipes;
                if(totalRecipes == 0){
                    ++totalLunchRecipes;
                }
            }
        });
        firestore.collection("Salad recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int totalRecipes = queryDocumentSnapshots.size();
                if(totalRecipes >= totalSaladRecipes && totalSaladRecipes > 0) {
                    Recipe_Detail recipe_detail = null;
                    ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot docu = doc.getDocument();
                        recipe_detail = new Recipe_Detail();
                        recipe_detail.setId(docu.getId());
                        recipe_detail.setCategory("Salad");
                        recipe_detail.setRecipeTitle((String) docu.get("recipe title"));
                        recipe_detail.setIngredients((List<String>) docu.get("ingredients"));
                        recipe_detail.setServing((String) docu.get("serving"));
                        recipe_detail.setCookingMethod((String) docu.get("cooking Method"));
                        recipes.add(recipe_detail);
                        Log.d("docuMent changes", docu.getId());
                    }
                    setSaladPushNotificationEnabled(recipes);
                }
                else if(totalSaladRecipes == 0){
                    setSaladPushNotificationEnabled(null);
                }
                totalSaladRecipes = totalRecipes;
                if(totalRecipes == 0){
                    ++totalSaladRecipes;
                }
            }
        });
        firestore.collection("Appetizers recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int totalRecipes = queryDocumentSnapshots.size();
                if(totalRecipes >= totalAppetizersRecipes && totalAppetizersRecipes > 0) {
                    Recipe_Detail recipe_detail = null;
                    ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot docu = doc.getDocument();
                        recipe_detail = new Recipe_Detail();
                        recipe_detail.setId(docu.getId());
                        recipe_detail.setCategory("Appetizers");
                        recipe_detail.setRecipeTitle((String) docu.get("recipe title"));
                        recipe_detail.setIngredients((List<String>) docu.get("ingredients"));
                        recipe_detail.setServing((String) docu.get("serving"));
                        recipe_detail.setCookingMethod((String) docu.get("cooking Method"));
                        recipes.add(recipe_detail);
                        Log.d("docuMent changes", docu.getId());
                    }
                    setAppetizersPushNotificationEnabled(recipes);
                }
                else if(totalAppetizersRecipes == 0){
                    setAppetizersPushNotificationEnabled(null);
                }
                totalAppetizersRecipes = totalRecipes;
                if(totalRecipes == 0){
                    ++totalAppetizersRecipes;
                }
            }
        });
        firestore.collection("Dinner recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int totalRecipes = queryDocumentSnapshots.size();
                if(totalRecipes >= totalDinnerRecipes && totalDinnerRecipes > 0) {
                    Recipe_Detail recipe_detail = null;
                    ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot docu = doc.getDocument();
                        recipe_detail = new Recipe_Detail();
                        recipe_detail.setId(docu.getId());
                        recipe_detail.setCategory("Dinner");
                        recipe_detail.setRecipeTitle((String) docu.get("recipe title"));
                        recipe_detail.setIngredients((List<String>) docu.get("ingredients"));
                        recipe_detail.setServing((String) docu.get("serving"));
                        recipe_detail.setCookingMethod((String) docu.get("cooking Method"));
                        recipes.add(recipe_detail);
                        Log.d("docuMent changes", docu.getId());
                    }
                    setDinnerPushNotificationEnabled(recipes);
                }
                else if(totalDinnerRecipes == 0){
                    setDinnerPushNotificationEnabled(null);
                }
                totalDinnerRecipes = totalRecipes;
                if(totalRecipes == 0){
                    ++totalDinnerRecipes;
                }
            }
        });
        firestore.collection("Breakfast recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                assert queryDocumentSnapshots != null;
                int totalRecipes = queryDocumentSnapshots.size();
                if(totalRecipes >= totalBreakfastRecipes && totalBreakfastRecipes > 0) {
                    Recipe_Detail recipe_detail = null;
                    ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                    Log.d("breakfast doc size","" + queryDocumentSnapshots.getDocumentChanges().size());
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot docu = doc.getDocument();
                        recipe_detail = new Recipe_Detail();
                        recipe_detail.setId(docu.getId());
                        recipe_detail.setCategory("Breakfast");
                        recipe_detail.setRecipeTitle((String) docu.get("recipe title"));
                        recipe_detail.setIngredients((List<String>) docu.get("ingredients"));
                        recipe_detail.setServing((String) docu.get("serving"));
                        recipe_detail.setCookingMethod((String) docu.get("cooking Method"));
                        recipes.add(recipe_detail);
                        Log.d("docuMent changes", docu.getId());
                    }
                    setBreakfastPushNotificationEnabled(recipes);
                }
                else if(totalRecipes == 0){
                    setBreakfastPushNotificationEnabled(null);
                }
                totalBreakfastRecipes = totalRecipes;
                if(totalRecipes == 0){
                    ++totalBreakfastRecipes;
                }
            }
        });

        firestore.collection("Latest recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Recipe_Detail recipe_detail = null;
                ArrayList<Recipe_Detail> recipes = new ArrayList<>();
                if (firsttiMeLatestRecipesFetch) {
                    List<DocumentSnapshot> docuMents = queryDocumentSnapshots.getDocuments();
                    String docId;
                    int size;
                    for (DocumentSnapshot docuMent : docuMents) {
                        docId = docuMent.getId();
                        size = ((List<Recipe_Detail>) (docuMent.get("recipes"))).size();
                        switch (docId) {
                            case "Latest appetizers recipes":
                                totalLatestAppetizersRecipes = size;
                                break;
                            case "Latest breakfast recipes":
                                totalLatestBreakfastRecipes = size;
                                break;
                            case "Latest lunch recipes":
                                totalLatestLunchRecipes = size;
                                break;
                            case "Latest salad recipes":
                                totalLatestSaladRecipes = size;
                                break;
                            default:
                                totalLatestDinnerRecipes = size;
                                break;
                        }
                    }
                    firsttiMeLatestRecipesFetch = false;
                } else {
                    List<DocumentChange> docuMentChanges = queryDocumentSnapshots.getDocumentChanges();
                    QueryDocumentSnapshot docuMentSnapshot = docuMentChanges.get(docuMentChanges.size() - 1).getDocument();
                    String docId = docuMentSnapshot.getId();
                    List<Recipe_Detail> recipeList = (List<Recipe_Detail>) (docuMentSnapshot.get("recipes"));
                    int recipeListSize = recipeList.size();
                    Log.d("latest recipes size", "onEvent: " + recipeListSize);
                    Map<String, Object> reci = (Map<String, Object>) recipeList.get(recipeListSize - 1);
                    if (docId.equals("Latest Dinner recipes") && totalLatestDinnerRecipes <= recipeListSize) {
                            totalLatestDinnerRecipes = recipeListSize;
                            recipe_detail = new Recipe_Detail();
                            recipe_detail.setId(docId);
                            recipe_detail.setCategory(docId);
                            recipe_detail.setRecipeTitle((String) (reci.get("recipe title")));
                            recipe_detail.setIngredients((List<String>) (reci.get("ingredients")));
                            recipe_detail.setServing((String) (reci.get("serving")));
                            recipe_detail.setCookingMethod((String) (reci.get("cooking Method")));
                            recipes.add(recipe_detail);
                            setLatestPushNotificationEnabled(recipes, docId);
                    } else if (docId.equals("Latest appetizers recipes") && totalLatestAppetizersRecipes <= recipeListSize) {
                            totalLatestAppetizersRecipes = recipeListSize;
                            recipe_detail = new Recipe_Detail();
                            recipe_detail.setId(docId);
                            recipe_detail.setCategory(docId);
                            recipe_detail.setRecipeTitle((String) (reci.get("recipe title")));
                            recipe_detail.setIngredients((List<String>) (reci.get("ingredients")));
                            recipe_detail.setServing((String) (reci.get("serving")));
                            recipe_detail.setCookingMethod((String) (reci.get("cooking Method")));
                            recipes.add(recipe_detail);
                            setLatestPushNotificationEnabled(recipes, docId);
                    } else if (docId.equals("Latest breakfast recipes") && totalLatestBreakfastRecipes <= recipeListSize) {
                            totalLatestBreakfastRecipes = recipeListSize;
                            recipe_detail = new Recipe_Detail();
                            recipe_detail.setId(docId);
                            recipe_detail.setCategory(docId);
                            recipe_detail.setRecipeTitle((String) (reci.get("recipe title")));
                            recipe_detail.setIngredients((List<String>) (reci.get("ingredients")));
                            recipe_detail.setServing((String) (reci.get("serving")));
                            recipe_detail.setCookingMethod((String) (reci.get("cooking Method")));
                            recipes.add(recipe_detail);
                            setLatestPushNotificationEnabled(recipes, docId);
                    } else if (docId.equals("Latest salad recipes") && totalLatestSaladRecipes <= recipeListSize) {
                            totalLatestSaladRecipes = recipeListSize;
                            recipe_detail = new Recipe_Detail();
                            recipe_detail.setId(docId);
                            recipe_detail.setCategory(docId);
                            recipe_detail.setRecipeTitle((String) (reci.get("recipe title")));
                            recipe_detail.setIngredients((List<String>) (reci.get("ingredients")));
                            recipe_detail.setServing((String) (reci.get("serving")));
                            recipe_detail.setCookingMethod((String) (reci.get("cooking Method")));
                            recipes.add(recipe_detail);
                            setLatestPushNotificationEnabled(recipes, docId);
                    } else if (docId.equals("Latest lunch recipes") && totalLatestLunchRecipes <= recipeListSize) {
                            totalLatestLunchRecipes = recipeListSize;
                            recipe_detail = new Recipe_Detail();
                            recipe_detail.setId(docId);
                            recipe_detail.setCategory(docId);
                            recipe_detail.setRecipeTitle((String) (reci.get("recipe title")));
                            recipe_detail.setIngredients((List<String>) (reci.get("ingredients")));
                            recipe_detail.setServing((String) (reci.get("serving")));
                            recipe_detail.setCookingMethod((String) (reci.get("cooking Method")));
                            recipes.add(recipe_detail);
                            setLatestPushNotificationEnabled(recipes, docId);
                    }
                }
            }
        });
    }

    private void setBreakfastPushNotificationEnabled(ArrayList<Recipe_Detail> recipes) {
        if(!breakfastPushNotificationEnabled){
            this.breakfastPushNotificationEnabled = true;
        }
        else{
            HashMap<String, ArrayList<Recipe_Detail>> breakfastRecipes = new HashMap<>();
            breakfastRecipes.put("Breakfast recipes", recipes);
            createNotification(breakfastRecipes);
        }

    }

    private void setSaladPushNotificationEnabled(ArrayList<Recipe_Detail> recipes) {
        if(!saladPushNotificationEnabled){
            this.saladPushNotificationEnabled = true;
        }
        else{
            HashMap<String, ArrayList<Recipe_Detail>> saladRecipes = new HashMap<>();
            saladRecipes.put("Salad recipes", recipes);
            createNotification(saladRecipes);
        }
    }

    private void setAppetizersPushNotificationEnabled(ArrayList<Recipe_Detail> recipes) {
        if(!appetizersPushNotificationEnabled){
            this.appetizersPushNotificationEnabled = true;
        }
        else{
            HashMap<String, ArrayList<Recipe_Detail>> appetetizersRecipes = new HashMap<>();
            appetetizersRecipes.put("Appetizers recipes", recipes);
            createNotification(appetetizersRecipes);
        }
    }
    private void setLatestPushNotificationEnabled(ArrayList<Recipe_Detail> recipes, String whichLatestRecipe) {
            HashMap<String, ArrayList<Recipe_Detail>> latestRecipes = new HashMap<>();
            switch(whichLatestRecipe){
                case "Latest appetizers recipes":
                    latestRecipes.put("Latest appetizers recipes", recipes);
                    break;
                case "Latest breakfast recipes":
                    latestRecipes.put("Latest breakfast recipes", recipes);
                    break;
                case "Latest lunch recipes":
                    latestRecipes.put("Latest lunch recipes", recipes);
                    break;
                case "Latest salad recipes":
                    latestRecipes.put("Latest salad recipes", recipes);
                    break;
                default:
                    latestRecipes.put("Latest dinner recipes", recipes);
                    break;
            }

            createNotification(latestRecipes);
    }
    private void setDinnerPushNotificationEnabled(ArrayList<Recipe_Detail> recipes) {
        if(!dinnerPushNotificationEnabled){
            this.dinnerPushNotificationEnabled = true;
        }
        else{
            HashMap<String, ArrayList<Recipe_Detail>> dinnerRecipes = new HashMap<>();
            dinnerRecipes.put("Dinner recipes", recipes);
            createNotification(dinnerRecipes);
        }
    }

    private void setLunchPushNotificationEnabled(ArrayList<Recipe_Detail> recipes) {
        if(!lunchPushNotificationEnabled){
            this.lunchPushNotificationEnabled = true;
        }
        else{
            HashMap<String, ArrayList<Recipe_Detail>> lunchRecipes = new HashMap<>();
            lunchRecipes.put("Lunch recipes", recipes);
            createNotification(lunchRecipes);
        }
    }

    private void createNotification(HashMap<String, ArrayList<Recipe_Detail>> recipes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My channel";
            String description = "My notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent notifyIntent = new Intent(context, Recipes.class);
        notifyIntent.putExtra("Recipes", recipes);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.outline_notification_important_black_18dp)
                .setContentTitle("Recipe updates")
                .setContentText("Check recipe updates")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(11, builder.build());
    }
    void fetchLatestRecipes(final NotifyUser notifyUser){
        firestore.collection("Latest recipes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Recipe_Detail> recipes = null;

                for( DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    Recipe_Detail recipe = null;
                    recipes = new ArrayList<>();
                    List<Map<Object, Object>> list = (List<Map<Object, Object>>) document.get("recipes");
                    for(Map<Object, Object> data : list){
                        Log.d("latest", "onSuccess: " + data.get("recipe title"));
                        recipe = new Recipe_Detail();
                        recipe.setRecipeTitle((String) data.get("recipe title"));
                        recipe.setCookingMethod((String) data.get("cooking Method"));
                        recipe.setIngredients((List<String>) data.get("ingredients"));
                        recipe.setServing((String) data.get("serving"));
                        recipe.setId(UUID.randomUUID().toString());
                        recipe.setCategory(document.getId());
                        recipes.add(recipe);
                    }
                    searchRecipes.put(document.getId(), recipes);
                }
                notifyUser.searchRecipeStatus(searchRecipes);
                searchRecipes = new HashMap<>();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    void backupRecipes(final List<String> recipes, final Backup_recipe_status backup_recipe_status){
        final DocumentReference documentReference = firestore.collection("Users").document(userDcumentId);
        documentReference.get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> encodedRecipes = (List<String>) (documentSnapshot.get("favouriate recipes"));

                if(encodedRecipes != null && encodedRecipes.containsAll(recipes)){
                    backup_recipe_status.backupRecipeStatus("These recipes already exists");
                }
                else{
                    documentReference.update("favouriate recipes", recipes).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    backup_recipe_status.backupRecipeStatus("Backup successfully coMpleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            backup_recipe_status.backupRecipeStatus("Backup failed");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    /*void deleteRecipeFromBackup(final List<String> recipes, final Backup_recipe_status backup_recipe_status){
        firestore.collection("Users").
                document(userDcumentId).update("favouriate recipes", recipes).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        backup_recipe_status.backupRecipeStatus("Recipe successfully deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                backup_recipe_status.backupRecipeStatus("Recipe is not deleted");
            }
        });
    }*/
    void fetchBackup(final ViewBackup viewBackup){
        firestore.collection("Users").
                document(userDcumentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> encodedRecipes = (List<String>)documentSnapshot.get("favouriate recipes");
                if(encodedRecipes != null && encodedRecipes.size() > 0){
                    Log.d("backup size", "size: " + encodedRecipes.size());
                    HashMap<String, ArrayList<Recipe_Detail>> recipes = new HashMap<>();
                    ArrayList<Recipe_Detail> breakfast = new ArrayList<>();
                    ArrayList<Recipe_Detail> lunch = new ArrayList<>();
                    ArrayList<Recipe_Detail> dinner = new ArrayList<>();
                    ArrayList<Recipe_Detail> salad = new ArrayList<>();
                    ArrayList<Recipe_Detail> appetizers = new ArrayList<>();
                    for(String encodedRecipe : encodedRecipes){
                        byte[] data = Base64.decode(encodedRecipe, Base64.DEFAULT);
                        String str_recipe = new String(data);
                        String[] recipe_contents = str_recipe.split("!!axz");
                        Recipe_Detail recipe_detail = new Recipe_Detail();
                        recipe_detail.setRecipeTitle(recipe_contents[1]);
                        recipe_detail.setCookingMethod(recipe_contents[2]);
                        String ing = recipe_contents[3].substring(1, recipe_contents[3].length() -2);
                        recipe_detail.setIngredients(Arrays.asList(ing.split(",")));
                        recipe_detail.setServing(recipe_contents[4]);
                        recipe_detail.setCategory(recipe_contents[5]);
                        if(recipe_contents[5].equals("Breakfast") || recipe_contents[5].equalsIgnoreCase("Latest breakfast recipes")){
                            breakfast.add(recipe_detail);
                        }
                        else if(recipe_contents[5].equals("Dinner") || recipe_contents[5].equalsIgnoreCase("Latest Dinner recipes")){
                            dinner.add(recipe_detail);
                        }
                        else if(recipe_contents[5].equals("Salad") || recipe_contents[5].equalsIgnoreCase("Latest salad recipes")){
                            salad.add(recipe_detail);
                        }
                        else if(recipe_contents[5].equals("Appetizers") || recipe_contents[5].equalsIgnoreCase("Latest appetizers recipes")){
                            appetizers.add(recipe_detail);
                        }
                        else{
                            lunch.add(recipe_detail);
                        }
                    }
                    if(dinner.size() > 0){
                        recipes.put("Dinner", dinner);
                    }
                    if(appetizers.size() > 0){
                        recipes.put("Appetizers", appetizers);
                    }
                    if(lunch.size() > 0){
                        recipes.put("Lunch", lunch);
                    }
                    if(salad.size() > 0){
                        recipes.put("Salad", salad);
                    }
                    if(breakfast.size() > 0){
                        recipes.put("Breakfast", breakfast);
                    }
                    viewBackup.searchRecipeStatus(recipes, true);
                }
                else{
                    viewBackup.searchRecipeStatus(null, false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    void restoreBackup(final Backup_recipe_status backup_recipe_status, final RestoreBackup restoreBackup){
        firestore.collection("Users").document(userDcumentId).
                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> backupRecipes = (List<String>) documentSnapshot.get("favouriate recipes");
                if(backupRecipes.size() > 0){
                    final List<Recipe_Detail> recipes = new ArrayList<>();
                    Picasso picasso = Picasso.with(context);
                    for(String encodedRecipe : backupRecipes) {
                        byte[] data = Base64.decode(encodedRecipe, Base64.DEFAULT);
                        String str_recipe = new String(data);
                        String[] recipe_contents = str_recipe.split("!!axz");
                        final Recipe_Detail recipe_detail = new Recipe_Detail();
                        recipe_detail.setId(recipe_contents[0]);
                        recipe_detail.setRecipeTitle(recipe_contents[1]);
                        recipe_detail.setCookingMethod(recipe_contents[2]);
                        String ing = recipe_contents[3].substring(1, recipe_contents[3].length() - 2);
                        recipe_detail.setIngredients(Arrays.asList(ing.split(",")));
                        recipe_detail.setServing(recipe_contents[4]);
                        recipe_detail.setCategory(recipe_contents[5]);
                        picasso.load(recipe_detail.getServing()).
                                into(new Target(){
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        recipe_detail.setServingTwo(bitmap);
                                        recipes.add(recipe_detail);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                    restoreBackup.restoreRecipesBackup(recipes);
                    backup_recipe_status.backupRecipeStatus("Backup successfully restored");
                }
                else{
                    backup_recipe_status.backupRecipeStatus("No backup exists");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    void checkRecipeExistenceOnBackup(final String recipe, final DeleteRecipe deleteRecipe, final Recipe_Detail recipe_detail, final SQL_delete_recipe delete_recipe, final String sectionTitle, final int position){
        firestore.collection("Users").document(userDcumentId).
                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> backupRecipes = (List<String>) documentSnapshot.get("favouriate recipes");
                if(backupRecipes != null && backupRecipes.size() > 0 && backupRecipes.contains(recipe)){
                    deleteRecipe.deleteRecipe(recipe_detail, delete_recipe, sectionTitle, position, true);
                }
                else{
                    deleteRecipe.deleteRecipe(recipe_detail, delete_recipe, sectionTitle, position, false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
