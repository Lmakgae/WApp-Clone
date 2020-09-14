package com.hlogi.wappclone.contacts.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.hlogi.wappclone.contacts.data.model.Contact;

import java.util.List;

@Dao
public interface ContactsDao {

    @Query("SELECT * FROM contacts_table WHERE id is not NULL ORDER BY display_name ASC")
    DataSource.Factory<Integer, Contact> getAllContactsOnWApp();

    @Query("SELECT * FROM contacts_table WHERE number = :number")
    LiveData<Contact> getContact(String number);

    @Query("SELECT * FROM contacts_table WHERE number = :number")
    Contact getContactObject(String number);

    @Query("SELECT * FROM contacts_table WHERE id = :id")
    LiveData<List<Contact>> getContactsOtherNumbers(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Contact> contact);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM contacts_table WHERE id = :number")
    void delete(String number);

    @Query("DELETE FROM contacts_table")
    void deleteAllContacts();

}
