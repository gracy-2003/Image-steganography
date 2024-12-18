package com.example.miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class UserDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserData.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "user_data";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_USERNAME + " TEXT PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_DOB + " TEXT,"
                + COLUMN_GENDER + " TEXT,"
                + COLUMN_PROFILE_IMAGE + " BLOB"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertOrUpdateUserData(String username, String name, String email, String dob, String gender, Bitmap profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_GENDER, gender);

        if (profileImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            profileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put(COLUMN_PROFILE_IMAGE, byteArray);
        }

        // Check if the user already exists
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor.getCount() > 0) {
            // Update existing user
            db.update(TABLE_NAME, values, COLUMN_USERNAME + "=?", new String[]{username});
        } else {
            // Insert new user
            db.insert(TABLE_NAME, null, values);
        }
        cursor.close();
        db.close();
    }

    public UserData getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserData userData = null;

        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            int dobIndex = cursor.getColumnIndex(COLUMN_DOB);
            int genderIndex = cursor.getColumnIndex(COLUMN_GENDER);
            int profileImageIndex = cursor.getColumnIndex(COLUMN_PROFILE_IMAGE);

            String name = nameIndex != -1 ? cursor.getString(nameIndex) : null;
            String email = emailIndex != -1 ? cursor.getString(emailIndex) : null;
            String dob = dobIndex != -1 ? cursor.getString(dobIndex) : null;
            String gender = genderIndex != -1 ? cursor.getString(genderIndex) : null;
            byte[] profileImageBytes = profileImageIndex != -1 ? cursor.getBlob(profileImageIndex) : null;

            Bitmap profileImage = null;
            if (profileImageBytes != null) {
                profileImage = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
            }

            userData = new UserData(username, name, email, dob, gender, profileImage);
        }
        cursor.close();
        db.close();
        return userData;
    }

    public static class UserData {
        public String username;
        public String name;
        public String email;
        public String dob;
        public String gender;
        public Bitmap profileImage;

        public UserData(String username, String name, String email, String dob, String gender, Bitmap profileImage) {
            this.username = username;
            this.name = name;
            this.email = email;
            this.dob = dob;
            this.gender = gender;
            this.profileImage = profileImage;
        }
    }
}

