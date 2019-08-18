package com.example.getmypic.Models;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.LinkedList;
import java.util.List;

@Dao
interface PostsDao{
    @Query("select * from Posts order by uploadedDate desc")
    List<Posts> getAll();

    @Query("select * from Posts where userEmail = :userEmail order by uploadedDate desc")
    List<Posts> getAllByUser(String userEmail);

    @Query("select * from Posts order by uploadedDate desc")
    LiveData<List<Posts>> getAllPosts();

    @Query("select * from Posts where userEmail = :userEmail order by uploadedDate desc")
    LiveData<List<Posts>> getAllPostsByUser(String userEmail);

    @Query("select * from Posts where id = :id")
    Posts get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Posts... posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void set(List<Posts> posts);

    @Delete
    void delete(Posts... post);
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

    public static void getAllPostsByUser(final MainModel.GetAllPostsListener listener) {
        new AsyncTask<String,String,List<Posts>>(){

            @Override
            protected List<Posts> doInBackground(String... strings) {

                List<Posts> list = new LinkedList<>();

                if (Users.isAuthenticated()){
                    list = SQLite.db.postsDao().getAllByUser(Users.getUser().getEmail());
                }

                return list;
            }

            @Override
            protected void onPostExecute(List<Posts> data) {
                super.onPostExecute(data);
                listener.onComplete(data);

            }
        }.execute();

    }

    public static void setPosts(Posts posts, final MainModel.AddPostListener listener){
        new AsyncTask<Posts, String, Boolean>(){

            @Override
            protected Boolean doInBackground(Posts... posts) {
                try{
                    SQLite.db.postsDao().insertAll(posts);
                    return true;
                }
                catch (Exception e){
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);
            }
        }.execute(posts);
    }

    public static void deletePost(Posts posts, final MainModel.DeletePostListener listener){
        new AsyncTask<Posts, String, Boolean>(){

            @Override
            protected Boolean doInBackground(Posts... posts) {
                try{
                    SQLite.db.postsDao().delete(posts);

                    return true;
                }
                catch (Exception e){
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);

            }
        }.execute(posts);
    }
}
