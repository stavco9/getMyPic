package com.example.getmypic.Models;

import android.graphics.Bitmap;

import java.util.List;

public class MainModel {
    final public static MainModel instance = new MainModel();

    SQLite modelSql;
    Firebase modelFirebase;
    private void Model() {
        modelSql = new SQLite();
        modelFirebase = new Firebase();
//        }
    }

    public interface GetAllPostsListener{
        void onComplete(List<Posts> data);
    }

    public interface AddPostListener{
        void onComplete(boolean success);
    }
    public void addPost(Posts post, AddPostListener listener) {
        //TODO: fix async impl
        modelFirebase.addPost(post, listener);
    }


    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, listener);
    }
}
