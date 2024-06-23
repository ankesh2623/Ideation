package com.example.ideation.Fragments;

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
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ideation.Model.PostModel;
import com.example.ideation.Model.UserModel;
import com.example.ideation.RegisterActivity;
import com.example.ideation.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentProfileBinding.
                inflate(inflater, container, false);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getFollowCount();
        getFollowingCount();
        getPostCount();

        binding.gitHubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/bhavya2921");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        binding.linkdinLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.linkedin.com/in/bhavya-agrawal-0a3521202/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        binding.reposLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(binding.reposLink.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.github.com/users/bhavya2921";
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    binding.repocount.setText(response.getString("public_repos")+" public Repos");
                    binding.reposLink.setText(response.getString("repos_url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                binding.userProfileImage.setImageURI(uri);
                final StorageReference reference = storage.getReference().child(fAuth.getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Uploded", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("Users").child(fAuth.getCurrentUser().
                                        getUid()).child("imageURL").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        });

        getUser();

        binding.changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(getContext(), RegisterActivity.class));
                getActivity().finish();
            }
        });

        return binding.getRoot();
    }

    private void getPostCount() {
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cont=0;
                for (DataSnapshot shot : snapshot.getChildren()){
                    PostModel post = shot.getValue(PostModel.class);
                    if (post.getUserID().equals(fAuth.getCurrentUser().getUid())) cont++;
                }
                binding.postCount.setText(cont+"posts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowCount() {
        database.getReference().child("follow").child(fAuth.getCurrentUser().getUid()).child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.followCount.setText(snapshot.getChildrenCount()+"");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFollowingCount() {
        database.getReference().child("follow").child(fAuth.getCurrentUser().getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.followingCount.setText(snapshot.getChildrenCount()+" following");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void getUser() {
        String userID= fAuth.getCurrentUser().getUid();
        database.getReference().child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user!=null) updateUser(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUser(UserModel user) {
        binding.constraintLayout2.setVisibility(View.VISIBLE);
        binding.progressBar4.setVisibility(View.GONE);
        if (!user.getImageURL().equals("default")){
            Picasso.get().load(user.getImageURL()).into(binding.userProfileImage);
        }
        binding.userName.setText(user.getUserName());
        binding.workProfession.setText(user.getProfession());
        binding.followCount.setText(String.valueOf(user.getFollowCount()));
    }

}