package com.hlogi.wappclone.auth.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hlogi.wappclone.auth.data.model.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_table LIMIT 1")
    LiveData<User> getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

//    @Query()
//    void updateContact(User user);

    @Query("DELETE FROM user_table")
    void delete();

}
