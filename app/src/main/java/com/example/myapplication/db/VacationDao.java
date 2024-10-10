package com.example.myapplication.db;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VacationDao {
    @Insert
    Long insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacation")
    List<Vacation> getAllVacations();
    @Query("SELECT * FROM vacation WHERE id = :vacationId LIMIT 1")
    Vacation getVacationById(int vacationId);
}
