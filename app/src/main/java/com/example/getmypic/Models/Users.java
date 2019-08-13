package com.example.getmypic.Models;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzey;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;

import java.util.List;

@Keep
public class Users {
    public String displayName;
    public String emailAddress;
    public String googleId;
    public String facebookId;
    public String imagePath;

    private static final boolean devMode = false;

    public Users(String emailAddress, String displayName, String facebookId, String imagePath) {
        this.emailAddress = emailAddress;
        this.facebookId = facebookId;
        this.imagePath = imagePath;
        this.displayName = displayName;
    }

    public static FirebaseUser getUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (devMode) {
            return devModeUser;
        }

        return user;
    }

    public static boolean isAuthenticated() {
        FirebaseUser user = getUser();
        return (user != null && !user.isAnonymous());
    }

    private static FirebaseUser devModeUser = new FirebaseUser() {
        @NonNull
        @Override
        public String getUid() {
            return "123456789";
        }

        @NonNull
        @Override
        public String getProviderId() {
            return null;
        }

        @Override
        public boolean isAnonymous() {
            return false;
        }

        @Nullable
        @Override
        public List<String> zza() {
            return null;
        }

        @NonNull
        @Override
        public List<? extends UserInfo> getProviderData() {
            return null;
        }

        @NonNull
        @Override
        public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
            return null;
        }

        @Override
        public FirebaseUser zzb() {
            return null;
        }

        @NonNull
        @Override
        public FirebaseApp zzc() {
            return null;
        }

        @Nullable
        @Override
        public String getDisplayName() {
            return "Developer";
        }

        @Nullable
        @Override
        public Uri getPhotoUrl() {
            return Uri.parse("https://developer.android.com/images/jetpack/jetpack-hero.svg");
        }

        @Nullable
        @Override
        public String getEmail() {
            return "watchme_developer@watchme.gmail.com";
        }

        @Nullable
        @Override
        public String getPhoneNumber() {
            return null;
        }

        @Nullable
        @Override
        public String zzd() {
            return null;
        }

        @NonNull
        @Override
        public zzey zze() {
            return null;
        }

        @Override
        public void zza(@NonNull zzey zzey) {

        }

        @NonNull
        @Override
        public String zzf() {
            return null;
        }

        @NonNull
        @Override
        public String zzg() {
            return null;
        }

        @Nullable
        @Override
        public FirebaseUserMetadata getMetadata() {
            return null;
        }

        @NonNull
        @Override
        public zzz zzh() {
            return null;
        }

        @Override
        public void zzb(List<zzy> list) {

        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }

        @Override
        public boolean isEmailVerified() {
            return false;
        }
    };
}
