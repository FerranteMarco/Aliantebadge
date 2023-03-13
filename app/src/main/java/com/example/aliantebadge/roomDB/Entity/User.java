package com.example.aliantebadge.roomDB.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


/**
 *  Classe usata per memorizzare in locale e visualizzare le informazioni dell'utente loggato nell'app.
 *
 *  Usiamo le annotazioni per specificare tutti i vincoli su di essi (esempio: @PrimaryKey, @NotNull,
 *  @ColumnInfo, etc.).
 *  RoomDB li usa per costruire automaticamente i comandi SQL per la costruzione del DB
 *
 */
@Entity
public class User {

    @PrimaryKey
    @NotNull
    public String uid;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String secondName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "accepted")
    public boolean accepted;

}
