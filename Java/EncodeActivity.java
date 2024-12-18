// EncodeActivity.java
package com.example.miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncodeActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView selectedImageView;
    private Button selectImageButton, encodeButton, saveImageButton;
    private EditText enterTextEditText, secretKeyEditText;
    private Bitmap selectedImageBitmap;
    private Bitmap encodedImageBitmap;
    private TextView userNameTextView;
    private ImageButton settingsButton;
    private ImageDatabase imageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        imageDatabase = ImageDatabase.getInstance(this);

        selectedImageView = findViewById(R.id.selectedImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        encodeButton = findViewById(R.id.encodeButton);
        saveImageButton = findViewById(R.id.saveImageButton);
        enterTextEditText = findViewById(R.id.enterTextEditText);
        secretKeyEditText = findViewById(R.id.secretKeyEditText);
        userNameTextView = findViewById(R.id.userNameTextView);
        settingsButton = findViewById(R.id.settingsButton);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());
        selectImageButton.setOnClickListener(v -> openGallery());
        encodeButton.setOnClickListener(v -> encodeTextInImage());
        saveImageButton.setOnClickListener(v -> saveEncodedImage());

        displayUsername();

        settingsButton.setVisibility(View.VISIBLE);
        settingsButton.setOnClickListener(v -> openSettingsPage());

        userNameTextView.setVisibility(View.VISIBLE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                selectImageButton.setVisibility(View.INVISIBLE);
                selectedImageView.setVisibility(View.VISIBLE);
                selectedImageView.setImageBitmap(selectedImageBitmap);
                selectedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load the image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void encodeTextInImage() {
        String message = enterTextEditText.getText().toString();
        String pin = secretKeyEditText.getText().toString();

        if (message.isEmpty() || pin.isEmpty() || selectedImageBitmap == null) {
            Toast.makeText(this, "Please select an image, enter text and a 6-digit pin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.length() != 6) {
            Toast.makeText(this, "Please enter a 6-digit pin", Toast.LENGTH_SHORT).show();
            return;
        }

        encodedImageBitmap = encodeTextInImage(selectedImageBitmap, message, pin);
        selectedImageView.setImageBitmap(encodedImageBitmap);
        Toast.makeText(this, "Image encoded successfully!", Toast.LENGTH_SHORT).show();
    }

    private Bitmap encodeTextInImage(Bitmap image, String text, String pin) {
        Bitmap mutableImage = image.copy(Bitmap.Config.ARGB_8888, true);
        String messageWithPin = text + pin;
        int messageLength = messageWithPin.length();
        int width = mutableImage.getWidth();
        int height = mutableImage.getHeight();
        int[] pixels = new int[width * height];
        mutableImage.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < 32; i++) {
            int pixel = pixels[i];
            pixel = (pixel & 0xFFFFFFFE) | ((messageLength >> (31 - i)) & 1);
            pixels[i] = pixel;
        }

        for (int i = 0; i < messageWithPin.length(); i++) {
            char c = messageWithPin.charAt(i);
            for (int j = 0; j < 8; j++) {
                int pixelIndex = 32 + i * 8 + j;
                int pixel = pixels[pixelIndex];
                pixel = (pixel & 0xFFFFFFFE) | ((c >> (7 - j)) & 1);
                pixels[pixelIndex] = pixel;
            }
        }

        mutableImage.setPixels(pixels, 0, width, 0, 0, width, height);
        return mutableImage;
    }

    private void saveEncodedImage() {
        if (encodedImageBitmap == null) {
            Toast.makeText(this, "No encoded image to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "encoded_image_" + System.currentTimeMillis() + ".png";

        // Save image to internal folder for EncodedImagesActivity
        File directory = new File(getFilesDir(), "EncodedImages");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            encodedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Toast.makeText(this, "Image saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save the image.", Toast.LENGTH_SHORT).show();
        }

        // Save image to ImageDatabase
        long id = imageDatabase.addImage(fileName, encodedImageBitmap);
        if (id != -1) {
            Toast.makeText(this, "Image saved to database!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save the image to database.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");
        userNameTextView.setText(username);
    }

    private void openSettingsPage() {
        Intent intent = new Intent(EncodeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
