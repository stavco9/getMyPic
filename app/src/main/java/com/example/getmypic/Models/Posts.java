package com.example.getmypic.Models;

import java.text.DateFormat;

public class Posts {
    private String text;
    private String postImageUrl;
    private Users ownerUser;
    private DateFormat uploadedDate;

    public Posts(String text, String postImageUrl, Users ownerUser, DateFormat uploadedDate) {
        this.text = text;
        this.postImageUrl = postImageUrl;
        this.ownerUser = ownerUser;
        this.uploadedDate = uploadedDate;
    }

    public String getText(){
        return this.text;
    }

    public String getPostImageUrl(){
        return this.postImageUrl;
    }

    public Users getOwnerUser(){
        return this.ownerUser;
    }

    public DateFormat getUploadedDate(){
        return this.uploadedDate;
    }
}
