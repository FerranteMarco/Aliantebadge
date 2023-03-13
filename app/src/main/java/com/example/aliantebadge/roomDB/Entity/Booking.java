package com.example.aliantebadge.roomDB.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Booking {

        @PrimaryKey
        @NotNull
        public String id_booking;

        @ColumnInfo(name = "date")
        public Long date;

        @ColumnInfo(name = "time")
        public Long time;

        @ColumnInfo(name = "user")
        public String user_id;

        @ColumnInfo(name = "nameReservation")
        public String nameReservation;



    }
