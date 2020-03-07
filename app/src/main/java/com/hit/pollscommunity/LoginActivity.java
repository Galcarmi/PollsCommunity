package com.hit.pollscommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {
    private EditText emailField;
    private EditText passwordField;
    private Button loginBtn;
    private Button goToRegisterBtn;

    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_button);
        goToRegisterBtn = findViewById(R.id.register_button);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            if(Utils.checkForInternetConnection(LoginActivity.this)){
                signIn();
            }
            }
        });

        goToRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

    }

    private void checkIfUserAlreadyLoggedIn(){
        if(mAuth.getCurrentUser()!=null){
            finish();
        }
    }

    private void signIn() {
        loginBtn.setEnabled(false);
        loginBtn.setAlpha(0.5f);
        goToRegisterBtn.setEnabled(false);
        goToRegisterBtn.setAlpha(0.5f);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_missing_username_or_password_toast),Toast.LENGTH_LONG).show();
            loginBtn.setEnabled(true);
            loginBtn.setAlpha(1f);
            goToRegisterBtn.setEnabled(true);
            goToRegisterBtn.setAlpha(1f);
            return;
        }
        else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrong_username_or_password),Toast.LENGTH_SHORT).show();
                        loginBtn.setEnabled(true);
                        loginBtn.setAlpha(1f);
                        goToRegisterBtn.setEnabled(true);
                        goToRegisterBtn.setAlpha(1f);

                    }
                    else{
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_successfully_toast),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfUserAlreadyLoggedIn();
    }
}
