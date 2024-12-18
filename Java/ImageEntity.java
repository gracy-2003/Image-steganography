package com.example.miniproject;

import android.graphics.Bitmap;

public class ImageEntity {
    private long id;
    private String fileName;
    private Bitmap image;

    public ImageEntity(long id, String fileName, Bitmap image) {
        this.id = id;
        this.fileName = fileName;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public Bitmap getImage() {
        return image;
    }
}

