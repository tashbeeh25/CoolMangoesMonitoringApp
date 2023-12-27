package com.example.coolmangoesmonitoringapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostWindo extends AppCompatActivity {

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;

    FirebaseAuth auth;
    public String imageUrl;

    public String voiceUrl;

    private String voiceFilePath; // Add this variable to store the path of the recorded voice file

    private static final int REQUEST_PERMISSION_CODE = 100;
    //String reciverimg, reciverUid,reciverName,SenderUID;
    //TextView reciverNName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    //public  static String senderImg;
    //public  static String reciverIImg;
    CardView sendbtn, imageBtn, recordBtn, pauseBtn;

    Button back_btn;

    TextView groupName, disployTextMessage;
    EditText textmsg;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    //String senderRoom,reciverRoom;
    //RecyclerView messageAdpter;
    // ArrayList<MsgModelClass> messagesArrayList;
    //MessageAdapter mmessagesAdpter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_windo);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        //Toast.makeText(PostWindo.this, currentGroupName, Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("user");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("group").child(currentGroupName);


        getSupportActionBar().hide();
        //database = FirebaseDatabase.getInstance();
        //firebaseAuth = FirebaseAuth.getInstance();

        //reciverName = getIntent().getStringExtra("nameeee");
        //reciverimg = getIntent().getStringExtra("reciverImg");
        //reciverUid = getIntent().getStringExtra("uid");

        //messagesArrayList = new ArrayList<>();

        back_btn = findViewById(R.id.back_btn);
        groupName = findViewById(R.id.group_name);
        sendbtn = findViewById(R.id.sendbtnn);
        imageBtn = findViewById(R.id.imageBtn);
        recordBtn = findViewById(R.id.recordBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        textmsg = findViewById(R.id.textmsg);
        disployTextMessage = findViewById(R.id.msgadpter);
        //reciverNName = findViewById(R.id.recivername);
        //messageAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        groupName.setText(currentGroupName);

        GetUserInfo();
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostWindo.this, PostsDashboard.class);
                startActivity(intent);
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessageInfoToDatabase();
                textmsg.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SaveMessageInfoToDatabase() {
        String message = textmsg.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(PostWindo.this, "Please write a message", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentdateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentdateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);


        }
    }

    private void GetUserInfo() {

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = snapshot.child("firstName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot snapshot) {

        Iterator iter = snapshot.getChildren().iterator();

        while (iter.hasNext()) {
            String chatDate = (String) ((DataSnapshot) iter.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iter.next()).getValue();
            String chatName = (String) ((DataSnapshot) iter.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iter.next()).getValue();

            disployTextMessage.append(chatName + " :\n" + chatMessage + " :\n" + chatTime + "     " + chatDate + "\n\n\n");

        }

    }
}