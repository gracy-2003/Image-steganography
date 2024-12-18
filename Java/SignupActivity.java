package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private CheckBox termsCheckBox;
    private TextView signInText, termsLink;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        nameEditText = findViewById(R.id.name);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        signUpButton = findViewById(R.id.signUpButton);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        signInText = findViewById(R.id.signInText);
        termsLink = findViewById(R.id.termsLink);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());

        signUpButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveUserData();
            }
        });

        signInText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        termsLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, TermsAndPolicyActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        String name = nameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return false;
        }

        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Confirm Password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "You must agree to the Terms and Policy", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserData() {
        String name = nameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        DatabaseHelper dbHelper = new DatabaseHelper(SignupActivity.this);
        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(SignupActivity.this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
        } else {
            boolean isInserted = dbHelper.addUser(name, username, email, password);  // Ensure this method exists
            if (isInserted) {
                Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                saveToSharedPreferences(name, username, email); // Save name, username, and email
                navigateToWelcomeActivity(username); // Navigate to WelcomeActivity
            } else {
                Toast.makeText(SignupActivity.this, "Error signing up. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToSharedPreferences(String name, String username, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.putString("name", name);  // Save name
        editor.putString("email", email); // Save email
        editor.apply();
    }

    private void navigateToWelcomeActivity(String username) {
        Intent intentWelcome = new Intent(SignupActivity.this, WelcomeActivity.class);
        intentWelcome.putExtra("username", username);
        startActivity(intentWelcome);
        finish(); // Close SignupActivity
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Call the parent class's onBackPressed method to handle the back press behavior
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SignupActivity so the user can't go back to it
    }
}
