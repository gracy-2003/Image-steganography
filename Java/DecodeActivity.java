package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DecodeActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final String TAG = "DecodeActivity";
    private ImageView selectedImageView;
    private Button selectImageButton, decodeButton;
    private EditText secretKeyEditText;
    private TextView decodedTextView, userNameTextView;
    private Bitmap selectedImageBitmap;
    private ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        selectedImageView = findViewById(R.id.selectedImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        decodeButton = findViewById(R.id.decodeButton);
        secretKeyEditText = findViewById(R.id.secretKeyEditText);
        decodedTextView = findViewById(R.id.decodedTextView);
        userNameTextView = findViewById(R.id.userNameTextView);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        selectImageButton.setOnClickListener(v -> openGallery());
        decodeButton.setOnClickListener(v -> decodeTextFromImage());

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DecodeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Default Username");
        userNameTextView.setText(username);

        // Check if the activity was started from a shared image
        handleSharedImage(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSharedImage(intent);
    }

    private void handleSharedImage(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    loadSharedImage(imageUri);
                }
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            loadSharedImage(selectedImageUri);
        }
    }

    private void loadSharedImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
            if (selectedImageBitmap != null) {
                selectedImageView.setVisibility(View.VISIBLE);
                selectedImageView.setImageBitmap(selectedImageBitmap);
                selectImageButton.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(this, "Failed to load the image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
            Toast.makeText(this, "Failed to load the image. File not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void decodeTextFromImage() {
        String secretKey = secretKeyEditText.getText().toString();

        if (selectedImageBitmap == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (secretKey.isEmpty() || secretKey.length() != 6) {
            Toast.makeText(this, "Please enter a valid 6-digit secret key", Toast.LENGTH_SHORT).show();
            return;
        }

        String decodedText = decodeTextFromImage(selectedImageBitmap, secretKey);
        if (decodedText != null && !decodedText.isEmpty()) {
            decodedTextView.setText(decodedText);
            decodedTextView.setVisibility(View.VISIBLE);
            secretKeyEditText.setVisibility(View.GONE);
            decodeButton.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Failed to decode. Invalid secret key or no hidden message.", Toast.LENGTH_SHORT).show();
        }
    }

    private String decodeTextFromImage(Bitmap image, String pin) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        int messageLength = 0;
        for (int i = 0; i < 32; i++) {
            int pixel = pixels[i];
            messageLength = (messageLength << 1) | (pixel & 1);
        }

        if (messageLength <= 0 || messageLength > pixels.length - 32) {
            Log.e(TAG, "Invalid message length: " + messageLength);
            return null;
        }

        StringBuilder decodedText = new StringBuilder();
        for (int i = 0; i < messageLength; i++) {
            char c = 0;
            for (int j = 0; j < 8; j++) {
                int pixelIndex = 32 + i * 8 + j;
                if (pixelIndex >= pixels.length) {
                    Log.e(TAG, "Pixel index out of bounds: " + pixelIndex);
                    return null;
                }
                int pixel = pixels[pixelIndex];
                c = (char) ((c << 1) | (pixel & 1));
            }
            decodedText.append(c);
        }

        String fullDecodedText = decodedText.toString();
        if (fullDecodedText.endsWith(pin)) {
            return fullDecodedText.substring(0, fullDecodedText.length() - pin.length());
        } else {
            Log.e(TAG, "Decoded text does not end with the provided pin");
            return null;
        }
    }
}

