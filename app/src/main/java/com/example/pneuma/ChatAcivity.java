package com.example.pneuma;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAcivity extends AppCompatActivity
{

    private androidx.appcompat.widget.Toolbar ChatToolBar;
    private ImageButton SendMessageButton, SendImageFileButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    private String messageRecieverID, messageRecieverName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitializeFields();
    }

    private void InitializeFields()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolBar);


        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendImageFileButton = (ImageButton) findViewById(R.id.send_image_file_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);

    }
}
