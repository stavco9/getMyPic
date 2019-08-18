package com.example.getmypic.Models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.getmypic.GetMyPicApplication;
import com.example.getmypic.MainActivity;

import java.util.Date;

@Database(entities = {Posts.class}, version = 22)
@TypeConverters({Converters.class})
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostsDao postsDao();
}
public class SQLite {

    static  public AppLocalDbRepository db = Room.databaseBuilder(GetMyPicApplication.getContext(),
            AppLocalDbRepository.class,
            "getmypic.db")
            .fallbackToDestructiveMigration()
            .build();
}

class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}