package com.example.pneuma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserID;
    private CircleImageView ProfileImage;
    private TextView ProfileUsername;
    private TextView ProfileUsername2;
    private TextView username;
    private TextView addy, biog_;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            currentUserID = mAuth.getCurrentUser().getUid();
        }
        UserRef = FirebaseDatabase.getInstance("https://pneuma-b1a42-default-rtdb.firebaseio.com/").getReference().child("Users");
        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        //displaying the username in the navigation bar
        ProfileUsername = (TextView)findViewById(R.id. tv_name);
        ProfileUsername2 = (TextView)findViewById(R.id. tv_name2);
        username = (TextView)findViewById(R.id. userAt);
        addy = (TextView)findViewById(R.id. tv_address);
        biog_ = (TextView)findViewById(R.id. profile_bio);
        if (currentUserID != null) {

            //get the reference for the current user
            DocumentReference docRef = db.collection("users").document(currentUserID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
//check if user info exists
                        if (document.exists()) {
                            String name = document.get("fullname").toString();
                            String user = document.get("username").toString();
                            String loc = document.get("country").toString();
                            ProfileUsername.setText(name);
                            ProfileUsername2.setText(name);
                            username.setText(user);
                            addy.setText(loc);
                            biog_.setText(document.get("bio").toString());
//load users profile image using glide
                            Glide.with(ProfileActivity.this)
                                    .load(document.get("profile_url").toString())
                                    .into(ProfileImage);

                            //   Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            //   Log.d(TAG, "No such document");
                        }
                    } else {
                        //   Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

    }
}