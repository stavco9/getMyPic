package com.example.getmypic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.getmypic.Models.DataManager;
import com.example.getmypic.Models.DownloadImage;
import com.example.getmypic.Models.Firebase;
import com.example.getmypic.Models.MainModel;
import com.example.getmypic.Models.PostAsyncDao;
import com.example.getmypic.Models.Posts;
import com.example.getmypic.Models.TakePhoto;
import com.example.getmypic.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navController = Navigation.findNavController(this, R.id.get_my_pic_nav_graph);

        if (Users.isAuthenticated()) {
            this.prepareViewForLoggedInUser(Users.getUser());
        } else {
            if (GetMyPicApplication.isInternetAvailable()) {
                FirebaseAuth.getInstance().signInAnonymously();
            }

            this.prepareViewForGuest();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);
        getSupportActionBar().setTitle("WatchMe!");

        DataManager.SyncAllPosts();
    }

    public void prepareViewForGuest() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.login).setVisible(true);
        navigationView.getMenu().findItem(R.id.myFeeds).setVisible(false);
        navigationView.getMenu().findItem(R.id.removeUser).setVisible(false);
        navigationView.getMenu().findItem(R.id.logout).setVisible(false);
        ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_profile_pic)).setImageResource(R.drawable.launcher_icon);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name)).setText("");

        navController.popBackStack(R.id.listFeeds, false);
    }

    public void prepareViewForLoggedInUser(FirebaseUser user) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.login).setVisible(false);
        navigationView.getMenu().findItem(R.id.myFeeds).setVisible(true);
        navigationView.getMenu().findItem(R.id.removeUser).setVisible(true);
        navigationView.getMenu().findItem(R.id.logout).setVisible(true);
        new DownloadImage((ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_profile_pic)).execute(user.getPhotoUrl().toString());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name)).setText(user.getDisplayName());

        navController.popBackStack(R.id.listFeeds, false);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            //case R.id.login:
                //Fragment loginFreg = new Login();// the fragment which you ant to display
                //FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.beginTransaction().replace(R.id.login,loginFreg).commit();

                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
