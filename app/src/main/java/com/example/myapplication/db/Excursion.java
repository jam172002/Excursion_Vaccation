package com.example.myapplication.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "excursion")
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String exTitle;
    public String exDate;
    public int vacationId;



        // Constructor
    @Ignore
    public Excursion(int id, String exTitle, String exDate) {
            this.id = id;
            this.exTitle = exTitle;
            this.exDate = exDate;
        }
    public Excursion() {
    }



    @Override
    public String toString() {
        return exTitle + " on " + exDate;
    }
}
