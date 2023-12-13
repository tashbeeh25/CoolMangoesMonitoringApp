package com.example.coolmangoesmonitoringapp;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    public static String email;

    EditText emailEditText, passwordEditText;

    private Button loginButton;
    private OkHttpClient client;
    FirebaseAuth auth;
    String userPattern = "^(?=.*[A-Z])(?=.*\\d).{6,}$\n";
    android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FirebaseApp.initializeApp(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        getSupportActionBar().hide();


        // Initialize OkHttpClient
        client = new OkHttpClient();

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbtn);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();



                if ((TextUtils.isEmpty(email))){
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Enter The Email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Enter The Password", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    progressDialog.dismiss();
                    passwordEditText.setError("More Then Six Characters");
                    Toast.makeText(Login.this, "Password Needs To Be Longer Then Six Characters", Toast.LENGTH_SHORT).show();
                //}//else if (!username.matches(userPattern)) {
                 //   progressDialog.dismiss();
                 //   usernameEditText.setError("Give Proper username");
                }else {

                    progressDialog.show();


                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                           // progressDialog.dismiss(); // Dismiss the progressDialog here

                            if (task.isSuccessful()){
                                //performLogin(email, password);

                                try {
                                    Intent intent = new Intent(Login.this , Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }catch (Exception e){
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



            }
        });

    }

    private void showSuccessToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    private void showFailedToast() {
        // Create a Toast instance
        Toast toast = Toast.makeText(this, "Could not successfully log in", Toast.LENGTH_SHORT);

        // You can customize the position of the toast
        toast.setGravity(Gravity.CENTER, 0, 0);

        // Show the toast
        toast.show();
    }

    public void performLogin(String email, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8000/api/login/")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful login
                    String responseBody = response.body().string();


                    Log.d(responseBody, "body");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessToast();

                            Intent intent = new Intent(Login.this, Dashboard.class);
                            startActivity(intent);
                        }
                    });
                    // Parse the response if needed
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showFailedToast();
                        }
                    });

                    Intent intent = new Intent(Login.this, Login.class);
                    startActivity(intent);
                    // Handle login failure
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle network failure
            }
        });

    }

}