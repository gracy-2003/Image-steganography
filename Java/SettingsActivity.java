package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView userNameTextView;
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userDatabase = new UserDatabase(this);

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Account Option
        TextView accountOption = findViewById(R.id.accountOption);
        accountOption.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        // About Option
        TextView aboutOption = findViewById(R.id.aboutOption);
        aboutOption.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Encoded Images Option
        TextView encodedImagesOption = findViewById(R.id.encodedImagesOption);
        encodedImagesOption.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, EncodedImagesActivity.class);
            startActivity(intent);
        });

        // Help & Support Option
        TextView helpSupportOption = findViewById(R.id.helpSupportOption);
        helpSupportOption.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HelpAndSupportActivity.class);
            startActivity(intent);
        });

        // FAQs Option
        TextView faqsOption = findViewById(R.id.faqs);
        faqsOption.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, FAQActivity.class);
            startActivity(intent);
        });

        // Logout Option
        TextView logoutOption = findViewById(R.id.logoutOption);
        logoutOption.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize profile image view and username text view
        profileImageView = findViewById(R.id.profileImageView);
        userNameTextView = findViewById(R.id.userNameTextView);

        // Load profile image and username
        loadProfileImage();
        displayUserName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileImage();
        displayUserName();
    }

    private void loadProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (!username.isEmpty()) {
            UserDatabase.UserData userData = userDatabase.getUserData(username);
            if (userData != null && userData.profileImage != null) {
                profileImageView.setImageBitmap(userData.profileImage);
            } else {
                // Fallback to SharedPreferences if database doesn't have the image
                String encodedImage = sharedPreferences.getString("profilePhoto", "");
                if (!encodedImage.isEmpty()) {
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profileImageView.setImageBitmap(decodedByte);
                }
            }

            // Set outline provider to clip the image to a circle
            profileImageView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            profileImageView.setClipToOutline(true);
        }
    }

    private void displayUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");
        userNameTextView.setText(username);
    }
}
