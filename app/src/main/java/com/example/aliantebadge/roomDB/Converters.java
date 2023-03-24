package com.example.aliantebadge.roomDB;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * Metodi invocati automaticamente da RoomDB per effettuare conversioni dei tipi.
 *
 * Per effettuare le conversioni sono utilizzati i metodi fromJson() e toJson() della classe Gsos
 * per convertire un oggetto, in modo da memorizzarli nel DB come stinghe.
 *
 */
public class Converters {

    @TypeConverter
    public static ArrayList<String> fromStringToArrayListString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListStringToString(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ArrayList<Map<String, Double>> fromStringToArrayListLong(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListLongToString(ArrayList<Map<String, Double>> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
