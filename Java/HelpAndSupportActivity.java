package com.example.miniproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HelpAndSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        // Back Button functionality
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity
            }
        });

        // Email click handler
        TextView contactEmail = findViewById(R.id.contactEmail);
        contactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open email app with pre-filled email address
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@yourapp.com")); // Use the support email
                intent.putExtra(Intent.EXTRA_SUBJECT, "Help and Support Request"); // Optional subject
                try {
                    startActivity(Intent.createChooser(intent, "Send email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HelpAndSupportActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Phone click handler
        TextView contactPhone = findViewById(R.id.contactPhone);
        contactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialer with the phone number pre-filled
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+1234567890")); // Phone number
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HelpAndSupportActivity.this, "No dialer app installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

