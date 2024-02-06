package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    LinearLayout profileLayOut, profileLayout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        profileLayOut = findViewById(R.id.profileLayout);
        profileLayout2 = findViewById(R.id.profileLayout2);
        Button logout_button = (Button) findViewById(R.id.logoutBtn);
        Button chat_button = (Button) findViewById(R.id.chatBtn);
        Button home_button = (Button) findViewById(R.id.dashboardBtn);

        profileLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, Profile.class);
                startActivity(intent);
            }
        });

        profileLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, ChangeDetails.class);
                startActivity(intent);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent);
            }
        });


        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, Dashboard.class);
                startActivity(intent);
            }
        });

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, ChatsDashboard.class);
                startActivity(intent);
            }
        });
    }
}