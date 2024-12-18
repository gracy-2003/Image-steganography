package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ImageButton backButton;
    private TextView signUpText, forgotPasswordText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);
        signUpText = findViewById(R.id.signUpText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            if (isValidInput()) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                boolean isUserValid = dbHelper.checkUserCredentials(email, password);

                if (isUserValid) {
                    String username = dbHelper.getUsernameByEmail(email);

                    // Store login status and username in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", username);
                    editor.putString("email", email); // Storing email for future reference
                    editor.apply();

                    // Navigate to WelcomeActivity and pass the username
                    Intent intentWelcome = new Intent(LoginActivity.this, WelcomeActivity.class);
                    intentWelcome.putExtra("username", username);
                    startActivity(intentWelcome);
                    finish(); // Close LoginActivity to prevent going back
                } else {
                    if (dbHelper.isEmailExists(email)) {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email not registered. Please sign up.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signUpText.setOnClickListener(v -> {
            Intent intentSignUp = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intentSignUp);
            finish(); // Close LoginActivity to prevent going back
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity so the user can't come back to it
        });

        forgotPasswordText.setOnClickListener(v -> {
            Intent intentForgotPassword = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intentForgotPassword);
        });
    }

    private boolean isValidInput() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity so the user can't go back to it
        super.onBackPressed();  // Call super to ensure proper handling of back press
    }
}
