package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView aboutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        aboutTextView = findViewById(R.id.aboutTextView);

        // Set the text to "About"
        aboutTextView.setText("About");

        // Back button functionality
        backButton.setOnClickListener(v -> {
            finish(); // Close this activity and return to the previous one
        });
    }
}

