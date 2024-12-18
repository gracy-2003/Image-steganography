package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotEmailEditText;
    private Button resetPasswordButton;
    private ImageButton backButton;
    private TextView backToLoginText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        forgotEmailEditText = findViewById(R.id.forgotEmail);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backButton = findViewById(R.id.backButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        dbHelper = new DatabaseHelper(this);

        // Set OnClickListener for the Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set OnClickListener for the Reset Password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmail()) {
                    String email = forgotEmailEditText.getText().toString().trim();
                    if (dbHelper.isEmailExists(email)) {
                        // Email exists, navigate to ResetPasswordActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Email not found in the database", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set OnClickListener for Back to Login TextView
        backToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the LoginActivity
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Optionally close ForgotPasswordActivity
            }
        });
    }

    // Validate email input for password reset
    private boolean isValidEmail() {
        String email = forgotEmailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            forgotEmailEditText.setError("Email is required");
            return false;
        }

        // You can add more email validation logic here if needed
        return true;
    }
}

