package com.example.getmypic;

import android.content.Context;
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
        ((TextView) inf.findViewById(R.id.postWriter)).setText(this.post.getUserEmail());
        ((TextView) inf.findViewById(R.id.postDescription)).setText(this.post.getText());
        ((TextView) inf.findViewById(R.id.postDate)).setText(this.post.getUploadedDate());
        ((ImageView) inf.findViewById(R.id.postImage)).setImageURI(Uri.parse(this.post.getPostImageUrl()));
        return inf;
    }
}
