package com.example.aliantebadge.roomDB.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aliantebadge.roomDB.Entity.User;

import java.util.List;

/**
 *  DAO - Data Access Object
 *  Interfaccia che espone i metodi rappresentanti i possibili comandi SQL che si possono eseguire
 *  sull'entità User.
 *
 *  Usiamo le seguenti annotazioni:
 *      - @Insert, per specificare a RoomDB di generare automaticamente un comando SQL per inserire
 *  gli elementi specificati nei paramentri della funzione
 *      - @Delete, per specificare a RoomDB di generare automaticamente un comando SQL per eliminare
 *  gli elementi dell'entità User
 *      - @Query, per definire le interrogazioni che non possono essere generate automaticamente
 *  da RoomDB
 *
 *  L'espressione "User... users" usata in alcuni parametri di input dei metodi esposti
 *  dall'interfaccia, indica che si possono ricevere in input più istanze dell'entità User.
 *
 *  Usiamo i LiveData come tipo di ritorno quando si vuole monitoriare e catturare ogni variazione
 *  delle informazioni memorizzate, e aggiornarle in tempo reale all'interno dell'app.
 *
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAllUsers();


    @Query("SELECT * FROM user limit 1")
    User getCurrentUser();


    @Insert
    void insertUser(User... users);

    @Delete
    void delete(User user);

    @Update
    void updateUser(User... users);

    @Query("DELETE FROM user WHERE uid = :uid")
    void deleteByUserId(String uid);


    @Query("select * FROM user WHERE accepted = :val")
    List<User> getUserToAccept(boolean val);



    @Query("DELETE FROM user")
    void deleteAll();
}