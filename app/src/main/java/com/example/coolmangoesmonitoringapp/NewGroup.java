package com.example.coolmangoesmonitoringapp;

import androidx.annotation.NonNull;
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

public class NewGroup extends AppCompatActivity {

    String gn;
    public  static String name;

    EditText group_name;
    private static final String API_URL = "http://10.0.2.2:8000/api/create-group/";

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Button create_group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        create_group = (Button) findViewById(R.id.create_group_btn);

        FirebaseApp.initializeApp(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating group...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        group_name = findViewById(R.id.group_name);

        DatabaseReference reference = database.getReference().child("group");


        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gn = group_name.getText().toString();
                name = gn;

                reference.child(gn).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast toast = Toast.makeText(NewGroup.this, "Successfully created a group", Toast.LENGTH_SHORT);

                        }
                    }
                });

                if (TextUtils.isEmpty(gn)) {
                    Toast.makeText(NewGroup.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                } else {
                    makeRegistrationRequest(gn);
                }
            }
        });
    }

    private void showSuccessToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Successfully created a group", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void showFailedToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Could not successfully create a group", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void makeRegistrationRequest(String gn) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", gn);
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
                            Intent intent = new Intent(NewGroup.this, PostsDashboard.class);
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

                    Intent intent = new Intent(NewGroup.this, NewGroup.class);
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
            if ("Successfully created a group".equals(message)) {
                // Start the Dashboard activity

            } else {
                // Handle other cases or show a toast message
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error
        }
    }

}