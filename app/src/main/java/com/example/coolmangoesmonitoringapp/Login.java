package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.coolmangoesmonitoringapp.Profile.new_email;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    public static String usernam;

    public static String userId;


    public static String email;


    //String usern;

    EditText emailEditText, passwordEditText;

    TextView regTxt;
    private Button loginButton;
    private OkHttpClient client;
    FirebaseAuth auth;
    String userPattern = "^(?=.*[A-Z])(?=.*\\d).{6,}$\n";
    android.app.ProgressDialog progressDialog1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FirebaseApp.initializeApp(this);

        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setMessage("Please Wait...");
        progressDialog1.setCancelable(false);
        getSupportActionBar().hide();



        // Initialize OkHttpClient
        client = new OkHttpClient();

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbtn);
        regTxt = findViewById(R.id.reg_text);
        //usern = "mollym12";


        regTxt.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email= emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if ((TextUtils.isEmpty(email))){
                    progressDialog1.dismiss();
                    Toast.makeText(Login.this, "Enter The Email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    progressDialog1.dismiss();
                    Toast.makeText(Login.this, "Enter The Password", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    progressDialog1.dismiss();
                    passwordEditText.setError("More Then Six Characters");
                    Toast.makeText(Login.this, "Password Needs To Be Longer Then Six Characters", Toast.LENGTH_SHORT).show();
                }else {

                    progressDialog1.show();

                    currentUsername();

                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            runOnUiThread(() -> progressDialog1.dismiss());

                            if (task.isSuccessful()){
                                performLogin(usernam,password);


                                try {
                                    Intent intent = new Intent(Login.this , Containers.class);
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

    private void currentUsername() {
        OkHttpClient client3 = new OkHttpClient();

        //email = new_email;


        String apiUrl = "https://api2.charlie-iot.com/api/get-user-by-email/" + email;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client3.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String name1 = json.getString("username");
                        String user_id = String.valueOf(json.getInt("id"));

                        runOnUiThread(() -> {
                            usernam = name1;
                            userId = user_id;
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
                }
            }
        });
    }

    public void performLogin(String username, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username != null ? username : "")
                .add("password", password != null ? password : "")
                .build();


        Request request = new Request.Builder()
                .url("https://api2.charlie-iot.com/api/login/")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        runOnUiThread(() -> {
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
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