package com.example.getmypic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.PostAsyncDao;
import com.example.getmypic.Models.Posts;

import java.util.List;

public class RemoveUser extends DialogFragment {
    private static int numberOfPostsToRemove = 0;

    public RemoveUser() {
        // Required empty public constructor
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final NavController navController = Navigation.findNavController(getActivity(), R.id.get_my_pic_nav_graph);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to remove all your posts ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final List<Posts> postsToRemove = Firebase.getUserPosts();
                        numberOfPostsToRemove = postsToRemove.size();

                        for(final Posts post : postsToRemove){
                            Firebase fb = new Firebase();
                            fb.deletePost(post, new MainModel.DeletePostListener() {
                                @Override
                                public void onComplete(boolean success) {
                                    numberOfPostsToRemove--;
                                    if (numberOfPostsToRemove == 0) {
                                        navController.navigate(R.id.logout);
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (navController.getCurrentDestination().getId() == R.id.removeUser){
                            navController.popBackStack();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
