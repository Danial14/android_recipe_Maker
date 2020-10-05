package com.example.noman_000.android_recipe_maker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Basic_ForM extends AppCompatActivity {
    static final String VALID_EMAIL= "Email is valid";
    static final String VALID_PASSWORD = "Password is valid";
    static SharedPreferences sharedPreferences;
    Basic_ForM(){
        /*if(sharedPreferences == null){
            sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_FILE_NAME),
                    Context.MODE_PRIVATE);
        }*/
    }
    void makeTextClickable(TextView textView, final String token){
        String text = (String) textView.getText();
        SpannableString spannableString = new SpannableString(text);
        int spanStringStartIndex = text.indexOf(token);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = null;
                switch(token){
                    case "Signup":
                        Toast.makeText(Basic_ForM.this, "signup", Toast.LENGTH_LONG).show();
                        intent = new Intent(Basic_ForM.this, Registration_ForM.class);
                        break;
                    default:
                        intent = new Intent(getApplicationContext(), UserLogin.class);
                    }
                    startActivity(intent);
                }
            };
            spannableString.setSpan(clickableSpan, spanStringStartIndex, spanStringStartIndex + token.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    String isPasswordValid(String password){
        if(TextUtils.isEmpty(password)){
          return "Password is required";
        }
        else if(password.length() < 6){
            return "Password should be of atleast 6 characters";
        }
        return VALID_PASSWORD;
    }
    String isEmailValid(String email){
        if(TextUtils.isEmpty(email)){
            Log.d("valid email req", "isEmailValid: ");
          return "Email is required";
        }
        else if(!email.contains("@")){
            return "Please enter valid email";
        }
        else{
            int atIndex = email.indexOf('@');
            if(atIndex > 0 && atIndex < email.length() - 1){
                return VALID_EMAIL;
            }
            return "Please enter valid email";
        }
    }
    Set<String> getUserEmailSet(){
        if(sharedPreferences == null){
            sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_FILE_NAME),
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getStringSet(getString(R.string.EMAIL_SET_KEY), null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
}
