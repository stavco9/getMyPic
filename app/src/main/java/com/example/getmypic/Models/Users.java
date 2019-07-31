package com.example.getmypic.Models;

import android.net.Uri;
import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Users{
    public String givenName;
    public String familyName;
    public String emailAddress;
    public String googleId;
    public String facebookId;
    public Uri imagePath;


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
