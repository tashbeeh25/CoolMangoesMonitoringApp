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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    ImageView playBtn;
    ImageView stopBtn;

    private boolean isPlaying = false;

    private static final int REQUEST_PERMISSION_CODE = 100;
    //String reciverimg, reciverUid,reciverName,SenderUID;
    //TextView reciverNName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    //public  static String senderImg;
    //public  static String reciverIImg;
    CardView sendbtn, imageBtn, recordBtn, pauseBtn;

    Button back_btn;

    private ImageView postedImage;  // Add this variable to reference the ImageView
    private CardView postedVN;  // Add this variable to reference the ImageView


    TextView groupName, disployTextMessage;
    EditText textmsg;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    //String senderRoom,reciverRoom;
    //RecyclerView messageAdpter;
    // ArrayList<MsgModelClass> messagesArrayList;
    //MessageAdapter mmessagesAdpter;

    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference imageStorageRef;
    private Uri imageUri;

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

        playBtn = findViewById(R.id.recordBtnn);
        stopBtn = findViewById(R.id.stopBtn);
        back_btn = findViewById(R.id.back_btn);
        groupName = findViewById(R.id.group_name);
        sendbtn = findViewById(R.id.sendbtnn);
        imageBtn = findViewById(R.id.imageBtn);
        recordBtn = findViewById(R.id.recordBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        textmsg = findViewById(R.id.textmsg);
        postedImage = findViewById(R.id.postedImage);
        postedVN = findViewById(R.id.voiceRecordCV);
        disployTextMessage = findViewById(R.id.msgadpter);
        //reciverNName = findViewById(R.id.recivername);
        //messageAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        groupName.setText(currentGroupName);
        imageStorageRef = FirebaseStorage.getInstance().getReference().child("group_images");


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

        // Inside your Send button click listener, handle image sending
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement voice recording logic
                startRecording();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement voice recording pausing logic
                pauseRecording();
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

        // Initialize GroupMessageKeyRef here
        GroupMessageKeyRef = GroupNameRef.child(messageKey);

        if (imageUri != null) {
            // Handle image sending logic
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentdateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentdateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);

            // Handle image sending logic
            sendImageMessage();

        } else if (!TextUtils.isEmpty(voiceFilePath)) {
            // Handle voice note sending logic
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentdateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentdateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);

            // Handle voice note sending logic
            sendVoiceNoteMessage();

        } else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentdateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentdateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

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



    // Add methods for handling image and voice note messages
    private void sendImageMessage() {
        // Upload image to Firebase Storage and get the download URL
        StorageReference filePath = imageStorageRef.child(currentGroupName).child(imageUri.getLastPathSegment());
        filePath.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    // Show the posted image
                    postedImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(downloadUrl).into(postedImage);
                    // Save image message to the database
                    saveMessageToDatabase(downloadUrl);
                    // Hide the text messages TextView
                    //disployTextMessage.setVisibility(View.GONE);
                });
            }
        });
    }

    private void sendVoiceNoteMessage() {
        // Upload voice note to Firebase Storage and get the download URL
        StorageReference voiceFilePathRef = FirebaseStorage.getInstance().getReference().child("group_voice_notes").child(currentGroupName).child(voiceFilePath);
        voiceFilePathRef.putFile(Uri.fromFile(new File(voiceFilePath))).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                voiceFilePathRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (uri != null) {
                        String voiceDownloadUrl = uri.toString();
                        saveMessageToDatabase(voiceDownloadUrl);
                        Toast.makeText(this, "Saved to VN to database", Toast.LENGTH_SHORT).show();
                        postedVN.setVisibility(View.VISIBLE);

                        playBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // play the recording

                                try {
                                    if (mediaPlayer == null) {
                                        mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setDataSource(String.valueOf(uri));
                                        mediaPlayer.prepare();
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                isPlaying = false;
                                                playBtn.setVisibility(View.VISIBLE);
                                                stopBtn.setVisibility(View.GONE);                                            }
                                        });
                                    }

                                    mediaPlayer.start();
                                    isPlaying = true;
                                    // Update UI if needed (e.g., change play/pause button icon)

                                    playBtn.setVisibility(View.GONE);
                                    stopBtn.setVisibility(View.VISIBLE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        Log.e("PostWindo", "Voice download URL is null");
                    }
                });
            }
        });
    }

    private void saveMessageToDatabase(String fileDownloadUrl) {
        // Save image or voice note message to the database
        // You may need to modify the database structure to store different types of messages
        // For example, add a field indicating the type of message (text, image, voice note)
        // and another field for the file download URL.

        if (GroupMessageKeyRef != null) {
            // Your existing message saving logic...
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", fileDownloadUrl); // Save the download URL
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);
        }else{
            Log.e("PostWindo", "groupKeyisNULL");
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult to handle the selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Optionally, you can display the selected image in an ImageView
            // imageView.setImageURI(imageUri);
        }
    }

    private void startRecording() {
        // Implement logic to start recording audio
        // Ensure you have the necessary permissions and handle exceptions

        // For example:
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        voiceFilePath = getExternalCacheDir().getAbsolutePath() + "/voice_note.3gp";
        mediaRecorder.setOutputFile(voiceFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording ...", Toast.LENGTH_SHORT).show();
            pauseBtn.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseRecording() {
        // Implement logic to pause recording audio
        // For example:
        mediaRecorder.stop();
        Toast.makeText(this, "Recording has stopped...", Toast.LENGTH_SHORT).show();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        pauseBtn.setVisibility(View.GONE);
        recordBtn.setVisibility(View.VISIBLE);
    }
}