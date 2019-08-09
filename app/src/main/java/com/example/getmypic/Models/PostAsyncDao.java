package com.example.getmypic.Models;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
interface PostsDao{
    @Query("select * from Posts")
    List<Posts> getAll();

    @Query("select * from Posts")
    LiveData<List<Posts>> getAllPosts();

    @Query("select * from Posts where id = :id")
    Posts get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Posts... students);

    @Insert
    void insert(List<Posts> students);

    @Delete
    void delete(Posts student);
}

public class PostAsyncDao {
    public static void getAllPosts(final MainModel.GetAllPostsListener listener) {
        new AsyncTask<String,String,List<Posts>>(){

            @Override
            protected List<Posts> doInBackground(String... strings) {
                List<Posts> list = SQLite.db.postsDao().getAll();
                return list;
            }

            @Override
            protected void onPostExecute(List<Posts> data) {
                super.onPostExecute(data);
                listener.onComplete(data);

            }
        }.execute();

    }
}
