package com.example.noman_000.android_recipe_maker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import databases.Database_Manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.noman_000.android_recipe_maker.DataModels.Recipe_Detail;
import com.example.noman_000.android_recipe_maker.Interfaces.Backup_recipe_status;
import com.example.noman_000.android_recipe_maker.Interfaces.ViewBackup;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Home_Page extends AppCompatActivity implements ViewBackup {
    private CallbackManager callbackManager;
    private Database_Manager databaseManager;
    static ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__page);
        Log.d("HoMe", "onCreate: ");
        shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new MyPager(getSupportFragmentManager()));
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_list_black_48);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_favorite_black_48);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_search_black_48);
        databaseManager = Database_Manager.getDatabaseManagerInstance(getApplicationContext());
        Intent intent = getIntent();
        if(intent != null){
            int position = intent.getIntExtra("position", -1);
            if(position > -1) {
                viewPager.setCurrentItem(position, true);
                if(position == 2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home_Page.this);
                    builder.setMessage(R.string.query_guidence).
                            setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Home_Page.this, SearchRecipe.class);
                                    startActivity(intent);
                                }
                            }).setCancelable(false).show();
                }
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home_Page.this);
                    builder.setMessage(R.string.query_guidence).
                            setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Home_Page.this, SearchRecipe.class);
                                    startActivity(intent);
                                }
                            }).setCancelable(false).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        shareDialog.registerCallback(callbackManager,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(Home_Page.this, "Recipe successfully shared", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(Home_Page.this, "error", Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu01, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.latest_recipes:
                Intent intent = new Intent(this, Latest_Recipe.class);
                startActivity(intent);
                break;
            case R.id.logout:
                Intent logout = new Intent(this, UserLogin.class);
                startActivity(logout);
                finish();
                break;
            case R.id.restore_backup:
                databaseManager.restoreBackupFroMFirebase(new Backup_recipe_status() {
                    @Override
                    public void backupRecipeStatus(String status) {
                        if(status.equalsIgnoreCase("No backup exists")){
                            Toast.makeText(Home_Page.this, status, Toast.LENGTH_LONG).show();
                        }
                        else{

                            List<Fragment> fragMentS = getSupportFragmentManager().getFragments();
                            for(Fragment fragMent : fragMentS) {
                                if (fragMent instanceof My_Favouriate_Recipes) {
                                    Toast.makeText(Home_Page.this, status, Toast.LENGTH_LONG).show();
                                    ((My_Favouriate_Recipes) fragMent).updateRecipesContent();
                                }
                            }
                        }
                    }
                });
                break;
            default:
                databaseManager.fetchBackupFroMFirebase(this);
        }
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Home page", "onDestroy");
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void searchRecipeStatus(HashMap<String, ArrayList<Recipe_Detail>> recipes, boolean status) {
        if(status){
            Intent intent = new Intent(this, Recipes.class);
            intent.putExtra("Recipes", recipes);
            intent.putExtra("View backup", true);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "There is no recipe in backup", Toast.LENGTH_LONG).show();
        }
    }
}
class MyPager extends FragmentStatePagerAdapter {
    MyPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new Ingredients_List_Search();
            case 1: return new My_Favouriate_Recipes();
            default: return new Simple_Search();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}

