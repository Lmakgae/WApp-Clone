package com.hlogi.wappclone.chats.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.hlogi.wappclone.chats.data.model.ChatConversation;

import java.util.List;

@Dao
public interface ChatConversationDao {

    @Query("SELECT * FROM chat_conversations_table ORDER BY last_update ASC")
    DataSource.Factory<Integer, ChatConversation> getAllConversations();

    @Query("SELECT * FROM chat_conversations_table WHERE chat_convo_id = :id")
    LiveData<ChatConversation> getConversation(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatConversation conversation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ChatConversation> conversations);

//    @Query()
//    void updateContact(ChatConversation conversation);

    @Delete
    void delete(ChatConversation conversation);

    @Query("DELETE FROM chat_conversations_table")
    void deleteAllChat_Conversations();

}
