package com.example.getmypic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getmypic.Models.DataManager;
import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.Listeners;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.PostAsyncDao;
import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.PostsListAdapter;
import com.example.getmypic.Models.PostsViewModel;
import com.example.getmypic.Models.TakePhoto;

import java.util.List;

public class ListFeeds extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private PostsViewModel postsViewModel;
    private List<Posts> fragmentPosts;

    public ListFeeds() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataManager.listeners.add(new Listeners.DataManagerImageUpdate() {
            @Override
            public void onUpdate() {
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
        recyclerViewAdapter = new PostsListAdapter(getContext(), fragmentPosts);
        ((PostsListAdapter) recyclerViewAdapter).setOnItemClickListener(new PostsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index, String source) {
                Posts post = fragmentPosts.get(index);

                if (source.equals("postImage")) {
                    ListFeedsDirections.ActionListFeedsToViewFeed actionListFeedsToViewFeed = ListFeedsDirections.actionListFeedsToViewFeed(post);
                    ((MainActivity) getActivity()).navController.navigate(actionListFeedsToViewFeed);
                } else if (source.equals("postGotoEdit")) {
                    ListFeedsDirections.ActionListFeedsToCreateFeed actionListFeedsToCreateFeed = ListFeedsDirections.actionListFeedsToCreateFeed();
                    actionListFeedsToCreateFeed.setPost(post);
                    ((MainActivity) getActivity()).navController.navigate(actionListFeedsToCreateFeed);
                } else if (source.equals("postGotoDelete")) {
                    ListFeedsDirections.ActionListFeedsToRemovePost actionListFeedsToRemovePost = ListFeedsDirections.actionListFeedsToRemovePost();
                    actionListFeedsToRemovePost.setPost(post);
                    ((MainActivity) getActivity()).navController.navigate(actionListFeedsToRemovePost);
                }
            }
        });
        postsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);
        postsViewModel.getAllPosts().observe(this, new Observer<List<Posts>>() {
            @Override
            public void onChanged(final List<Posts> data) {
                fragmentPosts = data;
                ((PostsListAdapter) recyclerViewAdapter).setData(fragmentPosts);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });
    }

    // This function needs to move to main activity
    public void syncImages(final String url) {

        if (url.length() > 0) {
            final TakePhoto photo = new TakePhoto();
            final String fileName = photo.getLocalImageFileName(url);

            Bitmap checkimage = photo.loadImageFromFile(fileName);

            if (checkimage == null) {
                Firebase.getImage(url, new MainModel.GetImageListener() {
                    @Override
                    public void onComplete(Bitmap image) {
                        photo.saveImageToFile(image, fileName);
                    }
                });
            }
        }
    }

    private boolean isIMatchId(List<Posts> firebaseposts, Posts localPost) {
        for (Posts firebasePost : firebaseposts) {
            if (firebasePost.getId().equals(localPost.getId())) {
                return true;
            }
        }

        return false;
    }

    // This function needs to move to mainActivity
    public void syncPosts(final List<Posts> localPosts) {
        Firebase.getAllPosts(new MainModel.GetAllPostsListener() {
            @Override
            public void onComplete(List<Posts> firebasePosts) {

                for (final Posts post : firebasePosts) {

                    PostAsyncDao.setPosts(post, new MainModel.AddPostListener() {
                        @Override
                        public void onComplete(boolean success) {
                            syncImages(post.getPostImageUrl());
                        }
                    });
                }

                for (final Posts post : localPosts) {
                    if (!isIMatchId(firebasePosts, post)) {
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
        View view = inflater.inflate(R.layout.fragment_list_feeds, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("WatchMe! - Home");

        recyclerView = view.findViewById(R.id.listfeeds_recyclerview);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (!GetMyPicApplication.isInternetAvailable()) {
            PostAsyncDao.getAllPosts(new MainModel.GetAllPostsListener() {
                @Override
                public void onComplete(final List<Posts> data) {
                    fragmentPosts = data;
                    ((PostsListAdapter) recyclerViewAdapter).setData(fragmentPosts);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            });
        }

        return view;
    }
}
