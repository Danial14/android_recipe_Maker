package com.example.noman_000.android_recipe_maker;

import android.content.Context;
import android.os.AsyncTask;

import com.example.noman_000.android_recipe_maker.Interfaces.OnIngredientsDownloaded;

import databases.Database_Manager;

class FetchIngredients extends AsyncTask {
    private OnIngredientsDownloaded onIngredientsDownloaded;
    private Context context;
    FetchIngredients(OnIngredientsDownloaded onIngredientsDownloaded, Context context){
        this.onIngredientsDownloaded = onIngredientsDownloaded;
        this.context = context;
    }
    @Override
    protected Object doInBackground(Object[] objects) {
        Database_Manager.getDatabaseManagerInstance(context).fetchIngredients(onIngredientsDownloaded);
        return null;
    }
}
