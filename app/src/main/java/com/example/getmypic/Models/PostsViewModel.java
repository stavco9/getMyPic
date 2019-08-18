package com.example.getmypic.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PostsViewModel extends ViewModel {
    private static LiveData<List<Posts>> data = Posts.getAllPosts();
    public static LiveData<List<Posts>> getAllPosts() {
        return data;
    }
}
