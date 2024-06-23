//package com.example.ideation.Fragments;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.ideation.Adapter.PostAdapter;
//import com.example.ideation.Model.PostModel;
//import com.example.ideation.databinding.FragmentFeedBinding;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class BookmarkFragment extends Fragment {
//
//    FragmentFeedBinding binding;
//    PostAdapter adapter;
//    ArrayList<PostModel> posts = new ArrayList<>();
//    FirebaseFirestore fstore;
//    FirebaseDatabase db;
//    String userID;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        binding = FragmentFeedBinding.inflate(inflater, container, false);
//
//        binding.feedRecyle.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new PostAdapter(posts, getContext(), true);
//        binding.feedRecyle.setAdapter(adapter);
//
//        fstore = FirebaseFirestore.getInstance();
//        db = FirebaseDatabase.getInstance();
//        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        db.getReference().child("saves").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                posts.clear(); // Clear the list to avoid duplicates
//                for (DataSnapshot shot : snapshot.getChildren()) {
//                    PostModel post = shot.getValue(PostModel.class);
//                    if (post != null && post.getUserID() != null && post.getUserID().equals(userID)) {
//                        posts.add(post);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//                binding.progressBar2.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle database error
//                Toast.makeText(getContext(), "Failed to fetch saved posts", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return binding.getRoot();
//    }
//}
package com.example.ideation.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ideation.Adapter.PostAdapter;
import com.example.ideation.Model.PostModel;
import com.example.ideation.databinding.FragmentFeedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {

    FragmentFeedBinding binding;
    PostAdapter adapter;
    ArrayList<PostModel> posts = new ArrayList<>();
    FirebaseDatabase db;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize RecyclerView
        binding.feedRecyle.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(posts, getContext(), true); // Assuming true means this is a bookmarked fragment
        binding.feedRecyle.setAdapter(adapter);

        // Initialize Firebase components
        db = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
        } else {
            // Handle case where user is not authenticated
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Fetch saved posts from Firebase
        db.getReference().child("saves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot shot : snapshot.getChildren()) {
                    PostModel post = shot.getValue(PostModel.class);
                    if (post != null && post.getUserID() != null && post.getUserID().equals(userID)) {
                        posts.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "Failed to fetch saved posts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
