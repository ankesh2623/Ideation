package com.example.ideation.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideation.Adapter.PostAdapter;
import com.example.ideation.AddPostActivity;
import com.example.ideation.Model.PostModel;
import com.example.ideation.Model.UserModel;
import com.example.ideation.R;
import com.example.ideation.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    FragmentHomeBinding binding;
    private PostAdapter adapter;
    private ArrayList<PostModel> posts = new ArrayList<PostModel>();
    FirebaseFirestore fstore;
    FirebaseDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentHomeBinding.inflate(inflater, container, false);
        fstore = FirebaseFirestore.getInstance();
        adapter = new PostAdapter(posts,getContext(),false);
        db = FirebaseDatabase.getInstance();

        db.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot: snapshot.getChildren()){
                    PostModel post = shot.getValue(PostModel.class);
                    posts.add(post);
                }
                adapter.notifyDataSetChanged();
                binding.progressBar3.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.searchPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                searchPost(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddPostActivity.class));
            }
        });
        return binding.getRoot();
    }

    private void searchPost (String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("posts")
                .orderByChild("overview").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot shot : snapshot.getChildren()){
                    PostModel post = shot.getValue(PostModel.class);
                    posts.add(post);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}