package com.example.coolmangoesmonitoringapp;
import static android.content.ContentValues.TAG;
import static com.example.coolmangoesmonitoringapp.Login.email;
import static com.example.coolmangoesmonitoringapp.Login.userId;
import static com.example.coolmangoesmonitoringapp.Profile.new_username;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Containers extends AppCompatActivity {

    public static String selectedQrCode;
    private static ListView listView;

    public Button addContainer;
    TextView noContainer, showContainer;
    public static ArrayList<String> qrCodesList;

    public static String usern;

    android.app.ProgressDialog progressDialog2;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containers);

        currentUser();

        usern = new_username;

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button logout_button = (Button) findViewById(R.id.logoutBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button chat_btn = (Button) findViewById(R.id.chatBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button setting_button = (Button) findViewById(R.id.setting);

        //@SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button
        addContainer = (Button) findViewById(R.id.addContainer);
        qrCodesList = new ArrayList<>();
        listView = findViewById(R.id.listContainers);
        noContainer = findViewById(R.id.noContainer);
        showContainer = findViewById(R.id.showContainer);

        getQrCodes();

        listView.setOnItemClickListener((parent, view, position, id) -> {
                // Retrieve the selected QR code
            selectedQrCode = qrCodesList.get(position);

                // Perform your action with the selected QR code
                // For example, display a toast and navigate to the Dashboard activity
            Toast.makeText(Containers.this, "Selected QR Code: " + selectedQrCode, Toast.LENGTH_SHORT).show();

                // Create an intent to start the Dashboard activity
            Intent intent = new Intent(Containers.this, Dashboard.class);

                // Pass the selected QR code to the Dashboard activity if needed
            intent.putExtra("selectedQrCode", selectedQrCode);

            // Start the Dashboard activity
            startActivity(intent);

        });

        if (qrCodesList.isEmpty()){
            showContainer.setVisibility(View.GONE);
            noContainer.setVisibility(View.VISIBLE);
            addContainer.setVisibility(View.VISIBLE);
            addContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Containers.this, QRscanner.class);
                    startActivity(intent);
                }
            });
        }


        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Containers.this, MainActivity.class);
                startActivity(intent);
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Containers.this, ChatsDashboard.class);
                startActivity(intent);
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Containers.this, Setting.class);
                startActivity(intent);
            }
        });


    }

    private void getQrCodes() {

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Please Wait...");
        progressDialog2.setCancelable(false);
        getSupportActionBar().hide();
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "https://api2.charlie-iot.com/api/get-qr-code-by-user-id/" + userId;

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
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Iterate through each QR code in the array
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String qrCode = jsonObject.getString("qr_code");

                            // Add the QR code to the list
                            qrCodesList.add(qrCode);
                            Log.d(TAG, "qrCodesList sample: "+ qrCodesList);
                        }

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            // Create ArrayAdapter and set it to the ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Containers.this, android.R.layout.simple_list_item_1, qrCodesList);

                            listView.setAdapter(adapter);

                            showContainer.setVisibility(View.VISIBLE);
                            noContainer.setVisibility(View.GONE);
                            addContainer.setVisibility(View.GONE);
                            Log.d(TAG, "qrCodesList 1: "+ qrCodesList);
                            
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

    private void currentUser() {
        OkHttpClient client2 = new OkHttpClient();

        String apiUrl = "https://api2.charlie-iot.com/api/get-user-by-email/" + email;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client2.newCall(request).enqueue(new Callback() {
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
                        int id1 = json.getInt("id");
                        String name = json.getString("username");
                        String emaill = json.getString("email");

                        //userID = String.valueOf(id1);
                        usern = name;
                        //verifemail = emaill;


                        runOnUiThread(() -> {


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
