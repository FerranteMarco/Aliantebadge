package com.example.aliantebadge.roomDB.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.aliantebadge.roomDB.Entity.Badge;

import java.util.List;

@Dao
public interface BadgeDao {

    @Query("SELECT * FROM Badge WHERE inizio =:date")
    List<Badge> getAllBadgeOnDay(long date);

    @Query("SELECT * FROM Badge ")
    List<Badge> getAllBadge();

    @Query("SELECT * FROM Badge WHERE operator_id =:user_id")
    List<Badge> getAllBadgeByUser(String user_id);

    @Insert
    void insertBadge(Badge reservation);

    @Query("DELETE FROM Badge")
    void deleteAll();
}
