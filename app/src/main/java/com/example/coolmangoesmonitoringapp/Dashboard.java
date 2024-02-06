package com.example.coolmangoesmonitoringapp;
import static android.content.ContentValues.TAG;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static com.example.coolmangoesmonitoringapp.Profile.new_username;

import static com.example.coolmangoesmonitoringapp.Login.email;
import static com.example.coolmangoesmonitoringapp.Containers.selectedQrCode;
import static com.example.coolmangoesmonitoringapp.Containers.usern;
import static com.example.coolmangoesmonitoringapp.Containers.qrCodesList;
import static com.example.coolmangoesmonitoringapp.Login.usernam;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.firebase.FirebaseApp;

public class Dashboard extends AppCompatActivity {

    private TextView textView;

    ProgressBar progressBar;
    int counter = 0;


    TextView usersTxt, username, connectionTxt, humText;

    android.app.ProgressDialog progressDialog3;

    public static String selectedValue;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        FirebaseApp.initializeApp(this);

        progressDialog3 = new ProgressDialog(this);
        progressDialog3.setMessage("Please Wait...");
        progressDialog3.setCancelable(false);
        getSupportActionBar().hide();


        textView = findViewById(R.id.tempText);
        usersTxt = findViewById(R.id.users);
        username = findViewById(R.id.usern);
        connectionTxt = findViewById(R.id.connection);
        humText = findViewById(R.id.HumText);

        Log.d(TAG, "usernam  " + usernam);
        Log.d(TAG, "usern " + usern);
        Log.d(TAG, "email  " + email);
        Log.d(TAG, "qr code list  " + qrCodesList);


        Spinner spinner = findViewById(R.id.spinner);
        spinner.setPrompt("Change the container");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, qrCodesList);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item
                selectedValue = qrCodesList.get(position);
                // Do something with the selected value
                showTempDataAgain();
                selectedQrCode = selectedValue;
                Toast.makeText(getApplicationContext(), "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });


        username.setText(usern);

        Button temperature_button = (Button) findViewById(R.id.tempBtn);
        Button humidity_button = (Button) findViewById(R.id.humBtn);
        Button logout_button = (Button) findViewById(R.id.logoutBtn);
        Button chat_button = (Button) findViewById(R.id.chatBtn);
        Button setting_button = (Button) findViewById(R.id.setting);
        Button viewContBtn= (Button) findViewById(R.id.viewContBtn);
        Button addContainer = (Button) findViewById(R.id.addContainer);


        if (usern != null && usern.equals(usernam)){
            showTempData();
            showHumData();

        }else{
            connectionTxt.setText("You are NOT connected to a container");
            connectionTxt.setTextColor(Color.RED);
            Toast.makeText(Dashboard.this, "Unable to retrieve temperature", Toast.LENGTH_SHORT).show();
        }

        temperature_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, TemperatureInfo.class);
                intent.putExtra("selectedValue", selectedValue);
                startActivity(intent);
            }
        });

        humidity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, HumidityInfo.class);
                intent.putExtra("selectedValue", selectedValue);
                startActivity(intent);
            }
        });

        viewContBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, Containers.class);
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
                Intent intent = new Intent(Dashboard.this, ChatsDashboard.class);
                startActivity(intent);
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, Setting.class);
                startActivity(intent);
            }
        });

        addContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, QRscanner.class);
                startActivity(intent);
            }
        });

    }

    private void showTempData() {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "https://api2.charlie-iot.com/api/latest-temperature/" + selectedQrCode;

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

                        String temperature = json.getString("temperature");

                        Float temp = Float.parseFloat(temperature);


                        connectionTxt.setText("You are connected to a container");
                        connectionTxt.setTextColor(GREEN);

                        float tempo = temp * 5;


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
                        t.schedule(tt, 0, 100);


                        // change color of progress

                        int red = Color.parseColor("#FF0000");


                        //String tempData = temperature.toString();

                        // Update UI elements with the extracted data
                        runOnUiThread(() -> {

                            progressDialog3.show();

                            textView.setText(temperature + "°C");

                            Float tempInt = Float.parseFloat(temperature);

                            //float tempInt = 20;

                            if (tempInt < 7.00) {
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


                            } else if (tempInt > 14.00) {
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


                            } else {
                                textView.setTextColor(GREEN);

                            }

                        });

                        progressDialog3.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
                }
            }
        });
    }


    private void showTempDataAgain() {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "http://api2.charlie-iot.com/api/latest-temperature/" + selectedValue;

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

                        String temperature = json.getString("temperature");

                        Float temp = Float.parseFloat(temperature);


                        connectionTxt.setText("You are connected to a container");
                        connectionTxt.setTextColor(GREEN);

                        float tempo = temp * 5;


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
                        t.schedule(tt, 0, 100);


                        // change color of progress

                        int red = Color.parseColor("#FF0000");


                        //String tempData = temperature.toString();

                        // Update UI elements with the extracted data
                        runOnUiThread(() -> {

                            progressDialog3.show();

                            textView.setText(temperature + "°C");

                            Float tempInt = Float.parseFloat(temperature);

                            //float tempInt = 20;

                            if (tempInt < 7.00) {
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


                            } else if (tempInt > 14.00) {
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
                            }

                            else if (tempInt.isNaN() == true) {
                                AlertDialog alertDialog1 = new AlertDialog.Builder(Dashboard.this).create();
                                alertDialog1.setTitle("No Data is available at the moment");
                                alertDialog1.setMessage("There are no current data available for this container");
                                //alertDialog.setIcon(R.drawable.welcome);


                                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                alertDialog1.show();

                                textView.setTextColor(Color.GRAY);
                                progressBar.setProgressTintList(ColorStateList.valueOf(GRAY));

                                textView.setText("");


                            } else {
                                textView.setTextColor(GREEN);
                                progressBar.setProgressTintList(ColorStateList.valueOf(GREEN));


                            }

                        });

                        progressDialog3.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
                }
            }
        });

    }

    private void showHumData() {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "http://api2.charlie-iot.com/api/latest-humidity/" + selectedQrCode;

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

                        String humidity = json.getString("humidity");




                        // Update UI elements with the extracted data
                        runOnUiThread(() -> {
                            humText.setText(humidity + "%");

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


    private void batteryData() {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "http://api2.charlie-iot.com/api/latest-battery/" + selectedValue;

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

                        String battery_voltage = json.getString("battery_voltage");


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