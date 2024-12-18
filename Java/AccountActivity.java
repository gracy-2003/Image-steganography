package com.example.miniproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private EditText name, username, email, dobEditText;
    private RadioGroup genderRadioGroup;
    private Button saveButton;
    private TextView editProfileText;
    private ImageView profileImageView;
    private Bitmap selectedProfileImage;
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userDatabase = new UserDatabase(this);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        dobEditText = findViewById(R.id.dob);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        saveButton = findViewById(R.id.saveButton);
        editProfileText = findViewById(R.id.editProfileLink);
        profileImageView = findViewById(R.id.userImageView);

        dobEditText.setEnabled(false);
        genderRadioGroup.setEnabled(false);
        for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
            genderRadioGroup.getChildAt(i).setEnabled(false);
        }
        saveButton.setVisibility(View.GONE);

        // Load user data from SharedPreferences and Database
        loadUserData();

        editProfileText.setOnClickListener(v -> {
            editProfileText.setText("Edit Photo");
            dobEditText.setEnabled(true);
            genderRadioGroup.setEnabled(true);
            for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
                genderRadioGroup.getChildAt(i).setEnabled(true);
            }
            saveButton.setVisibility(View.VISIBLE);
            editProfileText.setOnClickListener(v1 -> openGallery());
            dobEditText.setOnClickListener(v2 -> openDatePicker());
        });

        saveButton.setOnClickListener(v -> saveUserData());

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            String username = this.username.getText().toString();
            Intent intent = new Intent();
            intent.putExtra("username", username);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String usernameStr = sharedPreferences.getString("username", "");

        // Load data from SharedPreferences
        name.setText(sharedPreferences.getString("name", ""));
        username.setText(usernameStr);
        email.setText(sharedPreferences.getString("email", ""));
        dobEditText.setText(sharedPreferences.getString("dob", ""));
        String gender = sharedPreferences.getString("gender", "");
        if (!gender.isEmpty()) {
            for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) genderRadioGroup.getChildAt(i);
                if (radioButton.getText().toString().equals(gender)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }

        // Load profile image from SharedPreferences
        String encodedImage = sharedPreferences.getString("profilePhoto", "");
        if (!encodedImage.isEmpty()) {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImageView.setImageBitmap(decodedByte);
        }

        // Load data from Database
        UserDatabase.UserData userData = userDatabase.getUserData(usernameStr);
        if (userData != null) {
            if (userData.name != null) name.setText(userData.name);
            if (userData.email != null) email.setText(userData.email);
            if (userData.dob != null) dobEditText.setText(userData.dob);
            if (userData.gender != null && !userData.gender.isEmpty()) {
                for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) genderRadioGroup.getChildAt(i);
                    if (radioButton.getText().toString().equals(userData.gender)) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }
            if (userData.profileImage != null) {
                profileImageView.setImageBitmap(userData.profileImage);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                selectedProfileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(selectedProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String currentDob = dobEditText.getText().toString();
        if (!currentDob.isEmpty()) {
            String[] dateParts = currentDob.split("/");
            if (dateParts.length == 3) {
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1;
                int year = Integer.parseInt(dateParts[2]);
                calendar.set(year, month, day);
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AccountActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                dobEditText.setText(formattedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveUserData() {
        if (isValidInput()) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String nameStr = name.getText().toString();
            String usernameStr = username.getText().toString();
            String emailStr = email.getText().toString();
            String dobStr = dobEditText.getText().toString();
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedGender = findViewById(selectedGenderId);
            String gender = selectedGender != null ? selectedGender.getText().toString() : "";

            editor.putString("name", nameStr);
            editor.putString("username", usernameStr);
            editor.putString("email", emailStr);
            editor.putString("dob", dobStr);
            editor.putString("gender", gender);

            if (selectedProfileImage != null) {
                editor.putString("profilePhoto", encodeImageToBase64(selectedProfileImage));
            }

            editor.apply();

            // Save to database
            try {
                userDatabase.insertOrUpdateUserData(usernameStr, nameStr, emailStr, dobStr, gender, selectedProfileImage);
            } catch (Exception e) {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
            }

            dobEditText.setEnabled(false);
            genderRadioGroup.setEnabled(false);
            for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
                genderRadioGroup.getChildAt(i).setEnabled(false);
            }
            saveButton.setVisibility(View.GONE);

            editProfileText.setText("Edit Profile");
            editProfileText.setOnClickListener(v -> {
                dobEditText.setEnabled(true);
                genderRadioGroup.setEnabled(true);
                for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
                    genderRadioGroup.getChildAt(i).setEnabled(true);
                }
                saveButton.setVisibility(View.VISIBLE);
                editProfileText.setText("Edit Photo");
                editProfileText.setOnClickListener(v1 -> openGallery());
                dobEditText.setOnClickListener(v2 -> openDatePicker());
            });

            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput() {
        if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email.getText().toString())) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedProfileImage == null) {
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String encodeImageToBase64(Bitmap image) {
        Bitmap resizedImage = resizeImage(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap resizeImage(Bitmap image) {
        int maxSize = 500;
        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.round(ratio * width);
        int newHeight = Math.round(ratio * height);

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, false);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
}
