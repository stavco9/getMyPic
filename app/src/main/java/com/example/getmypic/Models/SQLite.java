package com.example.getmypic.Models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.getmypic.GetMyPicApplication;

@Database(entities = {Posts.class}, version = 20)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostsDao postsDao();
}
public class SQLite {

    static  Context context;

    static public AppLocalDbRepository db = Room.databaseBuilder(GetMyPicApplication.getContext(),
            AppLocalDbRepository.class,
            "getmypic.db")
            .fallbackToDestructiveMigration()
            .build();
}