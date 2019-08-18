package com.example.getmypic.Models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static List<Listeners.DataManagerImageUpdate> listeners = new ArrayList<>();

    public static void SyncAllPosts() {
        Firebase.getPostsChanges(new Listeners.GetPostsChangesListener() {
            @Override
            public void onChange(DocumentChange dc) {
                Posts p = new Posts(dc.getDocument().getData());
                final Posts postChange = new Posts(dc.getDocument().getId(), p.getText(), p.getPostImageUrl(), p.getUserEmail(), p.getUploadedDate());
                switch (dc.getType()) {
                    case ADDED:
                        PostAsyncDao.setPosts(postChange, new MainModel.AddPostListener() {
                            @Override
                            public void onComplete(boolean success) {
                                final String url = postChange.getPostImageUrl();
                                if (url.length() > 0) {
                                    final TakePhoto photo = new TakePhoto();
                                    final String fileName = photo.getLocalImageFileName(url);

                                    Bitmap checkimage = photo.loadImageFromFile(fileName);

                                    if (checkimage == null) {
                                        Firebase.getImage(url, new MainModel.GetImageListener() {
                                            @Override
                                            public void onComplete(Bitmap image) {
                                                if (image != null) {
                                                    photo.saveImageToFile(image, fileName);
                                                    for (Listeners.DataManagerImageUpdate listener : listeners) {
                                                        listener.onUpdate();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        break;
                    case MODIFIED:
                        PostAsyncDao.setPosts(postChange, new MainModel.AddPostListener() {
                            @Override
                            public void onComplete(boolean success) {
                                final String url = postChange.getPostImageUrl();
                                if (url.length() > 0) {
                                    final TakePhoto photo = new TakePhoto();
                                    final String fileName = photo.getLocalImageFileName(url);

                                    Bitmap checkimage = photo.loadImageFromFile(fileName);

                                    if (checkimage == null) {
                                        Firebase.getImage(url, new MainModel.GetImageListener() {
                                            @Override
                                            public void onComplete(Bitmap image) {
                                                if (image != null) {
                                                    photo.saveImageToFile(image, fileName);
                                                    for (Listeners.DataManagerImageUpdate listener : listeners) {
                                                        listener.onUpdate();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        break;
                    case REMOVED:
                        PostAsyncDao.deletePost(postChange, new MainModel.DeletePostListener() {
                            @Override
                            public void onComplete(boolean success) {

                            }
                        });
                        break;
                }
            }
        });
    }
}
