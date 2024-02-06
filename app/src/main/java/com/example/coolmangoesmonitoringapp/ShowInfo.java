package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowInfo extends AppCompatActivity {

    TextView id, name, email, phone, query;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        id = findViewById(R.id.ticketnum);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        query = findViewById(R.id.query);

        Button logout_button = (Button) findViewById(R.id.logoutBtn);
        Button home_button = (Button) findViewById(R.id.dashboardBtn);
        Button setting_button = (Button) findViewById(R.id.setting);

        userEmailingDetails();

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });


        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowInfo.this, Dashboard.class);
                startActivity(intent);
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowInfo.this, Setting.class);
                startActivity(intent);
            }
        });

    }

    private void userEmailingDetails() {
        OkHttpClient client3 = new OkHttpClient();


        String apiUrl = "https://api2.charlie-iot.com/api/latest-contact/";

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
                        String idd = json.getString("id");
                        String namee = json.getString("name");
                        String emaill = json.getString("email");
                        String phonee = json.getString("phone");
                        String queryy = json.getString("query");


                        runOnUiThread(() -> {
                            id.setText("Reference number #" + idd);
                            name.setText("Your Name: " + namee);
                            email.setText("Your Email: " + emaill);
                            phone.setText("Your Phone: " + phonee);
                            query.setText("Your Query: " + queryy);

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
}