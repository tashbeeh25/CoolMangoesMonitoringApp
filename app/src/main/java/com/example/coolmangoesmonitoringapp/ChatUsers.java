package com.example.coolmangoesmonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatUsers extends AppCompatActivity {


    /*RecyclerView mainUserRecycleView;
    UserAdapter adapter;
    ArrayList<Users> usersArrayList;
    TextView users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        users = findViewById(R.id.online);
        mainUserRecycleView = findViewById(R.id.mainUserRecyclerView);

        // Initialize the usersArrayList
        usersArrayList = new ArrayList<>();

        // Initialize the adapter and set it to the RecyclerView
        adapter = new UserAdapter(this, usersArrayList);
        mainUserRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mainUserRecycleView.setAdapter(adapter);

        showUserNames();
    }


    private void showUserNames() {
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "http://10.0.2.2:8000/api/user/";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ChatUsers.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray jsonArray = jsonObject.getJSONArray("users");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            String username = json.getString("username");
                            String first_name = json.getString("first_name");
                            String last_name = json.getString("last_name");
                            String email = json.getString("email");

                            // (id, usern, fn, ln, emaill, passwd, imageUri,status);
                            Users user = new Users(id, username, first_name, last_name, email, password, imageUri, status);
                            usersArrayList.add(user);
                        }

                            /*
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Loop through the JSON array to extract user data
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            String username = json.getString("username");
                            String first_name = json.getString("first_name");
                            String last_name = json.getString("last_name");
                            String email = json.getString("email");

                            // Create a User object and add it to the ArrayList
                            Users user = new Users(username, first_name, last_name, email);
                            usersArrayList.add(user);
                        }*/
    /*

                        // Update UI elements with the extracted data
                        runOnUiThread(() -> {
                            // Assuming you want to display the first user's username
                            if (!usersArrayList.isEmpty()) {
                                users.setText(usersArrayList.get(0).getUserName());
                                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChatUsers.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }*/

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter  adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imglogout;
    ImageView cumbut,setbut;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        database=FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        DatabaseReference reference = database.getReference().child("user");

        usersArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(ChatUsers.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //imglogout = findViewById(R.id.logoutimg);

        /*
        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this,R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button no,yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this,login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });*/

        /*
        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, setting.class);
                startActivity(intent);
            }
        });

        cumbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,10);
            }
        });

        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this,login.class);
            startActivity(intent);
        }*/

    }
}

