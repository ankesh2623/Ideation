package com.example.ideation.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideation.Adapter.PostAdapter;
import com.example.ideation.Model.PostModel;
import com.example.ideation.R;
import com.example.ideation.databinding.FragmentFeedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {

    FragmentFeedBinding binding;
    PostAdapter adapter;
    ArrayList<PostModel> posts = new ArrayList<>();
    FirebaseFirestore fstore;
    FirebaseDatabase db;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);

        binding.feedRecyle.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(posts,getContext(),true);
        binding.feedRecyle.setAdapter(adapter);

        fstore = FirebaseFirestore.getInstance();
        db=FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        db.getReference().child("saves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()){
                    PostModel post = shot.getValue(PostModel.class);
                    if (post.getUserID().equals(userID)){
                        posts.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}