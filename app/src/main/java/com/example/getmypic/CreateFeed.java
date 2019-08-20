package com.example.getmypic;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.PostAsyncDao;
import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.TakePhoto;
import com.example.getmypic.Models.Users;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private TakePhoto photo;
    private Bitmap imageBitmap;

    private EditText createFeedTxt;
    private ImageButton takePhotoGalleryBtn;
    private ImageButton takePhotoCameraBtn;
    private Button removePhoto;
    private ImageView imgpreview;
    private View imgpreviewCont;
    private Button createFeedSubmitBtn;
    private ProgressBar waitingBar;
    private View waitingCont;
    private TextView uploadedText;
    private View uploadedCont;



    private NavController navController;
    private String firebaseImageUrl = "";
    private final int CAMERA_REQUEST_CODE = 1888;
    private final int GALLERY_REQUEST_CODE = 1889;
    private Firebase fb = new Firebase();
    private Posts postToEdit;
    private int count;

    private OnFragmentInteractionListener mListener;

    public CreateFeed() {
        // Required empty public constructor
    }

    // Add final post to firebase after photo is taken !!!
    private void addFinalPostTofirebase(){

        if (postToEdit != null){
            count = Integer.parseInt(postToEdit.getId());
        }
        else {
            // Get the count of posts from Firebase
            count  = Firebase.getNextId();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        // Create new post object
        final Posts post = new Posts(Integer.toString(count), createFeedTxt.getText().toString(), firebaseImageUrl, Users.getUser().getEmail(), dateFormat.format(date));

        // Add post to Firebase
        fb.addPost(post, new MainModel.AddPostListener() {

            @Override
            public void onComplete(boolean success) {

                // If the post uploaded successfully to Firebase
                if (success){
                    // Add post to local DB SQLite cache
                    PostAsyncDao.setPosts(post, new MainModel.AddPostListener() {
                        @Override
                        public void onComplete(boolean success) {
                            Log.d("SQLite", "Added successfully post to local cache");

                            // Added result text
                            createFeedSubmitBtn.setVisibility(View.GONE);
                            waitingCont.setVisibility(View.GONE);
                            uploadedCont.setVisibility(View.VISIBLE);
                            navController = Navigation.findNavController(getActivity(), R.id.get_my_pic_nav_graph);

                            // If saved in SQLite successfully
                            //if (success){

                                // Set the text that it's successful for 2 seconds
                                uploadedText.setText("Uploaded successfully");
                                uploadedText.setTextColor(Color.GREEN);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Navigate back to list feeds
                                        if (navController.getCurrentDestination().getId() == R.id.createFeed) {
                                            navController.popBackStack();
                                        }
                                    }
                                }, 2000);
                            //}
                        }
                    });
                }
                else{
                    // Set text to error for 2 seconds
                    createFeedSubmitBtn.setVisibility(View.GONE);
                    waitingCont.setVisibility(View.GONE);
                    uploadedCont.setVisibility(View.VISIBLE);
                    uploadedText.setText("Error while creating post");
                    uploadedText.setTextColor(Color.RED);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (navController.getCurrentDestination().getId() == R.id.createFeed) {
                                // Pop back to previous fragment
                                navController.popBackStack();
                            }
                        }
                    }, 2000);
                }
            }
        });

    }

    private void addNewFeed(){

        // Adding waiting spinner
        createFeedSubmitBtn.setVisibility(View.GONE);
        waitingCont.setVisibility(View.VISIBLE);
        uploadedCont.setVisibility(View.GONE);

        // If a photo was taken
        if (imageBitmap != null){

            // Upload image to Firebase
            fb.saveImage(imageBitmap, new MainModel.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    firebaseImageUrl = url;

                    // Add final post to Firebase
                    addFinalPostTofirebase();

                    // Save the photo in local cache
                    TakePhoto photo = new TakePhoto();
                    String localFileName = photo.getLocalImageFileName(firebaseImageUrl);
                    photo.saveImageToFile(imageBitmap, localFileName);
                }
            });
        }
        // If no photo was taken but there's a text
        else if (createFeedTxt.getText().length() > 0 || firebaseImageUrl.length() > 0){

            // Add final post to Firabase
            addFinalPostTofirebase();
        }
        else{
            // Set error text for 2 seconds
            createFeedSubmitBtn.setVisibility(View.GONE);
            waitingCont.setVisibility(View.GONE);
            uploadedCont.setVisibility(View.VISIBLE);
            uploadedText.setText("Failed - empty post");
            uploadedText.setTextColor(Color.RED);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createFeedSubmitBtn.setVisibility(View.VISIBLE);
                    waitingCont.setVisibility(View.GONE);
                    uploadedCont.setVisibility(View.GONE);
                }
            }, 2000);
        }

    }

    private  void takePhoto(){

        // When clicking pick gallery button
        takePhotoGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pick photo from gallery
                photo = new TakePhoto();
                Intent galleryIntent = photo.pickFromGallery();
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        // When clicking pick camera button
        takePhotoCameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Take camera photo
                photo = new TakePhoto();

                // If the application has access permissions to camera
                if (TakePhoto.hasPemissions(getActivity())){
                    Intent cameraIntent = photo.captureFromCamera(getActivity());
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
                else{
                    TakePhoto.grantWriteStoragePermissions(getActivity());
                }
            }
        });

        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBitmap = null;
                firebaseImageUrl = "";
            }
        });
    }

    private void submitFeed(){

        // When cliking submit button
        createFeedSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If there's internet connection
                if (GetMyPicApplication.isInternetAvailable()){
                    addNewFeed();
                }
                else{
                    // Set error message of internet connectivity for 2 seconds
                    createFeedSubmitBtn.setVisibility(View.GONE);
                    waitingCont.setVisibility(View.GONE);
                    uploadedCont.setVisibility(View.VISIBLE);
                    uploadedText.setText("No internet connection");
                    uploadedText.setTextColor(Color.RED);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createFeedSubmitBtn.setVisibility(View.VISIBLE);
                            waitingCont.setVisibility(View.GONE);
                            uploadedCont.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
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

        // Initalize all views
        createFeedTxt = createFeedView.findViewById(R.id.create_feed_txt);
        takePhotoGalleryBtn = createFeedView.findViewById(R.id.createfeed_gallery);
        takePhotoCameraBtn = createFeedView.findViewById(R.id.createfeed_camera);
        removePhoto = createFeedView.findViewById(R.id.create_remove_image);
        imgpreview = createFeedView.findViewById(R.id.create_image_preview_img);
        imgpreviewCont = createFeedView.findViewById(R.id.create_image_preview);
        createFeedSubmitBtn = createFeedView.findViewById(R.id.create_feed_submit_btn);
        waitingBar = createFeedView.findViewById(R.id.create_waiting_bar);
        waitingCont = createFeedView.findViewById(R.id.create_waiting_container);
        uploadedText  = createFeedView.findViewById(R.id.create_final);
        uploadedCont = createFeedView.findViewById(R.id.create_final_container);

        postToEdit = CreateFeedArgs.fromBundle(getArguments()).getPost();

        if (postToEdit != null){
            createFeedTxt.setText(postToEdit.getText());
            firebaseImageUrl = postToEdit.getPostImageUrl();

            if (firebaseImageUrl.length() > 0 && imageBitmap != null){
                imgpreviewCont.setVisibility(View.VISIBLE);
                imgpreview.setImageBitmap(imageBitmap);
            }
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("WatchMe! - Edit post");
        } else {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("WatchMe! - New post");
        }

        // Initalize submit Button
        submitFeed();

        // Initalize take photos button
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

        Uri selectedImage;

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                // The photo was taken from gallery
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

                    // Convert string to bitmap
                    imageBitmap = BitmapFactory.decodeFile(imgDecodableString);

                    if (imageBitmap != null){
                        imageBitmap = TakePhoto.compressPhoto(imageBitmap, 5);
                        imgpreviewCont.setVisibility(View.VISIBLE);
                        imgpreview.setImageBitmap(imageBitmap);
                    }
                    else{
                        imgpreviewCont.setVisibility(View.GONE);
                    }

                    break;
                case  CAMERA_REQUEST_CODE:

                    // get the image URI
                    selectedImage = Uri.parse(photo.getFilePath());

                    try{
                        // convert the uri to bitmap
                        imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                        // rotate with 270 degrees
                        Matrix matrix = new Matrix();
                        matrix.postRotate(270);
                        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

                        if (imageBitmap != null){
                            imageBitmap = TakePhoto.compressPhoto(imageBitmap, 5);
                            imgpreviewCont.setVisibility(View.VISIBLE);
                            imgpreview.setImageBitmap(imageBitmap);
                        }
                        else{
                            imgpreviewCont.setVisibility(View.GONE);
                        }
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
