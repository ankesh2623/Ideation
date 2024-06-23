package com.example.ideation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email,password;
    Button login;
    String user,pass;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email =  findViewById(R.id.emailsignin);
        password = findViewById(R.id.passwordsignin);
        login = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        bar = findViewById(R.id.loading);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = email.getText().toString().trim();
                pass = password.getText().toString().trim();
                if (user.isEmpty()){
                    email.setError("Email is Required");
                    email.requestFocus();
                }
                if (pass.isEmpty()){
                    password.setError("Email is Required");
                    password.requestFocus();
                }
                else{
                    bar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(user,pass).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        SharedPreferences sp = getSharedPreferences("handleReg",MODE_PRIVATE);
                                        sp.edit().putInt("posi",2).apply();
                                        startActivity(new Intent(LoginActivity.this, BottomNavActivity.class));
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Failed "+task.getException().
                                                getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    bar.setVisibility(View.GONE);
                                }
                            });
                }

            }
        });
    }

    public void RegisterPage(View view) {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        finish();
    }

}