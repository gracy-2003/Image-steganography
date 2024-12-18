package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // If already logged in, skip MainActivity and go directly to WelcomeActivity
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so the user can't come back to it
        } else {
            // If not logged in, allow login or sign up
            loginButton.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish(); // Close MainActivity so user can't come back to it
            });

            signupButton.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
                finish(); // Close MainActivity so user can't come back to it
            });
        }
    }
}
