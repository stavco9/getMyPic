package com.example.getmypic.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firestore.v1.DocumentTransform;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Posts implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String text;
    private String postImageUrl;
    private String userEmail;
    private String uploadedDate;

    public Posts(String id, String text, String postImageUrl, String userEmail, String uploadedDate) {
        this.id = id;
        this.text = text;
        this.postImageUrl = postImageUrl;
        this.userEmail = userEmail;
        this.uploadedDate = uploadedDate;
    }

    public Posts(Map<String, Object> post){

        if (post.containsKey("id") &&
                post.get("id") != null){
            this.id = post.get("id").toString();
        }

        if (post.containsKey("text") &&
                post.get("text") != null){
            this.text = post.get("text").toString();
        }

        if (post.containsKey("postImageUrl") &&
                post.get("postImageUrl") != null){
            this.postImageUrl = post.get("postImageUrl").toString();
        }

        if (post.containsKey("userEmail") &&
                post.get("userEmail") != null){
            this.userEmail = post.get("userEmail").toString();
        }

        if (post.containsKey("uploadedDate") &&
                post.get("uploadedDate") != null){
            this.uploadedDate = post.get("uploadedDate").toString();
        }
    }

    public String getId(){
        return this.id;
    }

    public String getText(){
        return this.text;
    }

    public String getPostImageUrl(){
        return this.postImageUrl;
    }

    public String getUserEmail(){
        return this.userEmail;
    }

    public String getUploadedDate(){
        return this.uploadedDate;
    }

}
