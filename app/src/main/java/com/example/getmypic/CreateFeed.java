package com.example.getmypic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.Users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateFeed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFeed extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText createFeedTxt;

    private Button createFeedSubmitBtn;

    private int count;

    private OnFragmentInteractionListener mListener;

    public CreateFeed() {
        // Required empty public constructor
    }

    private void submitFeed(){
        createFeedSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firebase fb = new Firebase();

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                View createFeedView = v.getRootView();

                createFeedTxt = (EditText) createFeedView.findViewById(R.id.create_feed_txt);

                Posts post = new Posts(Integer.toString(count + 1), createFeedTxt.getText().toString(), "", Users.getUser().getEmail(), dateFormat.format(date));

                fb.addPost(post, new MainModel.AddPostListener() {
                    @Override
                    public void onComplete(boolean success) {
                        if (success){
                            NavController navController = Navigation.findNavController(getActivity(), R.id.get_my_pic_nav_graph);
                            //navController.navigate(R.id.action_createFeed_to_listFeeds);
                        }
                    }
                });
            }
        });
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateFeed.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFeed newInstance(String param1, String param2) {
        CreateFeed fragment = new CreateFeed();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View createFeedView = inflater.inflate(R.layout.fragment_create_feed, container, false);

        Firebase fb = new Firebase();

        fb.getAllPosts(new MainModel.GetAllPostsListener() {
            @Override
            public void onComplete(List<Posts> data) {
                count  = data.size();

                submitFeed();
            }
        });

        createFeedSubmitBtn = (Button) createFeedView.findViewById(R.id.create_feed_submit_btn);

        return  createFeedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
