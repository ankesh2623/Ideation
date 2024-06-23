package com.example.ideation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser fuser;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        logo = findViewById(R.id.imageView3);
        logo.animate().alpha(1).setDuration(1200);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("handleReg",MODE_PRIVATE);
                int ck=sp.getInt("posi",0);
                if (ck==0) startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                else if (ck==1) startActivity(new Intent(MainActivity.this, EmailVerificationActivity.class));
                else startActivity(new Intent(MainActivity.this,BottomNavActivity.class));
                finish();
            }
        },2000);
    }
}