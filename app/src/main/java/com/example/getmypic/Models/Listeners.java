package com.example.getmypic.Models;

import com.google.firebase.firestore.DocumentChange;

import java.util.List;

public class Listeners {
    public interface AddPostListener {
        void onComplete(String postId);
    }

    public interface EditPostListener {
        void onComplete(boolean success);
    }

    public interface DeletePostListener {
        void onComplete(boolean success);
    }

    public interface GetPosts {
        void onComplete(List<Posts> posts);
    }

    public interface GetPostsChangesListener {
        void onChange(DocumentChange change);
    }

    public interface SetImageListener {
        void onComplete(String imageUri);
    }

    public interface DeleteImageListener {
        void onComplete(boolean success);
    }

    public interface DataManagerImageUpdate {
        void onUpdate();
    }
}
