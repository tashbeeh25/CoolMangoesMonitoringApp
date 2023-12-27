package com.example.coolmangoesmonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PostsDashboard extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofgroups = new ArrayList<>();
    private DatabaseReference groupRef;

    Button create_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_dashboard);

        create_group = findViewById(R.id.create_group);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button logout_button = (Button) findViewById(R.id.logoutBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button chat_btn = (Button) findViewById(R.id.chatBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button dash_button = (Button) findViewById(R.id.dashboardBtn);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        groupRef = database.getReference().child("group");

        // Initialize UI components
        listView = findViewById(R.id.groupRecyclerView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listofgroups);
        listView.setAdapter(arrayAdapter);
        listView.setBackgroundColor(Color.GRAY);

        // Retrieve and display groups
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iter = snapshot.getChildren().iterator();

                while (iter.hasNext()) {
                    set.add(((DataSnapshot) iter.next()).getKey());
                }
                listofgroups.clear();
                listofgroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Handle item click, for example, start a new activity
                String selectedGroupName = listofgroups.get(position);
                Intent intent = new Intent(PostsDashboard.this, PostWindo.class);
                intent.putExtra("groupName", selectedGroupName);
                startActivity(intent);
            }
        });

        create_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostsDashboard.this, NewGroup.class);
                startActivity(intent);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostsDashboard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostsDashboard.this, Dashboard.class);
                startActivity(intent);
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostsDashboard.this, ChatUsers.class);
                startActivity(intent);
            }
        });
    }
}




    /*

        database= FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        create_group = (Button) findViewById(R.id.create_group);


        DatabaseReference reference = database.getReference().child("group");

        groupArrayList = new ArrayList<>();

        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        create_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostsDashboard.this, NewGroup.class);
                startActivity(intent);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Check if the data is of type String
                    if (dataSnapshot.getValue() instanceof String) {
                        String stringValue = (String) dataSnapshot.getValue();
                        // Convert the String to a Groups object
                        Groups groups = Groups.fromString(stringValue);
                        // Continue processing the Groups object
                        // Add your logic to handle the Groups object
                    } else {
                        // Convert the data to Groups object
                        try {
                            Groups groups = dataSnapshot.getValue(Groups.class);
                            if (groups != null) {
                                // Continue processing the Groups object
                                // Add your logic to handle the Groups object
                            } else {
                                Log.e("PostsDashboard", "Failed to convert data to Groups object");
                            }
                        } catch (DatabaseException e) {
                            // Handle the exception (e.g., log or show a Toast)
                            Log.e("PostsDashboard", "Error converting data to Groups object: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
