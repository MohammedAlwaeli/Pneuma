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
import android.widget.Toast;
//import com.google.auth.oauth2.GoogleCredentials;


import com.google.android.gms.tasks.Continuation;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText UserName, FullName, CountryName, bio_;
    private Button SaveInformationName;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private StorageTask uploadTask;


    private ImageView image;
    private Uri imageUri;

    private StorageReference PostImageReference;
    String currentUserID;
    final static int Gallery_pick = 1;
    private StorageReference UserProfileImageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        PostImageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance("https://pneuma-b1a42-default-rtdb.firebaseio.com/").getReference().child("Users").child(currentUserID); //it was the biggest error until I put the link for the database
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images"); //create folder profile images in the database

        UserName = (EditText)findViewById(R.id.setup_user_name);
        FullName = (EditText)findViewById(R.id.setup_full_name);
        CountryName = (EditText)findViewById(R.id.setup_country_name);
        bio_ = (EditText)findViewById(R.id.setup_Bio);
        SaveInformationName = (Button)findViewById(R.id.setup_information_button);
        ProfileImage = (CircleImageView)findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);

        SaveInformationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()){
                   // Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    //prevent uploading multiple images
                }
                else{
                    SaveAccountSetupInformation();
                }

            }
        });


        //make the profile button able to chose image from the gallery
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, Gallery_pick);
            }
        });
    }


    //upload the image to the firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(requestCode==Gallery_pick && resultCode==RESULT_OK && data != null){
            Uri imageUri = data.getData();
            ProfileImage.setImageURI(imageUri);

            //cropping the image still not working
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri(); //it will store the cropped image
                StorageReference filePath = UserProfileImageRef.child(currentUserID+".jpg"); //firebase reference to the image which will be located in UserProfileImageRef folder

                // it will save the cropped image in the firebase
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() { //storing the picture in the firebase
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                            final String downloadUril = task.getResult().getDownloadUrl().toString(); //getDownlaod fixed with changing firebase link in gradle(link for the image in firebase)
                            UserRef.child("profileimage").setValue(downloadUril)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully..",Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else {
                                                String message  = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Erroe Occured: "+message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }

                                        }
                                    });
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Error Occured: Image can not be corpped. Try again", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }*/

        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ProfileImage.setImageURI(imageUri);
        }
    }


    //storing the image
    private String getExtension(Uri uri)
    {
        ContentResolver cr =  getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void SaveAccountSetupInformation() {

        //storing the image
        /*
        StorageReference TempRef = UserProfileImageRef.child(System.currentTimeMillis()+"."+getExtension(imageUri));
        uploadTask = TempRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                });*/


        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();
        String biog = bio_.getText().toString();
        Uri pic = imageUri;

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "please write your username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "please write your fullname", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(country)){
            Toast.makeText(this, "please write your country", Toast.LENGTH_SHORT).show();
        }
        if(imageUri==null){
            Toast.makeText(this, "Please select a profile image...", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country" ,country);
            userMap.put("gender", "None");
            userMap.put("DOB", "None");
            userMap.put("profile_url", "None");
            userMap.put("bio", biog);

// Add a new document with a generated ID
            db.collection("users").document(currentUserID).set(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {

                            Toast.makeText(SetupActivity.this, "Your account is created Succussfully.",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            StoringImageToFirebaseStorage(pic);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String message  = e.getMessage();
                            Toast.makeText(SetupActivity.this, "Error Occured: "+message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                    });
/*
            HashMap userMap = new HashMap();

            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country" ,country);
            userMap.put("status", "Hey there, I am using Pneuma app dev by Mohammed");
            userMap.put("gender", "None");
            userMap.put("DOB", "None");
            userMap.put("relatoinshipStatus","None" );
            userMap.put("url", "None");



            UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account is created Succussfully.",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else {
                        String message  = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occured: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });*/
        }

    }
    private void StoringImageToFirebaseStorage(Uri pic) {


        String imagename = pic.getLastPathSegment() + currentUserID + ".jpg";
        final StorageReference filePath = PostImageReference.child("Profile").child(imagename);
        UploadTask uploadTask = filePath.putFile(pic);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                    //update info
                    db.collection("users").document(currentUserID).update("profile_url", downloadUri.toString());
                    SendUserToMainActivity();
                  //  usersRef.child(current_user_id).child("url").setValue(downloadUri.toString());

                   // Toast.makeText(PostActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();
                 //   SavingPostInformationToDatabase();
                } else {
                    String message = task.getException().getMessage();
                  //  Toast.makeText(PostActivity.this, "Eroor occured" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        //let's prevent the user from going back to the logout activity after the user login, in case the user clicked on back button
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}