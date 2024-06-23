package com.example.ideation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    TextInputEditText username, email, password;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.signup);
        username = findViewById(R.id.namesignup);
        email = findViewById(R.id.phoneNo);
        password = findViewById(R.id.passwordsignup);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        pbar = findViewById(R.id.progressBar);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString().trim();
                String emails = email.getText().toString().trim();
                String passwords = password.getText().toString().trim();
                if (userName.isEmpty()) username.setError("Name is Required!");
                else if (emails.isEmpty()) email.setError("Email is Required! ");
                else if (passwords.isEmpty()) password.setError("Password is Required! ");
                else {
                    pbar.setVisibility(View.VISIBLE);
                    register.setVisibility(View.INVISIBLE);
                    mAuth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser fuser = mAuth.getCurrentUser();
                                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(RegisterActivity.this, EmailVerificationActivity.class);
                                        intent.putExtra("userName", userName);
                                        intent.putExtra("email", emails);
                                        intent.putExtra("password", passwords);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        makeToast("Error: " + e.getMessage());
                                        pbar.setVisibility(View.INVISIBLE);
                                        register.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                makeToast("" + task.getException().getMessage());
                                pbar.setVisibility(View.INVISIBLE);
                                register.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void LoginPage(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
