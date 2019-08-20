package com.example.getmypic.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.getmypic.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostViewHolder> {
    private List<Posts> mDataset;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onClick(int index, String source);
    }

    public void setData(List<Posts> newData) {
        this.mDataset = newData;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView postWriter;
        public TextView postDate;
        public TextView postDescription;
        public ImageView postImage;
        public ProgressBar postImageLoading;
        public LinearLayout postActions;
        public ImageButton postGotoEdit;
        public ImageButton postGotoDelete;

        public PostViewHolder(View postView,  final OnItemClickListener listener) {
            super(postView);
            postWriter = postView.findViewById(R.id.postWriter);
            postDescription = postView.findViewById(R.id.postDescription);
            postDate = postView.findViewById(R.id.postDate);
            postImage = postView.findViewById(R.id.postImage);
            postImageLoading = postView.findViewById(R.id.postImageLoading);
            postActions = postView.findViewById(R.id.postActions);
            postGotoEdit = postView.findViewById(R.id.postGotoEdit);
            postGotoDelete = postView.findViewById(R.id.postGotoDelete);

            View.OnClickListener postImageListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    final String source = "postImage";
                    if (listener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index, source);
                        }
                    }
                }
            };
            postImage.setOnClickListener(postImageListener);
            postDescription.setOnClickListener(postImageListener);
            postWriter.setOnClickListener(postImageListener);
            postDate.setOnClickListener(postImageListener);
            postGotoEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    final String source = "postGotoEdit";
                    if (listener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index, source);
                        }
                    }
                }
            });
            postGotoDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    final String source = "postGotoDelete";
                    if (listener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index, source);
                        }
                    }
                }
            });
        }
    }

    public PostsListAdapter(Context context, List<Posts> postsList) {
        if (context != null){
            this.mInflater = LayoutInflater.from(context);
        }
        this.mDataset = postsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public PostsListAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_view_feed, parent, false);
        return new PostViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        /*final Posts post = mDataset[position];
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
        }*/

        String imageUrl = mDataset.get(position).getPostImageUrl();

        if (imageUrl != null && imageUrl.length() > 0){
            TakePhoto photo = new TakePhoto();
            String fileName = photo.getLocalImageFileName(imageUrl);
            Bitmap image = photo.loadImageFromFile(fileName);
            if (image != null) {
                holder.postImage.setImageBitmap(image);
                holder.postImage.setVisibility(View.VISIBLE);
                holder.postImageLoading.setVisibility(View.GONE);
            } else {
                holder.postImage.setVisibility(View.GONE);
                holder.postImageLoading.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.postImage.setImageBitmap(null);
            holder.postImage.setVisibility(View.GONE);
            holder.postImageLoading.setVisibility(View.GONE);
        }

        holder.postWriter.setText(mDataset.get(position).getDisplayName());
        holder.postDate.setText(mDataset.get(position).getUploadedDate().toString());
        holder.postDescription.setText(mDataset.get(position).getText());

        if (Users.isAuthenticated() &&
                mDataset.get(position).getUserEmail().equals(Users.getUser().getEmail())) {
            holder.postActions.setVisibility(View.VISIBLE);
        }
        else {
            holder.postActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(mDataset != null) {
            return mDataset.size();
        }
        return 0;
    }
}