package com.example.getmypic.Models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Posts implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String text;
    private String postImageUrl;
    private String userEmail;
    //private Date uploadedDate;
    private String uploadedDate;

    public Posts(String id, String text, String postImageUrl, String userEmail, String uploadedDate) {
        this.id = id;
        this.text = text;
        this.postImageUrl = postImageUrl;
        this.userEmail = userEmail;
        this.uploadedDate = uploadedDate;
    }

    public Posts(Map<String, Object> post) {

        if (post.containsKey("id") &&
                post.get("id") != null) {
            this.id = post.get("id").toString();
        }

        if (post.containsKey("text") &&
                post.get("text") != null) {
            this.text = post.get("text").toString();
        }

        if (post.containsKey("postImageUrl") &&
                post.get("postImageUrl") != null) {
            this.postImageUrl = post.get("postImageUrl").toString();
        }

        if (post.containsKey("userEmail") &&
                post.get("userEmail") != null) {
            this.userEmail = post.get("userEmail").toString();
        }

        if (post.containsKey("uploadedDate") &&
                post.get("uploadedDate") != null) {
            //this.uploadedDate = ((Timestamp) post.get("uploadedDate")).toDate();
            this.uploadedDate = post.get("uploadedDate").toString();
        }
    }

    public String getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getPostImageUrl() {
        return this.postImageUrl;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    /*public Date getUploadedDate() {
        return this.uploadedDate;
    }*/

    public String getUploadedDate() {
        return this.uploadedDate;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", this.text);
        map.put("postImageUrl", this.postImageUrl);
        map.put("userEmail", this.userEmail);
        //map.put("uploadedDate", new Timestamp(this.uploadedDate));
        map.put("uploadedDate", this.uploadedDate);
        return map;
    }

    public static LiveData<List<Posts>> getAllPosts() {
        final MutableLiveData<List<Posts>> data = new MutableLiveData<>();
        Firebase.getPosts(new Listeners.GetPosts() {
            @Override
            public void onComplete(List<Posts> posts) {
                data.setValue(posts);
            }
        });
        return data;
    }
}
