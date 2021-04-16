package com.example.pneuma;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserID;
    private CircleImageView match_1;
    private CircleImageView match_2;
    private androidx.appcompat.widget.Toolbar ChatToolBar;
    private ImageButton SendMessageButton, SendImageFileButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    private String messageRecieverID, messageRecieverName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            currentUserID = mAuth.getCurrentUser().getUid();
        }
       match_1 = (CircleImageView) findViewById(R.id.match1);
        match_2 = (CircleImageView) findViewById(R.id.match2);
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
                            ArrayList<String> list = (ArrayList<String>) document.get("Matches");

                            DocumentReference docRef1 = db.collection("users").document(list.get(0));
                            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                       if (task.isSuccessful()) {
                                                                           DocumentSnapshot document = task.getResult();

                                                                           if (document.exists()) {
                                                                               Glide.with(ChatActivity.this)
                                                                                       .load(document.get("profile_url").toString())
                                                                                       .into(match_1);
                                                                           }
                                                                       }
                                                                   }
                                                               });
                            DocumentReference docRef2 = db.collection("users").document(list.get(1));
                            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {
                                            Glide.with(ChatActivity.this)
                                                    .load(document.get("profile_url").toString())
                                                    .into(match_2);
                                        }
                                    }
                                }
                            });

                       //     Glide.with(ProfileActivity.this)
                          //          .load(document.get("profile_url").toString())
                           //         .into(ProfileImage);

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

    private void InitializeFields()
    {
        /*
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolBar);


        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendImageFileButton = (ImageButton) findViewById(R.id.send_image_file_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);
*/
    }
}
