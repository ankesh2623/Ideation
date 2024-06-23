package com.example.ideation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ideation.Model.UserModel;
import com.example.ideation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<UserModel> mUser;
    private boolean isfrag;

    private FirebaseUser fuser;

    public UserAdapter(Context context, List<UserModel> mUser, boolean isfrag) {
        this.context = context;
        this.mUser = mUser;
        this.isfrag = isfrag;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        UserModel user= mUser.get(position);
        holder.follow.setVisibility(View.VISIBLE);
        holder.profession.setText(user.getProfession());
        holder.username.setText(user.getUserName());
        Picasso.get().load(user.getImageURL()).placeholder(R.drawable.avatar).
                into(holder.profileImage);

        isFollow(user.getUserID(),holder.follow);

        if (user.getUserID().equals(fuser.getUid())){
            holder.follow.setVisibility(View.GONE);
        }

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(fuser.getUid()).child("following").
                            child(user.getUserID()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getUserID()).child("followers").
                            child(fuser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(fuser.getUid()).child("following").
                            child(user.getUserID()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getUserID()).child("followers").
                            child(fuser.getUid()).removeValue();
                }
            }
        });
    }

    private void isFollow(String userID, Button follow) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("follow").
                child(fuser.getUid()).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userID).exists()){
                    follow.setText("following");
                }
                else follow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImage;
        TextView username,profession;
        Button follow;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            profession = itemView.findViewById(R.id.item_profession);
            follow = itemView.findViewById(R.id.follow_item);
        }
    }
}
