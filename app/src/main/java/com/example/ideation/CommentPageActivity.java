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
    private String postID,authorID;
    Intent intent;
    FirebaseUser fuser;
    ArrayList<CommentModel> commentList = new ArrayList<>();
    CommentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();
        postID = intent.getStringExtra("postId");
        authorID = intent.getStringExtra("authorId");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();


        adapter = new CommentAdapter(CommentPageActivity.this,commentList);
        binding.commentsRecycler.setHasFixedSize(true);
        binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(CommentPageActivity.this));
        binding.commentsRecycler.setAdapter(adapter);

        getComments();

        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = binding.commentText.getText().toString().trim();
                if (!comment.isEmpty()){
                    pushComment(comment);
                }
            }
        });

    }

    private void getComments() {
        FirebaseDatabase.getInstance().getReference().child("comments").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot shot : snapshot.getChildren()){
                    CommentModel comment = shot.getValue(CommentModel.class);
                    commentList.add(comment);
                }
                adapter.notifyDataSetChanged();
                binding.progressBar6.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void pushComment(String comment) {
        HashMap<String,Object> map = new HashMap<>();

        map.put("comment",comment);
        map.put("publisher",fuser.getUid());

        FirebaseDatabase.getInstance().getReference().child("comments").child(postID).push().setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            makeToast("Comment Posted");
                            binding.commentText.setText("");
                        }
                        else makeToast(task.getException().getMessage());
                    }
                });
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (!user.getImageURL().equals("default"))
                            Picasso.get().load(user.getImageURL()).into(binding.userProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}