package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {

    private Button goToLoginBtn;
    private Button registerBtn;
    private EditText emailView;
    private EditText passwordView;
    private EditText firstnameView;
    private EditText lastnameView;
    private EditText adminCodeView;
    private CheckBox adminCheckbox;

    //firebase////
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("/Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        goToLoginBtn = findViewById(R.id.login_button);
        registerBtn = findViewById(R.id.register_button);
        emailView = findViewById(R.id.email_input);
        passwordView = findViewById(R.id.password_input);
        firstnameView = findViewById(R.id.firstname_input);
        lastnameView = findViewById(R.id.lastname_input);
        adminCodeView = findViewById(R.id.admin_code_input);
        adminCheckbox = findViewById(R.id.admin_checkbox);

        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(Utils.checkForInternetConnection(RegisterActivity.this)){
                    register();
                }
            }
        });

        goToLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        adminCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    adminCodeView.setVisibility(View.VISIBLE);
                }
                else{
                    adminCodeView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void register(){
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        final String firstname = firstnameView.getText().toString();
        final String lastname = lastnameView.getText().toString();

        if(TextUtils.isEmpty(email.trim())){
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.please_enter_email),Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password.trim())){
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.please_enter_password),Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(firstname.trim())){
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.please_enter_firstname),Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lastname.trim())){
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.please_enter_lastname),Toast.LENGTH_SHORT).show();
        }
        else if(password.length()<8){
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.password_not_long_enough_toast),Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            registerBtn.setEnabled(false);
            registerBtn.setAlpha(0.5f);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            boolean isAdmin =  adminCodeView.getText().toString().equals("admin");
                            User newUser = new User(mAuth.getCurrentUser().getUid(),firstname,lastname,isAdmin);
                            myRef.child(mAuth.getCurrentUser().getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.general_error_text_try_again),Toast.LENGTH_SHORT).show();
                                        mAuth.getCurrentUser().delete();
                                        registerBtn.setEnabled(true);
                                        registerBtn.setAlpha(1f);
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.login_successfully_toast),Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                    }
                                }
                            });
                        }else{
                            registerBtn.setEnabled(true);
                            registerBtn.setAlpha(1f);
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.weak_password_toast),Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.invalid_email_toast),Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.user_already_exists_toast),Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Log.e("error", e.getMessage());
                            }
                        }
                    }
                });
        }
    }
}
