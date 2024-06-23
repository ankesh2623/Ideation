package com.example.ideation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ideation.Adapter.CommentAdapter;
import com.example.ideation.Model.CommentModel;
import com.example.ideation.Model.UserModel;
import com.example.ideation.databinding.ActivityCommentPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentPageActivity extends AppCompatActivity {

    ActivityCommentPageBinding binding;
    private String postID, authorID;
    Intent intent;
    FirebaseUser fuser;
    ArrayList<CommentModel> commentList = new ArrayList<>();
    CommentAdapter adapter;
    ValueEventListener userValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if FirebaseUser is null
        if (fuser == null) {
            // Redirect to login/register screen or handle as necessary
            startActivity(new Intent(CommentPageActivity.this, RegisterActivity.class));
            finish();
            return;
        }

        // Get intent extras
        intent = getIntent();
        postID = intent.getStringExtra("postId");
        authorID = intent.getStringExtra("authorId");

        // Initialize views and adapters
        adapter = new CommentAdapter(CommentPageActivity.this, commentList);
        binding.commentsRecycler.setHasFixedSize(true);
        binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(CommentPageActivity.this));
        binding.commentsRecycler.setAdapter(adapter);

        // Fetch comments from Firebase
        getComments();

        // Post comment button click listener
        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = binding.commentText.getText().toString().trim();
                if (!comment.isEmpty()) {
                    pushComment(comment);
                }
            }
        });

        // Load user image and display name
        loadUserDetails();
    }

    private void getComments() {
        FirebaseDatabase.getInstance().getReference().child("comments").child(postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentList.clear();
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            CommentModel comment = shot.getValue(CommentModel.class);
                            if (comment != null) {
                                commentList.add(comment);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.progressBar6.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CommentPageActivity.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pushComment(String comment) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("publisher", fuser.getUid());

        FirebaseDatabase.getInstance().getReference().child("comments").child(postID).push()
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            makeToast("Comment Posted");
                            binding.commentText.setText("");
                        } else {
                            makeToast("Failed to post comment");
                        }
                    }
                });
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadUserDetails() {
        // Load user's profile image
        userValueEventListener = FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user != null && !user.getImageURL().equals("default")) {
                            Picasso.get().load(user.getImageURL()).into(binding.userProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CommentPageActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove ValueEventListener to avoid memory leaks
        if (userValueEventListener != null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid())
                    .removeEventListener(userValueEventListener);
        }
    }
}
