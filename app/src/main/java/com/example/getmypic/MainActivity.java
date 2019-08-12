package com.example.getmypic;

import android.os.Bundle;
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

import com.example.getmypic.Models.DownloadImage;
import com.example.getmypic.Models.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (Users.isAuthenticated()) {
            this.prepareViewForLoggedInUser(Users.getUser());
        } else {
            this.prepareViewForGuest();
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        /*if (Users.isAuthenticated()) {

            ImageView userImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_profile_pic);

            new DownloadImage(userImage).execute(Users.getUser().getPhotoUrl().toString());

            TextView userText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name);

            userText.setText("Hello " + Users.getUser().getDisplayName());

            navigationView.getMenu().findItem(R.id.myFeeds).setVisible(true);
            navigationView.getMenu().findItem(R.id.login).setVisible(false);
        } else {
            ImageView userImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_profile_pic);

            userImage.setImageBitmap(null);

            TextView userText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.display_name);

            userText.setText("");

            navigationView.getMenu().findItem(R.id.myFeeds).setVisible(false);
            navigationView.getMenu().findItem(R.id.login).setVisible(true);
        }*/

        NavController navController = Navigation.findNavController(this, R.id.get_my_pic_nav_graph);

        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                getSupportActionBar().setTitle(destination.getLabel());
            }
        });
    }

    public void prepareViewForGuest() {
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void prepareViewForLoggedInUser(FirebaseUser user) {
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        new DownloadImage((ImageView)navigationView.getHeaderView(0).findViewById(R.id.user_profile_pic)).execute(user.getPhotoUrl().toString());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.display_name)).setText(user.getDisplayName());

        NavController navController = Navigation.findNavController(this, R.id.get_my_pic_nav_graph);
        navController.navigate(R.id.action_startScreen_to_listFeeds);
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
