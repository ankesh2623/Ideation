package com.example.ideation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ideation.Model.PostModel;
import com.example.ideation.databinding.ActivityAddPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";

    FirebaseAuth fAuth;
    ActivityAddPostBinding binding;
    FirebaseDatabase db;
    String uID;
    PostModel post = new PostModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialise();

        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUser();
            }
        });
    }

    private void initialise() {
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login activity if user is not authenticated
            startActivity(new Intent(AddPostActivity.this, RegisterActivity.class));
            finish(); // Finish this activity to prevent returning to it
        } else {
            uID = currentUser.getUid();
            db = FirebaseDatabase.getInstance();
        }
    }

    private void setUser() {
        String overview = binding.overview.getText().toString().trim();
        String description = binding.problemDesc.getText().toString().trim();

        if (overview.isEmpty()) {
            binding.overview.setError("Overview is required");
            return;
        }
        if (description.isEmpty()) {
            binding.problemDesc.setError("Description is required");
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
        DatabaseReference ref = db.getReference("posts");
        String postid = ref.push().getKey();
        post.setPostUrl(postid);

        if (postid != null) {
            ref.child(postid).setValue(post)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully uploaded post
                        Log.d(TAG, "Post uploaded successfully with ID: " + postid);
                        startActivity(new Intent(AddPostActivity.this, BottomNavActivity.class));
                        finish(); // Finish this activity to prevent returning to it
                    })
                    .addOnFailureListener(e -> {
                        // Failed to upload post
                        Log.e(TAG, "Failed to upload post", e);
                        Toast.makeText(AddPostActivity.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Error creating post ID", Toast.LENGTH_SHORT).show();
        }
    }
}
