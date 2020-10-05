package com.example.noman_000.android_recipe_maker;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.noman_000.android_recipe_maker.Interfaces.ValidateUserSignup;

import java.util.HashSet;
import java.util.Set;

import databases.Database_Manager;


public class Registration_ForM extends Basic_ForM implements ValidateUserSignup {
    private Database_Manager databaseManager;
    private EditText email;
    private EditText password;
    private Button signUp;
    private static final String SIGNUP_MAIL = "USER_SIGNUP";
    private static final String SIGNUP_PASS = "USER_SIGNUP_PASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__for_m);
        makeTextClickable((TextView) findViewById(R.id.MEMEBER_ALREADY), "Login");
        databaseManager = Database_Manager.getDatabaseManagerInstance(getApplicationContext());
        email = findViewById(R.id.signup_eMail);
        password = findViewById(R.id.signup_password);
        signUp = findViewById(R.id.Register);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String checkEmail = isEmailValid(mail);
                String checkPassword = isPasswordValid(pass);
                if(checkEmail.equals(VALID_EMAIL)){
                    if(checkPassword.equals(VALID_PASSWORD)){
                        databaseManager.registerUser(mail, pass,
                                Registration_ForM.this);
                    }
                    else{
                        password.setError(checkPassword);
                    }
                }
                else{
                    email.setError(checkEmail);
                }
            }
        });
    }


    @Override
    public void onSuccessfulSignup() {
        Toast.makeText(this, "signup successful", Toast.LENGTH_LONG).show();
        Set<String> userEmail = getUserEmailSet();
        if(userEmail == null){
            userEmail = new HashSet<>();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        userEmail.add(email.getText().toString());
        editor.putStringSet(getString(R.string.EMAIL_SET_KEY), userEmail).apply();
        email.setText("");
        password.setText("");
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }

    @Override
    public void signupFailed() {
        Toast.makeText(this, "signup failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void userExists() {
        Toast.makeText(this, "User already exists", Toast.LENGTH_LONG).show();
        email.setText("");
        password.setText("");
    }
}
