package com.example.ideation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ideation.Model.PostModel;
import com.example.ideation.databinding.ActivityAddPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    ActivityAddPostBinding binding;
    FirebaseDatabase db;
    String uID,username,profession;
    PostModel post = new PostModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());

        initialise();



        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUser();
            }
        });


        setContentView(binding.getRoot());
    }

    private void setUser() {
        String overview = binding.overview.getText().toString().trim();
        String description = binding.problemDesc.getText().toString().trim();
        if (overview.isEmpty()){
            binding.overview.setError("Overview is Required");
            return;
        }
        if (description.isEmpty()){
            binding.problemDesc.setError("Description is Required");
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String time = dateFormat.format(Calendar.getInstance().getTime());
        post.setUserID(uID);
        post.setOverview(overview);
        post.setDescription(description);
        post.setTime(time);
        uploadData();
    }

    private void uploadData() {
        Date time = Calendar.getInstance().getTime();

        DatabaseReference ref = db.getReference("posts");
        String postid = ref.push().getKey();
        post.setPostUrl(postid);
        ref.child(postid).setValue(post);
        startActivity(new Intent(AddPostActivity.this, BottomNavActivity.class));
    }

    private void initialise() {
        fAuth= FirebaseAuth.getInstance();
        uID = fAuth.getCurrentUser().getUid();
        db=FirebaseDatabase.getInstance();

    }
}