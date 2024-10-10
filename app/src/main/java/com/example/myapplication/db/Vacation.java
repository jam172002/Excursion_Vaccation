package com.example.myapplication.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacation")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    public int id;  // This will be auto-generated
    public String title;
    public String hotel;
    public String startDate;
    public String endDate;

    // Constructor for Room (with auto-generated ID)
    public Vacation(String title, String hotel, String startDate, String endDate) {
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // This constructor will be ignored by Room
    @Ignore
    public Vacation(int id, String title, String hotel, String startDate, String endDate) {
        this.id = id;
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @NonNull
    @Override
    public String toString() {
        return title + " (" + startDate + " to " + endDate + ")";
    }
    // Getters and Setters (optional)
}
