package com.example.getmypic.Models;

import android.graphics.Bitmap;

import java.util.List;

public class MainModel {
    final public static MainModel instance = new MainModel();

    private SQLite modelSql;
    private Firebase modelFirebase;
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

    public interface DeletePostListener{
        void onComplete(boolean success);
    }
    public void deletePost(Posts post, DeletePostListener listener) {
        //TODO: fix async impl
        modelFirebase.deletePost(post, listener);
    }



    public interface GetImageListener{
        void onComplete(Bitmap image);
    }
    public void  getImage(String url, GetImageListener listener){
        modelFirebase.getImage(url, listener);
    }

    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, listener);
    }
}