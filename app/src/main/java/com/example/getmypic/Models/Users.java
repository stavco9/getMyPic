package com.example.getmypic.Models;

import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.Keep;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@Keep
public class Users{
    public String displayName;
    public String emailAddress;
    public String googleId;
    public String facebookId;
    public String imagePath;

    public  Users(String emailAddress, String displayName, String facebookId, String imagePath){
        this.emailAddress = emailAddress;
        this.facebookId = facebookId;
        this.imagePath = imagePath;
        this.displayName = displayName;
    }

    public static FirebaseUser getUser(){
        if (isAuthenticated()){
            return FirebaseAuth.getInstance().getCurrentUser();
        }
        return  null;
    }



    public static boolean isAuthenticated(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            return true;
        return false;
    }
}
