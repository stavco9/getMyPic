package com.example.getmypic;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmypic.Models.Posts;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.ViewFeedRowHolder> {
    List<Posts> mData;
    OnItemClickListener mListener;

    public PostsListAdapter(List<Posts> data) {
        mData = data;
    }
    interface OnItemClickListener{
        void onClick(int index);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewFeedRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_view_feed, viewGroup,false);
        ViewFeedRowHolder viewHolder = new ViewFeedRowHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFeedRowHolder postRowViewHolder, int i) {
        Posts post = mData.get(i);
        postRowViewHolder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewFeedRowHolder extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mName;
        TextView mId;
        CheckBox mCb;
        ProgressBar mAvatarProgress;
        public ViewFeedRowHolder(@NonNull View itemView,
                                 final OnItemClickListener listener) {
            super(itemView);
            //mAvatar = itemView.findViewById(R.id.strow_avatar_img);
            //mName = itemView.findViewById(R.id.strow_name_tv);
            //mId = itemView.findViewById(R.id.strow_id_tv);
            //mCb = itemView.findViewById(R.id.strow_checkbox);
            //mAvatarProgress = itemView.findViewById(R.id.strow_avatar_progress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (listener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index);
                        }
                    }
                }
            });

            mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int index = getAdapterPosition();
                    Log.d("TAG","chewckbox value change to: " + isChecked + " on item " + index);
                }
            });
        }

        public void bind(final Posts post){
            mName.setText("text: " + post.getText());
            mId.setText("id: " + post.getId());
            mCb.setChecked(false);
            mAvatar.setTag(post.getId());
            //mAvatar.setImageResource(R.drawable);

            if(post.getPostImageUrl() != null) {
                Picasso.get().setIndicatorsEnabled(true);
                Target target = new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (mAvatar.getTag() == post.getId()) {
                            mAvatar.setImageBitmap(bitmap);
                            mAvatarProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        mAvatarProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        mAvatarProgress.setVisibility(View.VISIBLE);
                    }
                };
                Picasso.get().load(post.getPostImageUrl())
                        //.placeholder(R.drawable.avatar)
                        .into(target);

            }else{
                mAvatarProgress.setVisibility(View.INVISIBLE);
            }
        }
    }
}
