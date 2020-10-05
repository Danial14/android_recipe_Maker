package com.example.noman_000.android_recipe_maker;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noman_000.android_recipe_maker.Interfaces.ValidateLogin;

import java.util.Set;

import databases.Database_Manager;


public class UserLogin extends Basic_ForM implements ValidateLogin {
    private Database_Manager databaseManager;
    private AutoCompleteTextView email;
    private EditText password;
    private Button login;
    private static final String USER_MAIL = "Mail";
    private static final String USER_PASS = "Pass";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        makeTextClickable((TextView) findViewById(R.id.NOT_A_MEMBER), "Signup");
        email = findViewById(R.id.eMail);
        password = findViewById(R.id.password);
        login = findViewById(R.id.log);
        databaseManager = Database_Manager.getDatabaseManagerInstance(getApplicationContext());
        Set<String> userEmail = getUserEmailSet();
        if(userEmail != null){
            email.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, userEmail.toArray(new String[]{})
            ));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String checkMail = isEmailValid(mail);
                Log.d("login", "onClick: " + mail + " " + pass);
                String checkPassword = isPasswordValid(pass);
                if(checkMail.equals(VALID_EMAIL)){
                    if(checkPassword.equals(VALID_PASSWORD)){
                        databaseManager.userLogin(mail, pass, UserLogin.this);
                    }
                    else{
                        password.setError(checkPassword);
                    }
                }
                else{
                    email.setError(checkMail);
                }
            }
        });
    }

    @Override
    public void onSuccessfulLogin() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.user_input, null);

        RadioGroup radioGroup = dialogView.findViewById(R.id.input_user);
        final Intent intent = new Intent(this, Home_Page.class);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.sear_ing_list:
                        intent.putExtra("position", 0);
                        break;
                    default:
                        intent.putExtra("position", 2);
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
          final AlertDialog dialog = builder.setTitle("Please select any one option and click ok").
                  setView(dialogView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  if(intent.getIntExtra("position", - 1) > -1) {
                      startActivity(intent);
                      databaseManager.startListeningForUpdates();
                      databaseManager.setEMail(email.getText().toString());
                      email.setText("");
                      password.setText("");
                  }
              }
          }).setCancelable(false).show();
          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);

    }

    @Override
    public void loginFailed() {
        Toast.makeText(this, "login failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void incorrectEMailOrPassword() {
        Toast.makeText(this, "Incorrect eMail or password", Toast.LENGTH_LONG).show();
        email.setText("");
        password.setText("");
    }
}
