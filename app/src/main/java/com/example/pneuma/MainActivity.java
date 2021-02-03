package com.example.pneuma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUsername;
    private ImageButton AddNewPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserID;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            currentUserID = mAuth.getCurrentUser().getUid();
        }
        UserRef = FirebaseDatabase.getInstance("https://pneuma-b1a42-default-rtdb.firebaseio.com/").getReference().child("Users");

        //add the toolbar to the main activity hamburger
        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawble_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,R.string.drawer_open, R.string.drawer_closer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header); //storing the navigation_header page in the layout in navView
        //NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);

        //displaying the username in the navigation bar
        NavProfileUsername = (TextView)navView.findViewById(R.id. nav_user_full_name);
        if (currentUserID!= null) {
            UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if(dataSnapshot.hasChild("fullname")){
                            String fullname = dataSnapshot.child("fullname").getValue().toString();
                            NavProfileUsername.setText(fullname);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Profile name does not exists...",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        //hamburger bar function
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });
    }

    private void SendUserToPostActivity() {

        Intent addNewPostIntent  =  new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }


    //method to check the user Authentication or register in the app
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser==null){
            SendUserTologinActivity();
        }
        else {
            chechUserExistance();
        }
    }

    //method to check if the user after login, if the user exist in the database, send the
    // user to login page, otherwise send the user to setting page to enter his info
    private void chechUserExistance() {
        final String Current_user_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //the user is authenticated but still not in the database Realtime
                if (!snapshot.hasChild(Current_user_id)){
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //now the user will fill his info for the first time in the database
    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity. this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserTologinActivity() {
        Intent loginIntent = new Intent(MainActivity. this, loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //sandwich
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //this fucntion will allow the user to navigate through home, profile ...after clicking on it
    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Friends List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_add_friends:
                Toast.makeText(this, "Add Friend", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_chat:
                Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                SendUserTologinActivity();
                break;

            case R.id.nav_personality:
                Toast.makeText(this, "Personality", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}