package com.example.getmypic.Models;

import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Firebase {
    FirebaseFirestore db;

    public Firebase(){
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
    }

    public void addCurrUser() {
        if (Users.isAuthenticated()){
            FirebaseUser currUser = Users.getUser();

            Users userToDB = new Users(currUser.getEmail(), currUser.getDisplayName(), currUser.getUid(), currUser.getPhotoUrl().toString());

            try{
                db.collection("users").document(currUser.getEmail())
                        .set(userToDB);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void addPost(Posts post, final MainModel.AddPostListener listener) {
        db.collection("posts").document(post.getId())
                .set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    interface GetPosts {
        void onComplete(Posts post);
    }

    public void getPost(String id, final GetPosts listener) {
        db.collection("posts").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            Posts post = snapshot.toObject(Posts.class);
                            listener.onComplete(post);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }

    public void getAllPosts(final MainModel.GetAllPostsListener listener) {
        db.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<Posts> posts = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(posts);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                    try{
                        Posts post = new Posts(doc.getData());

                        posts.add(post);
                    }
                    catch (RuntimeException exc){
                        exc.printStackTrace();
                    }

                }
                listener.onComplete(posts);
            }
        });
    }

    public void saveImage(Bitmap imageBitmap, final MainModel.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Date d = new Date();
        // Create a reference to "mountains.jpg"
        final StorageReference imageStorageRef = storageRef.child("image_" + d.getTime() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageStorageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    listener.onComplete(downloadUri.toString());
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }
}