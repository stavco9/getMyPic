package com.example.getmypic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.TakePhoto;
import com.example.getmypic.Models.Users;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private View createFeedView;
    private EditText createFeedTxt;
    private Button createFeedSubmitBtn;
    private Bitmap imageBitmap;
    private TakePhoto photo;
    private Button takePhotoGalleryBtn;
    private Button takePhotoCameraBtn;
    private TextView isUploadedView;
    private FrameLayout uploadedLayout;
    private ProgressBar waitingBar;
    private TextView uploadedText;
    private NavController navController;
    private boolean isCounted = false;
    private String firebaseImageUrl = "";
    private final int CAMERA_REQUEST_CODE = 1888;
    private final int GALLERY_REQUEST_CODE = 1889;
    private Firebase fb = new Firebase();
    private int count;

    private OnFragmentInteractionListener mListener;

    public CreateFeed() {
        // Required empty public constructor
    }

    private void addFinalPostTofirebase(List<Posts> data){

        isCounted = true;

        count  = data.size();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Posts post = new Posts(Integer.toString(count + 1), createFeedTxt.getText().toString(), firebaseImageUrl, Users.getUser().getEmail(), dateFormat.format(date));

        fb.addPost(post, new MainModel.AddPostListener() {

            @Override
            public void onComplete(boolean success) {

                uploadedLayout.setVisibility(View.VISIBLE);
                uploadedText.setVisibility(View.VISIBLE);
                waitingBar.setVisibility(View.GONE);

                navController = Navigation.findNavController(getActivity(), R.id.get_my_pic_nav_graph);

                if (success){

                    uploadedText.setText("Successfully added post !!!");

                    uploadedText.setTextColor(Color.GREEN);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadedLayout.setVisibility(View.GONE);
                            uploadedText.setVisibility(View.GONE);

                            navController.navigate(R.id.action_createFeed_to_listFeeds);
                        }
                    }, 2000);
                }
                else{
                    uploadedText.setText("Error while addding post");

                    uploadedText.setTextColor(Color.RED);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadedLayout.setVisibility(View.GONE);
                            uploadedText.setVisibility(View.GONE);

                            navController.popBackStack();
                        }
                    }, 2000);
                }
            }
        });
    }

    private void addNewFeed(final List<Posts> data){

        uploadedLayout.setVisibility(View.VISIBLE);
        waitingBar.setVisibility(View.VISIBLE);

        if (imageBitmap != null){
            fb.saveImage(imageBitmap, new MainModel.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    firebaseImageUrl = url;

                    addFinalPostTofirebase(data);
                }
            });
        }
        else if (createFeedTxt.getText().length() > 0){
            addFinalPostTofirebase(data);
        }
        else{
            uploadedLayout.setVisibility(View.VISIBLE);
            uploadedText.setVisibility(View.VISIBLE);
            waitingBar.setVisibility(View.GONE);

            uploadedText.setText("You must write some text or take a photo");

            uploadedText.setTextColor(Color.RED);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadedLayout.setVisibility(View.GONE);
                    uploadedText.setVisibility(View.GONE);
                }
            }, 2000);
        }

    }

    private  void takePhoto(){
        takePhotoGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo = new TakePhoto();

                Intent galleryIntent = photo.pickFromGallery();

                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        takePhotoCameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                photo = new TakePhoto();

                Intent cameraIntent = photo.captureFromCamera(getContext());

                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
    }

    private void submitFeed(){

        createFeedSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createFeedView = v.getRootView();

                fb.getAllPosts(new MainModel.GetAllPostsListener() {

                    @Override
                    public void onComplete(List<Posts> data) {
                        if (!isCounted){
                            addNewFeed(data);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View createFeedView = inflater.inflate(R.layout.fragment_create_feed, container, false);

        createFeedTxt = (EditText) createFeedView.findViewById(R.id.create_feed_txt);

        createFeedSubmitBtn = (Button) createFeedView.findViewById(R.id.create_feed_submit_btn);

        takePhotoGalleryBtn = (Button) createFeedView.findViewById(R.id.take_photo_gallery);

        takePhotoCameraBtn = (Button) createFeedView.findViewById(R.id.take_photo_camera);

        isUploadedView = (TextView) createFeedView.findViewById(R.id.is_Uploaded);

        waitingBar = (ProgressBar) createFeedView.findViewById(R.id.create_waiting_bar);

        uploadedText  = (TextView) createFeedView.findViewById(R.id.create_final);

        uploadedLayout = (FrameLayout) createFeedView.findViewById(R.id.create_waiting);

        submitFeed();

        takePhoto();

        return  createFeedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image

        Uri selectedImage;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    imageBitmap = BitmapFactory.decodeFile(imgDecodableString);

                    isUploadedView.setVisibility(View.VISIBLE);

                    break;
                case  CAMERA_REQUEST_CODE:
                    selectedImage = Uri.parse(photo.getFilePath());

                    try{
                        imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                        Matrix matrix = new Matrix();

                        matrix.postRotate(270);

                        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

                        isUploadedView.setVisibility(View.VISIBLE);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                    break;
            }
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
