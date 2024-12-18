package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Find the settings button
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        // Set click listener for the settings button
        settingsButton.setOnClickListener(view -> {
            // Navigate to SettingsActivity
            Intent intent = new Intent(WelcomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Find the encode button
        Button encodeButton = findViewById(R.id.encodeButton);

        // Set click listener for the encode button
        encodeButton.setOnClickListener(view -> {
            // Navigate to EncodeActivity
            Intent intent = new Intent(WelcomeActivity.this, EncodeActivity.class);
            startActivity(intent);
        });

        // Find the decode button
        Button decodeButton = findViewById(R.id.decodeButton);

        // Set click listener for the decode button
        decodeButton.setOnClickListener(view -> {
            // Navigate to DecodeActivity
            Intent intent = new Intent(WelcomeActivity.this, DecodeActivity.class);
            startActivity(intent);
        });

        // Retrieve username from SharedPreferences to persist login
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        // Check if the user is logged in, otherwise redirect to login
        if (username == null) {
            // If no username, redirect to MainActivity (Login screen)
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity so that the user can't return to it
        } else {
            // Find the userNameTextView and set the username
            TextView userNameTextView = findViewById(R.id.userNameTextView);
            userNameTextView.setText("Welcome, " + username);
        }
    }
}
