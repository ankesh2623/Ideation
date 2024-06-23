package com.example.ideation.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideation.Adapter.UserAdapter;
import com.example.ideation.Model.UserModel;
import com.example.ideation.R;
import com.example.ideation.databinding.FragmentExploreBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    FragmentExploreBinding binding;
    private ArrayList<UserModel> users = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentExploreBinding.inflate(inflater, container, false);

        binding.recyclerUsers.setHasFixedSize(true);
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(),users,true);
        binding.recyclerUsers.setAdapter(adapter);
        readUsers();


        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        return binding.getRoot();
    }

    private void readUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(binding.searchBar.getText().toString())){
                    users.clear();
                    for(DataSnapshot shot : snapshot.getChildren()){
                        UserModel user = shot.getValue(UserModel.class);
                        users.add(user);
                    }
                    adapter.notifyDataSetChanged();
                    binding.progressBar5.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser (String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("userName").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot shot : snapshot.getChildren()){
                    UserModel user = shot.getValue(UserModel.class);
                    users.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}