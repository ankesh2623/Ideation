package com.example.ideation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ideation.CommentPageActivity;
import com.example.ideation.Model.PostModel;
import com.example.ideation.Model.UserModel;
import com.example.ideation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.HomeViewHolder> {

    private List<PostModel> posts = new ArrayList<>();
    private Context context;
    FirebaseFirestore fstore;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    boolean toshow;
    DatabaseReference ref;

    public PostAdapter(List<PostModel> posts, Context context, boolean toshow) {
        this.posts = posts;
        this.context = context;
        this.toshow = toshow;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        fstore = FirebaseFirestore.getInstance();
        View view = LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        PostModel post = posts.get(position);
        ref = FirebaseDatabase.getInstance().getReference();

        if (post.getUserID().equals(uid)){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ref.child("posts").child(post.getPostUrl()).removeValue();
                    ref.child("likes").child(post.getPostUrl()).removeValue();
                    ref.child("comments").child(post.getPostUrl()).removeValue();
                    ref.child("saves").child(post.getPostUrl()).removeValue();
                }
            });
        }

        holder.overView.setText(post.getOverview());
        holder.description.setText(post.getDescription());
        ref.child("Users").
                child(post.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                holder.profession.setText(user.getProfession());
                holder.userName.setText(user.getUserName());
                if (user.getImageURL().equals("default")){
                    holder.profileImage.setImageResource(R.drawable.avatar);
                }
                else Picasso.get().load(user.getImageURL()).into(holder.profileImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.time.setText(post.getTime());

        isLiked(post.getPostUrl(),holder.like,holder.likecount);

        getSaveCount(post.getPostUrl(),holder.savecount, holder.bookmark);

        getCommentCount(post.getPostUrl(),holder.commentcount);

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.bookmark.getTag().equals(0))
                    ref.child("saves").child(post.getPostUrl()).child(uid).setValue(true);
                else
                    ref.child("saves").child(post.getPostUrl()).child(uid).removeValue();
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoComments(post.getPostUrl(), post.getUserID());
            }
        });
        holder.commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoComments(post.getPostUrl(), post.getUserID());
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals(0)){
                    ref.child("likes").child(post.getPostUrl()).
                            child(uid).setValue(true);
                }
                else{
                    ref.child("likes").child(post.getPostUrl()).child(uid).removeValue();
                }
            }
        });
    }

    private void getSaveCount(String postUrl, TextView savecount, ImageButton bookmark) {
        ref.child("saves").child(postUrl).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        savecount.setText(snapshot.getChildrenCount()+" saves");
                        if (snapshot.child(uid).exists()){
                            bookmark.setTag(1);
                            bookmark.setImageResource(R.drawable.ic_bookmarked);
                        }
                        else{
                            bookmark.setTag(0);
                            bookmark.setImageResource(R.drawable.bookmarks_vec);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, profession, overView, description;
        private TextView likecount,commentcount,savecount,time, delete;
        private ImageView profileImage;
        private ImageButton like, bookmark;
        private LinearLayout comment;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.postUserName);
            profession = itemView.findViewById(R.id.postProfession);
            time = itemView.findViewById(R.id.postTime);
            overView = itemView.findViewById(R.id.PostOverView);
            description = itemView.findViewById(R.id.postdescription);
            profileImage = itemView.findViewById(R.id.postProfile);
            like = itemView.findViewById(R.id.postLike);
            comment = itemView.findViewById(R.id.post_comment);
            bookmark = itemView.findViewById(R.id.post_bookmark);
            likecount = itemView.findViewById(R.id.likecount);
            commentcount = itemView.findViewById(R.id.commentCount);
            savecount = itemView.findViewById(R.id.shaveCount);
            delete = itemView.findViewById(R.id.deletePost);
        }
    }

    private void isLiked(String postId, ImageButton button, TextView likecount){
        ref.child("likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likecount.setText(snapshot.getChildrenCount()+" likes");
                if (snapshot.child(uid).exists()){
                    button.setTag(1);
                    button.setImageResource(R.drawable.ic_liked);
                }
                else {
                    button.setTag(0);
                    button.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCommentCount(String postID, TextView count){
        ref.child("comments").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count.setText(snapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void gotoComments(String posturl,String postUserId){
        Intent intent = new Intent(context, CommentPageActivity.class);
        intent.putExtra("postId",posturl);
        intent.putExtra("authorId",postUserId);
        context.startActivity(intent);
    }

}
