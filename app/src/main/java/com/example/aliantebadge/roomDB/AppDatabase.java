package com.example.aliantebadge.roomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.aliantebadge.roomDB.Dao.*;
import com.example.aliantebadge.roomDB.Entity.*;

/**
 * In entities devono essere riportate tutte le classi che definiscono le entità del DB.
 *
 * Ad ogni aggiornamento al DB corrisponde un upgrade della versione (currentVersion+1), da fare manualmente
 */
@Database(entities = {
        User.class,
        Badge.class
}, version = 2, exportSchema = false)
@TypeConverters(com.example.aliantebadge.roomDB.Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Dichiarazione dei DAO (Data Access Object), ognuno associato ad ogni entità del DB
     */
    public abstract UserDao userDao();
    public abstract BadgeDao badgeDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        // Crezione del DB
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "GRBarber_DB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()   // permette a Room di distruggere e ricreare il DB a seguito di un upgrade di versione
                    .build();

        }
        return INSTANCE;
    }

}
