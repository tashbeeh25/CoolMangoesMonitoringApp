package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChatsDashboard extends AppCompatActivity {

    LinearLayout profileLayOut, profileLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_dashboard);

        profileLayOut = findViewById(R.id.profileLayout);
        profileLayout2 = findViewById(R.id.profileLayout2);
        Button logout_button = (Button) findViewById(R.id.logoutBtn);
        Button setting_button = (Button) findViewById(R.id.setting);
        Button home = (Button) findViewById(R.id.dashboardBtn);

        profileLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDashboard.this, ChatUsers.class);
                startActivity(intent);
            }
        });

        profileLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDashboard.this, PostsDashboard.class);
                startActivity(intent);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDashboard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDashboard.this, Dashboard.class);
                startActivity(intent);
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDashboard.this, Setting.class);
                startActivity(intent);
            }
        });
    }
}