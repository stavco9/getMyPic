package com.example.getmypic.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firestore.v1.DocumentTransform;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

@Entity
public class Posts {
    @PrimaryKey
    @NonNull
    private String id;
    private String text;
    private String postImageUrl;
    private String userEmail;
    private Long uploadedDate;

    public Posts(String id, String text, String postImageUrl, String userEmail, Long uploadedDate) {
        this.id = id;
        this.text = text;
        this.postImageUrl = postImageUrl;
        this.userEmail = userEmail;
        this.uploadedDate = uploadedDate;
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

    public Long getUploadedDate(){
        return this.uploadedDate;
    }
}
