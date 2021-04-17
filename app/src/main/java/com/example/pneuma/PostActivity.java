package com.example.pneuma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;

    private static final int Gallery_pick =1;
    private Uri ImageUri;
    private String Description;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference PostImageReference;
    private DocumentReference usersRef, PostRef;
    private FirebaseAuth mAuth;
    private String url;

    private String saveCurrentDate, saveCurrentTime, postRendomName, downloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostImageReference = FirebaseStorage.getInstance().getReference();
        usersRef = db.collection("users").document(current_user_id);


        SelectPostImage = (ImageButton)findViewById(R.id.select_post_image);
        UpdatePostButton = (Button)findViewById(R.id.update_post_button);
        PostDescription = (EditText)findViewById(R.id.post_description);
        loadingBar = new ProgressDialog(this);
      //  final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(
        //        "https://firebasestorage.googleapis.com/v0/b/pneuma-b1a42.appspot.com/o/Post%20Images%2F918-March-202118%3A57.jpg?alt=media&token=cb35a211-93e7-473c-8453-cd7a002abff2");
        //storageRef.getDownloadUrl()
        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallary();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });
    }

    private void ValidatePostInfo() {

        Description = PostDescription.getText().toString();

        if(ImageUri==null){
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Add New post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImageToFirebaseStorage();
        }

    }

    private void StoringImageToFirebaseStorage() {
        //getting the date for the post
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        //getting the time for post
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRendomName = saveCurrentDate+saveCurrentTime;
        String imagename = ImageUri.getLastPathSegment()+postRendomName+".jpg";
        final StorageReference filePath = PostImageReference.child("Post Images").child(imagename);
        UploadTask uploadTask = filePath.putFile(ImageUri);

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
                    url = downloadUri.toString();

                    Toast.makeText(PostActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();
                    SavingPostInformationToDatabase();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Eroor occured"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
      /*  filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    downloadUrl = task.getResult().toString();
                    Toast.makeText(PostActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();
                    SavingPostInformationToDatabase();

                   String cool = filePath.getDownloadUrl().toString();

                   int john = 2+2;
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Eroor occured"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void SavingPostInformationToDatabase() {


        HashMap postsMap = new HashMap();
        postsMap.put("description", Description);
        postsMap.put("postimage", url);

        db.collection("users").document(current_user_id).update(postsMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {


                        loadingBar.dismiss();
                        SendUserToMainActivity();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.getMessage();
                        Toast.makeText(PostActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                });


    }




    private void OpenGallary() {
        Intent gallaryIntent = new Intent();
        gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallaryIntent.setType("image/*");
        startActivityForResult(gallaryIntent, Gallery_pick);
    }

    private void SendUserToMainActivity() {


        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null ){
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }
}