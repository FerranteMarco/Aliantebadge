package com.example.aliantebadge.roomDB.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Entity
public class Badge {

        @PrimaryKey
        @NotNull
        public String id_badge;

        @ColumnInfo(name = "inizio")
        public Long inizio;

        @ColumnInfo(name = "fine")
        public Long fine;

        @ColumnInfo(name = "operator_id")
        public String operator_id;

        @ColumnInfo(name = "operator_name")
        public String operator_name;

        @ColumnInfo(name = "phoneModel")
        public String phoneModel;

        @ColumnInfo(name = "geo_location")
        public ArrayList<Long> geo_location;

        @ColumnInfo(name = "geoTimes")
        public ArrayList<Long> geoTimes;

    }
