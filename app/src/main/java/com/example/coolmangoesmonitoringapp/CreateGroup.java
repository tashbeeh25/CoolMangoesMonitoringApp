package com.example.coolmangoesmonitoringapp;
/*
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateGroup extends AppCompatActivity {

    EditText group_name;
    //private static final String API_URL = "http://10.0.2.2:8000/api/register/";
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        FirebaseApp.initializeApp(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing The Account");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        Button register = (Button) findViewById(R.id.reg_btn);
        group_name = findViewById(R.id.group_name);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String gn = group_name.getText().toString();


                if (TextUtils.isEmpty(gn)) {
                    Toast.makeText(CreateGroup.this, "Please enter a group name", Toast.LENGTH_SHORT).show();

                }else{
                    auth.createUserWithEmailAndPassword(emaill, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();

                                DatabaseReference reference2 = database.getReference().child("group").child(id);
                                Users users = new Users(id, name);
                                Groups groups = new Groups(id, "DC-Farm", "ANNOUNCEMENT: NEW EQUIPMENT");


                                                        reference2.setValue(groups).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete( Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(Register.this,Dashboard.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(Register.this, "Error in creating the group", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete( Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(Register.this,Dashboard.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(Register.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status = "Hey I'm Using This Application";
                                    imageUri = "https://firebasestorage.googleapis.com/v0/b/cool-mangoes-fc.appspot.com/o/360_F_328113542_31B2IVU37qZ09cXXA6iMSXs62Optrwok.jpg?alt=media&token=02be7394-47e5-4f9c-abb0-81698a8acbc2";
                                    //     public Users(String user_id, String username, String first_name, String last_name,  String email, String password, String profilePic, String status) {
                                    Users users = new Users(id, usern, fn, ln, emaill, passwd, imageUri,status);
                                    Groups groups = new Groups(id, "DC-Farm", "ANNOUNCEMENT: NEW EQUIPMENT");

                                    reference2.setValue(groups).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete( Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.show();
                                                Intent intent = new Intent(Register.this,Dashboard.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(Register.this, "Error in creating the groups", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete( Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.show();
                                                Intent intent = new Intent(Register.this,Dashboard.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(Register.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

    }

    private void showSuccessToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void showFailedToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Could not successfully register", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void makeRegistrationRequest(String fn,String ln, String usern, String emaill, String passwd) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", usern);
            jsonBody.put("first_name", fn);
            jsonBody.put("last_name", ln);
            jsonBody.put("email", emaill);
            jsonBody.put("password", passwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(mediaType, jsonBody.toString());

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleSuccessfulResponse(responseBody);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessToast();

                            Intent intent = new Intent(Register.this, QRscanner.class);
                            startActivity(intent);
                        }
                    });


                } else {
                    // Handle error
                    //Log.e("Registration", "HTTP Error: " + response.code());
                    // Call onFailure with a custom IOException to trigger the onFailure callback
                    onFailure(call, new IOException("HTTP Error: " + response.code()));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showFailedToast();

                        }

                    });

                    Intent intent = new Intent(Register.this, Register.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //Log.e("Registration", "Network Failure: " + e.getMessage());
            }
        });
    }

    private void handleSuccessfulResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            // Parse and handle the response JSON as needed
            String message = jsonResponse.optString("message");

            // Assuming the response JSON contains a "message" key with a success message
            if ("Registration successful".equals(message)) {
                // Start the Dashboard activity

            } else {
                // Handle other cases or show a toast message
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }
    }
}*/