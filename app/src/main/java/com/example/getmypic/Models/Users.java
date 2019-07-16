package com.example.getmypic.Models;

import android.net.Uri;
import android.provider.ContactsContract;

public class Users{
    public String givenName;
    public String familyName;
    public String emailAddress;
    public String googleId;
    public String facebookId;
    public Uri imagePath;

    public Users() {

    }

    public Users(String givenName, String familyName, String emailAddress, String googleId, String facebookId, Uri imagePath){
        this.emailAddress = emailAddress;
        this.familyName = familyName;
        this.givenName = givenName;
        this.googleId = googleId;
        this.facebookId = facebookId;
        this.imagePath = imagePath;
    }

    public String getEmailAddress(){
        return  emailAddress;
    }

    public String getGivenName(){
        return  givenName;
    }

    public  String getFamilyName(){
        return  familyName;
    }

    public String getGoogleId(){
        return  googleId;
    }

    public String getFacebookId(){
        return facebookId;
    }

    public  Uri getImagePath(){
        return  imagePath;
    }
}
