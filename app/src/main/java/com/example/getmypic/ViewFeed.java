package com.example.getmypic;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.TakePhoto;
import com.example.getmypic.Models.Users;


public class ViewFeed extends Fragment {
    private Posts post;

    public ViewFeed() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (Posts)getArguments().get("post");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inf = inflater.inflate(R.layout.fragment_view_feed, container, false);

        final Posts post = ViewFeedArgs.fromBundle(getArguments()).getPost();

        String imageUrl = post.getPostImageUrl();
        ImageView postImage = inf.findViewById(R.id.postImage);

        if (imageUrl.length() > 0){
            TakePhoto photo = new TakePhoto();
            String fileName = photo.getLocalImageFileName(imageUrl);
            postImage.setImageBitmap(photo.loadImageFromFile(fileName));
            postImage.setVisibility(View.VISIBLE);
        }
        else{
            postImage.setImageBitmap(null);
            postImage.setVisibility(View.GONE);
        }

        ((TextView) inf.findViewById(R.id.postWriter)).setText(post.getDisplayName());
        ((TextView) inf.findViewById(R.id.postDescription)).setText(post.getText());
        ((TextView) inf.findViewById(R.id.postDate)).setText(post.getUploadedDate());

        if (Users.isAuthenticated() &&
                post.getUserEmail().equals(Users.getUser().getEmail())) {
            inf.findViewById(R.id.postActions).setVisibility(View.VISIBLE);
        }
        else {
            inf.findViewById(R.id.postActions).setVisibility(View.GONE);
        }

        inf.findViewById(R.id.postGotoEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFeedDirections.ActionViewFeedToCreateFeed actionViewFeedsToCreateFeed = ViewFeedDirections.actionViewFeedToCreateFeed();
                actionViewFeedsToCreateFeed.setPost(post);
                ((MainActivity) getActivity()).navController.navigate(actionViewFeedsToCreateFeed);
            }
        });

        inf.findViewById(R.id.postGotoDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFeedDirections.ActionViewFeedToRemovePost actionViewFeedToRemovePost = ViewFeedDirections.actionViewFeedToRemovePost();
                actionViewFeedToRemovePost.setPost(post);
                ((MainActivity) getActivity()).navController.navigate(actionViewFeedToRemovePost);
            }
        });

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("WatchMe! - Post");

        return inf;
    }
}
