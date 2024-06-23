package com.example.ideation.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ideation.Model.PostModel;
import com.example.ideation.Model.UserModel;
import com.example.ideation.RegisterActivity;
import com.example.ideation.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth fAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ActivityResultLauncher<String> launcher;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        context = getContext();
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Check if user is authenticated, redirect to login if not
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(context, RegisterActivity.class));
            requireActivity().finish();
            return binding.getRoot();
        }

        getFollowCount();
        getFollowingCount();
        getPostCount();

        binding.gitHubLink.setOnClickListener(v -> openLink("https://github.com/ankesh2623"));
        binding.linkdinLink.setOnClickListener(v -> openLink("https://www.linkedin.com/in/ankeshchaubey/"));
        binding.reposLink.setOnClickListener(v -> openLink(binding.reposLink.getText().toString()));

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.github.com/users/ankesh2623";
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        binding.repocount.setText(response.getString("public_repos") + " public Repos");
                        binding.reposLink.setText(response.getString("repos_url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Handle error fetching GitHub data
            Toast.makeText(context, "Failed to fetch GitHub data", Toast.LENGTH_SHORT).show();
        });

        queue.add(jsonReq);

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageSelection);

        binding.changeImage.setOnClickListener(v -> launcher.launch("image/*"));

//        binding.logout.setOnClickListener(v -> {
//            fAuth.signOut();
//            startActivity(new Intent(context, RegisterActivity.class));
//            requireActivity().finish();
//        });
        binding.logout.setOnClickListener(v -> {
            fAuth.signOut();
            Intent intent = new Intent(context, RegisterActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
//            startActivity(intent);
//            requireActivity().finish(); // Finish the current activity
            navigateToRegisterActivity();
        });


        getUser(currentUser.getUid());

        return binding.getRoot();
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void getPostCount() {
        DatabaseReference postsRef = database.getReference().child("posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cont = 0;
                for (DataSnapshot shot : snapshot.getChildren()) {
                    PostModel post = shot.getValue(PostModel.class);
                    if (post != null && post.getUserID().equals(fAuth.getUid())) {
                        cont++;
                    }
                }
                binding.postCount.setText(cont+ " posts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(context, "Failed to fetch post count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFollowCount() {
        DatabaseReference followRef = database.getReference().child("follow").child(fAuth.getUid()).child("followers");
        followRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followCount.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(context, "Failed to fetch follow count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFollowingCount() {
        DatabaseReference followingRef = database.getReference().child("follow").child(fAuth.getUid()).child("following");
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followingCount.setText(snapshot.getChildrenCount() + " following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(context, "Failed to fetch following count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser(String userId) {
        DatabaseReference userRef = database.getReference().child("Users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    updateUser(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(UserModel user) {
        binding.constraintLayout2.setVisibility(View.VISIBLE);
        binding.progressBar4.setVisibility(View.GONE);
        if (!user.getImageURL().equals("default")) {
            Picasso.get().load(user.getImageURL()).into(binding.userProfileImage);
        }
        binding.userName.setText(user.getUserName());
        binding.workProfession.setText(user.getProfession());
        binding.followCount.setText(String.valueOf(user.getFollowCount()));
    }

    private void handleImageSelection(Uri uri) {
        binding.userProfileImage.setImageURI(uri);
        final StorageReference reference = storage.getReference().child(fAuth.getUid());
        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
            reference.getDownloadUrl().addOnSuccessListener(uriResult -> {
                database.getReference().child("Users").child(fAuth.getUid())
                        .child("imageURL").setValue(uriResult.toString());
            });
        });
    }
    private void navigateToRegisterActivity(){
        Intent intent=new Intent(context,RegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
