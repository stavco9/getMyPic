package com.example.getmypic.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Firebase {
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static int nextId;
    private static List<Posts> listPosts;

    static {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
        storage = FirebaseStorage.getInstance();
    }

    public static void addPost(Posts post, final Listeners.AddPostListener listener) {
        Map<String, Object> postMap = post.toMap();
        postMap.put("uploadedDate", FieldValue.serverTimestamp());
        db.collection("posts").add(post.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.onComplete(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onComplete(null);
                    }
                });
    }

    public static void editPost(Posts post, final Listeners.EditPostListener listener) {
        db.collection("posts").document(post.getId()).set(post.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onComplete(false);
                    }
                });
    }

    public static void deletePost(Posts post, final Listeners.DeletePostListener listener) {
        db.collection("posts").document(post.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onComplete(false);
                    }
                });
    }

    public static void getPosts(final Listeners.GetPosts listener) {
        db.collection("posts").orderBy("uploadedDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        List<Posts> posts = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            posts.add(new Posts(doc.getData()));
                        }
                        listener.onComplete(posts);
                    }
                });
    }

    public static void getPostsChanges(final Listeners.GetPostsChangesListener listener) {
        db.collection("posts").orderBy("uploadedDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            listener.onChange(dc);
                            /*switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }*/
                        }

                    }
                });
    }

    public static void setImage(Bitmap imageBitmap, String imageName, final Listeners.SetImageListener listener) {
        final StorageReference imageStorageRef = storage.getReference().child(imageName + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageStorageRef.putBytes(data).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    listener.onComplete(task.getResult().toString());
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public static void deleteImage(String imageName, final Listeners.DeleteImageListener listener) {
        final StorageReference imageStorageRef = storage.getReference().child(imageName + ".jpg");
        imageStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                listener.onComplete(false);
            }
        });
    }

    public static void addCurrUser() {
        if (Users.isAuthenticated()) {
            FirebaseUser currUser = Users.getUser();

            Users userToDB = new Users(currUser.getEmail(), currUser.getDisplayName(), currUser.getUid(), currUser.getPhotoUrl().toString());

            try {
                db.collection("users").document(currUser.getEmail())
                        .set(userToDB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setNextId(List<Posts> posts) {

        int maxId = 0;

        for (Posts post : posts) {

            int currId = Integer.parseInt(post.getId());

            if (currId > maxId) {
                maxId = currId;
            }
        }

        nextId = maxId + 1;
    }

    private static void setListPosts(List<Posts> posts) {
        listPosts = posts;
    }

    public static List<Posts> getListPosts() {
        return listPosts;
    }

    public static List<Posts> getUserPosts() {
        if (Users.isAuthenticated()) {

            List<Posts> userPosts = new LinkedList<>();

            for (Posts posts : listPosts) {
                if (posts.getUserEmail().equals(Users.getUser().getEmail())) {
                    userPosts.add(posts);
                }
            }

            return userPosts;
        }

        return null;
    }

    public static int getNextId() {
        return nextId;
    }

    public static void addPost(Posts post, final MainModel.AddPostListener listener) {
        db.collection("posts").document(post.getId())
                .set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void deletePost(Posts post, final MainModel.DeletePostListener listener) {
        db.collection("posts").document(post.getId())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    interface GetPosts {
        void onComplete(Posts post);
    }

    public static void getPost(String id, final GetPosts listener) {
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

    public static void getAllPosts(final MainModel.GetAllPostsListener listener) {
        db.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<Posts> posts = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(posts);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                    try {
                        Posts post = new Posts(doc.getData());

                        posts.add(post);
                    } catch (RuntimeException exc) {
                        exc.printStackTrace();
                    }

                }

                setNextId(posts);
                setListPosts(posts);

                listener.onComplete(posts);
            }
        });
    }

    public static void getImage(String url, final MainModel.GetImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3 * ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if (task.isSuccessful()) {
                    byte[] bytes = task.getResult();
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    listener.onComplete(image);
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public static void saveImage(Bitmap imageBitmap, final MainModel.SaveImageListener listener) {
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