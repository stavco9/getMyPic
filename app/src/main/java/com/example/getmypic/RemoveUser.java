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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoveUser.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RemoveUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemoveUser extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int counterRemove = 0;

    private OnFragmentInteractionListener mListener;

    public RemoveUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemoveUser.
     */
    // TODO: Rename and change types and number of parameters
    public static RemoveUser newInstance(String param1, String param2) {
        RemoveUser fragment = new RemoveUser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

                        final int postsSize = postsToRemove.size();

                        for(final Posts post : postsToRemove){
                            Firebase fb = new Firebase();
                            fb.deletePost(post, new MainModel.DeletePostListener() {
                                @Override
                                public void onComplete(boolean success) {

                                    if (success){

                                        counterRemove++;

                                        PostAsyncDao.deletePost(post, new MainModel.DeletePostListener() {
                                            @Override
                                            public void onComplete(boolean success) {
                                                if (counterRemove == postsSize){
                                                    navController.popBackStack(R.id.listFeeds, false);
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        navController.popBackStack();
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        navController.popBackStack();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
