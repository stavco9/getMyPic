package com.example.getmypic.Models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.auth.User;

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
                db.collection("users").document(currUser.getUid())
                        .set(userToDB);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    interface GetStudentListener {
        void onComplete(Users student);
    }

    public void getUser(String id, final GetStudentListener listener) {
        db.collection("students").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            Users user = snapshot.toObject(Users.class);
                            listener.onComplete(user);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }
}
