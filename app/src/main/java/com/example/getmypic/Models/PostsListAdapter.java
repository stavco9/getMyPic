package com.example.getmypic.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.getmypic.R;

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

        public PostViewHolder(View postView) {
            super(postView);
            postWriter = postView.findViewById(R.id.postWriter);
            postDescription = postView.findViewById(R.id.postDescription);
            postDate = postView.findViewById(R.id.postDate);
            postImage = postView.findViewById(R.id.postImage);
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

        String imageUrl = mDataset[position].getPostImageUrl();

        if (imageUrl.length() > 0){
            Firebase.getImage(imageUrl, new MainModel.GetImageListener() {
                @Override
                public void onComplete(Bitmap image) {
                    holder.postImage.setImageBitmap(image);
                    holder.postWriter.setText(mDataset[position].getUserEmail());
                    holder.postDate.setText(mDataset[position].getUploadedDate());
                    holder.postDescription.setText(mDataset[position].getText());
                }
            });
        } else {
            holder.postWriter.setText(mDataset[position].getUserEmail());
            holder.postDate.setText(mDataset[position].getUploadedDate());
            holder.postDescription.setText(mDataset[position].getText());
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}