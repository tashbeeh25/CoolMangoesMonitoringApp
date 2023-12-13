package com.example.coolmangoesmonitoringapp;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
public class chatwindo extends AppCompatActivity {

    public  String imageUrl;

    public String voiceUrl;

    private String voiceFilePath; // Add this variable to store the path of the recorded voice file

    private static final int REQUEST_PERMISSION_CODE = 100;



    String reciverimg, reciverUid,reciverName,SenderUID;
    CircleImageView profile;
    TextView reciverNName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public  static String senderImg;
    public  static String reciverIImg;
    CardView sendbtn, imageBtn, recordBtn, pauseBtn;


    EditText textmsg;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    String senderRoom,reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<MsgModelClass> messagesArrayList;
    MessageAdapter mmessagesAdpter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);
        getSupportActionBar().hide();

        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtnn);
        imageBtn = findViewById(R.id.imageBtn);
        recordBtn = findViewById(R.id.recordBtn);
        pauseBtn= findViewById(R.id.pauseBtn);
        textmsg = findViewById(R.id.textmsg);
        reciverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new MessageAdapter(chatwindo.this, messagesArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);


        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText("" + reciverName);

        SenderUID = firebaseAuth.getUid();

        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;




        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");



        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MsgModelClass messages = dataSnapshot.getValue(MsgModelClass.class);
                    messagesArrayList.add(messages);
                }
                mmessagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue().toString();
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recordBtn.setOnClickListener(view -> {
            // Check for permission before starting recording
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                //isMicrophonePrssent();

                startRecording();
                recordBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.VISIBLE);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaRecorder != null) {
                    // Stop recording if it's already started
                    stopRecording();
                    pauseBtn.setVisibility(View.GONE);
                    recordBtn.setVisibility(View.VISIBLE);
                } else {
                    // Start recording if it's not started
                    startRecording();
                }
            }
        });



        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1); // Use a constant for the request code
            }


        });



        //String ImageUri = "gs://cool-mangoes-fc.appspot.com";

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageWithImage(imageUrl, voiceUrl);  // Move the existing button click logic to a separate method
                imageUrl = null;
                voiceUrl = null;
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Upload the image to Firebase Storage
            uploadImageToFirebaseStorage(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isMicrophonePrssent(){
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            Toast.makeText(this, " NO MICROPHONE", Toast.LENGTH_SHORT).show();

            return true;
        }else{
            return false;
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void startRecording() {
        // Start recording
        String fileName = "audio_" + System.currentTimeMillis() + ".3gp";
        voiceFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/" + fileName;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(voiceFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stopRecording() {

        if (mediaRecorder != null) {
            // Stop recording logic
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            // Upload the recorded voice to Firebase Storage
            uploadVoiceToFirebaseStorage(voiceFilePath);

            // You can use voiceFilePath to send the voice message (if needed)
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void playRecording() {
        // Play the recorded audio
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voiceFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this, "Recording playing", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other methods...

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    // Other methods...


    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String imageName = "image_" + System.currentTimeMillis() + ".jpg"; // Unique image name
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, now get the download URL
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();

                        // Now you can use imageUrl to send the image message
                        // You can optionally display a message or enable the send button here
                        Toast.makeText(chatwindo.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(chatwindo.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }



    private void uploadVoiceToFirebaseStorage(String voiceFilePath) {
        if (voiceFilePath != null) {
            // Create a unique name for the voice file
            String fileName = "voice_" + System.currentTimeMillis() + ".3gp";

            // Get a reference to the Firebase Storage location
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("voice_messages/" + fileName);

            // Upload the voice file to Firebase Storage
            storageReference.putFile(Uri.fromFile(new File(voiceFilePath)))
                    .addOnSuccessListener(taskSnapshot -> {
                        // Voice file uploaded successfully, now get the download URL
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            voiceUrl = uri.toString();

                            // Now you can use voiceUrl to send the voice message
                            // You can optionally display a message or enable the send button here
                            Toast.makeText(chatwindo.this, "Voice uploaded successfully", Toast.LENGTH_SHORT).show();

                            // Continue with sending the message, e.g., call sendMessageWithImage
                            //sendMessageWithImage(imageUrl, voiceUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful voice file uploads
                        Toast.makeText(chatwindo.this, "Voice upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void sendMessageWithImage(String imageUrl, String voiceUrl) {
        /*String message = textmsg.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(chatwindo.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
            return;
        }
        textmsg.setText("");
        Date date = new Date();

        // Use imageUrl to send the image message
        MsgModelClass messagess = new MsgModelClass(message, SenderUID, date.getTime(), imageUrl);

        database = FirebaseDatabase.getInstance();
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .push().setValue(messagess).addOnCompleteListener(task -> {
                    database.getReference().child("chats")
                            .child(reciverRoom)
                            .child("messages")
                            .push().setValue(messagess).addOnCompleteListener(task1 -> {
                                // Handle completion
                            });
                });
    }*/




        String message = textmsg.getText().toString();

        textmsg.setText("");
        Date date = new Date();

        // Use imageUrl to send the image message
        MsgModelClass messagess = new MsgModelClass(message, SenderUID, date.getTime(), imageUrl, voiceUrl);


        database = FirebaseDatabase.getInstance();
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .push().setValue(messagess).addOnCompleteListener(task -> {
                    database.getReference().child("chats")
                            .child(reciverRoom)
                            .child("messages")
                            .push().setValue(messagess).addOnCompleteListener(task1 -> {
                                // Handle completion
                            });
                });

    }

}