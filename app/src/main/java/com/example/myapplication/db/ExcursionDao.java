package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ExcursionDao {

    @Insert
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursion WHERE vacationId = :vacationId ")
   List<Excursion> getExcursionsByVacationId(int vacationId);
    @Query("SELECT * FROM excursion")
    List<Excursion> getAllExcursions();
    @Query("SELECT * FROM excursion WHERE id = :exId LIMIT 1")
    Excursion getExcursionById(int exId);




}
