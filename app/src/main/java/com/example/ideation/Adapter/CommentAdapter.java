package com.example.ideation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ideation.Model.CommentModel;
import com.example.ideation.Model.UserModel;
import com.example.ideation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context context;
    private List<CommentModel> commentList;
    private FirebaseUser fuser;

    public CommentAdapter(Context context, List<CommentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        CommentModel comment = commentList.get(position);

        holder.comment.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user != null) {
                            holder.username.setText(user.getUserName());

                            if (!user.getImageURL().equals("default")){
                                Picasso.get().load(user.getImageURL()).into(holder.profileImage);
                            }
                        } else {
                            holder.username.setText("Unknown User");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView username;
        TextView comment;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.commentItemProfile);
            username = itemView.findViewById(R.id.username_comment);
            comment = itemView.findViewById(R.id.shownComment);
        }
    }
}

//package com.example.ideation.Adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ideation.Model.CommentModel;
//import com.example.ideation.Model.UserModel;
//import com.example.ideation.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
//
//    private Context context;
//    private List<CommentModel> commnetL;
//    private FirebaseUser fuser;
//
//    public CommentAdapter(Context context, List<CommentModel> commnetL) {
//        this.context = context;
//        this.commnetL = commnetL;
//    }
//
//    @NonNull
//    @Override
//    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
//        return new CommentHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
//        fuser = FirebaseAuth.getInstance().getCurrentUser();
//        CommentModel comment = commnetL.get(position);
//
//        holder.comment.setText(comment.getComment());
//
//        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.
//                getPublisher()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                UserModel user = snapshot.getValue(UserModel.class);
//                holder.username.setText(user.getUserName());
//                if (!user.getImageURL().equals("default")){
//                    Picasso.get().load(user.getImageURL()).into(holder.profileImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return commnetL.size();
//    }
//
//    public class CommentHolder extends RecyclerView.ViewHolder {
//
//        ImageView profileImage;
//        TextView username;
//        TextView comment;
//        public CommentHolder(@NonNull View itemView) {
//            super(itemView);
//            profileImage = itemView.findViewById(R.id.commentItemProfile);
//            username = itemView.findViewById(R.id.username_comment);
//            comment = itemView.findViewById(R.id.shownComment);
//        }
//    }
//}
