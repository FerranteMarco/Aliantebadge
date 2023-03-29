package com.example.aliantebadge.roomDB.Dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.aliantebadge.roomDB.Entity.OtherUser;

import java.util.List;

@Dao
public interface OtherUserDao {

    @Insert
    void insertUser(OtherUser users);


    @Query("DELETE FROM otheruser")
    void deleteAll();


    @Query("SELECT * FROM otheruser")
    List<OtherUser> getAllUsers();
}
