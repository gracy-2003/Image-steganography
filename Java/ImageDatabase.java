package com.example.miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "image_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "images";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FILE_NAME = "file_name";
    private static final String COLUMN_IMAGE = "image";

    private static ImageDatabase instance;

    public static synchronized ImageDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ImageDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private ImageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FILE_NAME + " TEXT,"
                + COLUMN_IMAGE + " BLOB"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addImage(String fileName, Bitmap image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_IMAGE, getBitmapAsByteArray(image));
        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("ImageDatabase", "Failed to insert image into database");
        } else {
            Log.d("ImageDatabase", "Image inserted successfully with ID: " + result);
        }
        db.close();
        return result;
    }

    public List<ImageEntity> getAllImages() {
        List<ImageEntity> imageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_NAME));
                byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                if (bitmap != null) {
                    imageList.add(new ImageEntity(id, fileName, bitmap));
                } else {
                    Log.e("ImageDatabase", "Failed to decode image data for ID: " + id);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imageList;
    }

    public ImageEntity getImage(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_FILE_NAME, COLUMN_IMAGE},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        ImageEntity image = null;
        if (cursor != null && cursor.moveToFirst()) {
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_NAME));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            if (bitmap != null) {
                image = new ImageEntity(id, fileName, bitmap);
            } else {
                Log.e("ImageDatabase", "Failed to decode image data for ID: " + id);
            }
            cursor.close();
        }
        db.close();
        return image;
    }

    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}

