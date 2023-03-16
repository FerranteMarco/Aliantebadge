package com.example.aliantebadge.roomDB.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.aliantebadge.roomDB.Entity.Booking;

import java.util.List;

@Dao
public interface BookingDao {

    @Query("SELECT * FROM booking WHERE date =:date")
    List<Booking> getAllReservationOnDay(long date);

    @Query("SELECT * FROM booking ")
    List<Booking> getAllReservation();

    @Query("SELECT * FROM booking WHERE user =:user_id")
    List<Booking> getAllReservationByUser(String user_id);

    @Query("SELECT * FROM booking WHERE date <:date")
    List<Booking> getAllReservationPassed(long date);

    @Insert
    void insertReservation(Booking reservation);


    @Query("DELETE FROM booking")
    void deleteAll();
}
