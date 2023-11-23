package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.firebase.FirebaseApp;

public class Dashboard extends AppCompatActivity {

    private TextView textView;
    private TextView userNamesTextView;
    ProgressBar progressBar;
    int counter = 0;

    //Float tempInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FirebaseApp.initializeApp(this);

        //userNamesTextView = findViewById(R.id.welcomeuser_txt);

        textView = findViewById(R.id.tempText);

        //userNamesTextView.setText("Welcome to the dashboard");

        Button temperature_button = (Button)findViewById(R.id.tempBtn);
        Button logout_button = (Button)findViewById(R.id.logoutBtn);
        Button chat_button = (Button)findViewById(R.id.chatBtn);
        Button post_button = (Button)findViewById(R.id.postBtn);



        // temperature alert



        showTempData();


        temperature_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, TemperatureInfo.class);
                startActivity(intent);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, ChatUsers.class);
                startActivity(intent);
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, PostsDashboard.class);
                startActivity(intent);
            }
        });

    }




    private void showTempData() {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "http://10.0.2.2:8000/api/latest-temperature/";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                        int id = json.getInt("id");
                        String temperature = json.getString("temperature");

                        String timestamp = json.getString("timestamp");

                        Float temp = Float.parseFloat(temperature);

                        float tempo = temp * 5;
                       // float temp = 20;

                        // Set the target value that you want to reach
                        final Float targetValue = tempo; // Adjust this value to your desired target

                            progressBar = (ProgressBar) findViewById(R.id.progressBar);
                            final Timer t = new Timer();
                            TimerTask tt = new TimerTask() {
                                @Override
                                public void run() {
                                    counter++;
                                    progressBar.setProgress(counter);
                                    if (counter >= targetValue) {
                                        t.cancel();
                                    }
                                }
                            };
                            t.schedule(tt,0,100);


                            // change color of progress

                        int red = Color.parseColor("#FF0000");


                        //String tempData = temperature.toString();

                        // Update UI elements with the extracted data
                        runOnUiThread(() -> {
                            textView.setText(temperature + "°C");

                            Float tempInt = Float.parseFloat(temperature);

                            //float tempInt = 20;

                            if ( tempInt < 7.00) {
                                AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this).create();
                                alertDialog.setTitle("Temperature is too low");
                                alertDialog.setMessage("The current container temperature is too low. Please change the temperature to a higher setting ");
                                //alertDialog.setIcon(R.drawable.welcome);

                                progressBar.setProgressTintList(ColorStateList.valueOf(red));

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                alertDialog.show();

                                textView.setTextColor(Color.RED);
                                progressBar.setProgressTintList(ColorStateList.valueOf(red));


                            } else if ( tempInt > 14.00 ){
                                AlertDialog alertDialog1 = new AlertDialog.Builder(Dashboard.this).create();
                                alertDialog1.setTitle("Temperature is too high");
                                alertDialog1.setMessage("The current container temperature is too high. Please change the temperature to a lower setting ");
                                //alertDialog.setIcon(R.drawable.welcome);


                                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                alertDialog1.show();

                                textView.setTextColor(Color.RED);
                                progressBar.setProgressTintList(ColorStateList.valueOf(red));


                            }else{
                                textView.setTextColor(Color.GREEN);
                            }
                            //tempInt = Float.parseFloat(temperature);

                            //textView.setText(tempData);
                            // Update your UI elements with the extracted data
                            // For example, update TextViews with id, temperature, and timestamp
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