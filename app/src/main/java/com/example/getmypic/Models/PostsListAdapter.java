package com.example.getmypic.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.getmypic.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostViewHolder> {
    private Posts[] mDataset;
    private LayoutInflater mInflater;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView postWriter;
        public TextView postDate;
        public TextView postDescription;
        public ImageView postImage;
        public ProgressBar postImageLoading;

        public PostViewHolder(View postView) {
            super(postView);
            postWriter = postView.findViewById(R.id.postWriter);
            postDescription = postView.findViewById(R.id.postDescription);
            postDate = postView.findViewById(R.id.postDate);
            postImage = postView.findViewById(R.id.postImage);
            postImageLoading = postView.findViewById(R.id.postImageLoading);
        }
    }

    public PostsListAdapter(Context context, Posts[] postsList) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataset = postsList;
    }

    @Override
    public PostsListAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_view_feed, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        final Posts post = mDataset[position];
        holder.postWriter.setText(post.getUserEmail());
        holder.postDate.setText(post.getUploadedDate());
        holder.postDescription.setText(post.getText());
        holder.postImage.setTag(post.getId());

        if(post.getPostImageUrl() != null && post.getPostImageUrl() != "") {
            Picasso.get().setIndicatorsEnabled(true);
            Target target = new Target(){
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (holder.postImage.getTag() == post.getId()) {
                        holder.postImage.setImageBitmap(bitmap);
                        holder.postImageLoading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    holder.postImageLoading.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    holder.postImageLoading.setVisibility(View.VISIBLE);
                }
            };
            //Picasso.get().load(post.getPostImageUrl())
                    //.placeholder(R.drawable.avatar)
              //      .into(target);

        }else{
            holder.postImageLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}