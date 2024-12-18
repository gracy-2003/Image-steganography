package com.example.miniproject;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class EncodedImagesActivity extends AppCompatActivity {

    private LinearLayout imageContainer;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encoded_images);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        imageContainer = findViewById(R.id.imageContainer);
        scrollView = findViewById(R.id.scrollView);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Load encoded images
        loadEncodedImages();
    }

    private void loadEncodedImages() {
        File directory = new File(getFilesDir(), "EncodedImages");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    addImageToContainer(file);
                }
            }
        }
        toggleScrollView();
    }

    private void addImageToContainer(File imageFile) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.encoded_image_item, imageContainer, false);
        ImageView imageView = itemView.findViewById(R.id.encodedImageView);
        Button saveButton = itemView.findViewById(R.id.saveImageButton);

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        saveButton.setOnClickListener(v -> saveImageToGallery(bitmap, imageFile.getName()));

        imageContainer.addView(itemView);
    }

    private void saveImageToGallery(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EncodedImages");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(imageUri)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Image saved to gallery!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save the image to gallery.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create new MediaStore record.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleScrollView() {
        imageContainer.post(() -> {
            int contentHeight = imageContainer.getMeasuredHeight();
            int scrollHeight = scrollView.getHeight();
            scrollView.setFillViewport(contentHeight <= scrollHeight);
        });
    }
}
