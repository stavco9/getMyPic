package com.example.getmypic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.PostAsyncDao;
import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.PostsListAdapter;
import com.example.getmypic.Models.TakePhoto;

import java.util.List;

public class ListFeeds extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    public ListFeeds() {
        // Required empty public constructor
    }

    // This function needs to move to main activity
    public void syncImages(final String url){

        if (url.length() > 0){
            final TakePhoto photo = new TakePhoto();
            final String fileName = photo.getLocalImageFileName(url);

            Bitmap checkimage = photo.loadImageFromFile(fileName);

            if (checkimage == null){
                Firebase.getImage(url, new MainModel.GetImageListener() {
                    @Override
                    public void onComplete(Bitmap image) {
                        photo.saveImageToFile(image, fileName);
                    }
                });
            }
        }
    }

    private boolean isIMatchId(List<Posts> firebaseposts, Posts localPost){
        for(Posts firebasePost: firebaseposts){
            if (firebasePost.getId().equals(localPost.getId())){
                return true;
            }
        }

        return false;
    }

    // This function needs to move to mainActivity
    public void syncPosts(final List<Posts> localPosts){
        Firebase.getAllPosts(new MainModel.GetAllPostsListener() {
            @Override
            public void onComplete(List<Posts> firebasePosts) {

                for(final Posts post: firebasePosts){

                    PostAsyncDao.addPosts(post, new MainModel.AddPostListener() {
                        @Override
                        public void onComplete(boolean success) {
                            syncImages(post.getPostImageUrl());
                        }
                    });
                }

                for(final Posts post: localPosts){
                    if (!isIMatchId(firebasePosts, post)){
                        PostAsyncDao.deletePost(post, new MainModel.DeletePostListener() {
                            @Override
                            public void onComplete(boolean success) {

                            }
                        });
                    }
                }

                // This part needs to be removed from this function and get into a function that get's the posts from the local DB cache
                // instaed of Firebase
        /*recyclerViewAdapter = new PostsListAdapter(getContext(), data.toArray(new Posts[data.size()]));
        recyclerView.setAdapter(recyclerViewAdapter);*/
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_feeds, container,false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("WatchMe! - Home");

        recyclerView = view.findViewById(R.id.listfeeds_recyclerview);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        PostAsyncDao.getAllPosts(new MainModel.GetAllPostsListener() {
            @Override
            public void onComplete(final List<Posts> data) {

                syncPosts(data);

                recyclerViewAdapter = new PostsListAdapter(getContext(), data.toArray(new Posts[data.size()]));
                ((PostsListAdapter) recyclerViewAdapter).setOnItemClickListener(new PostsListAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int index, String source) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("post", data.get(index));

                        if (source.equals("postImage")) {
                            ((MainActivity) getActivity()).navController.navigate(R.id.action_listFeeds_to_viewFeed, bundle);
                        } else if (source.equals("postGotoEdit")) {
                            ((MainActivity) getActivity()).navController.navigate(R.id.action_listFeeds_to_createFeed, bundle);
                        } else if (source.equals("postGotoDelete")) {
                            ((MainActivity) getActivity()).navController.navigate(R.id.action_listFeeds_to_removePost, bundle);
                        }
                    }
                });
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });

        return view;
    }
}
