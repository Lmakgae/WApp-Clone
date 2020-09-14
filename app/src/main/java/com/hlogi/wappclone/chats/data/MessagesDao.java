package com.hlogi.wappclone.chats.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hlogi.wappclone.chats.data.model.Message;

import java.util.List;

@Dao
public interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE conversation_id = :chat_convo ORDER BY time_stamp ASC")
    DataSource.Factory<Integer, Message> getAllMessagesFromChatConvo(String chat_convo);

    @Query("SELECT * FROM messages_table WHERE message_id = :id AND conversation_id = :chat_convo_id")
    Message getMessage(String chat_convo_id, String id);

    @Query("SELECT * FROM messages_table WHERE message_id = :id AND conversation_id = :chat_convo_id")
    LiveData<Message> getMessageLiveData(String chat_convo_id, String id);

    @Query("SELECT * FROM messages_table WHERE conversation_id = :chat_convo_id ORDER BY time_stamp DESC LIMIT 1")
    LiveData<Message> getLastMessageFromChatConvo(String chat_convo_id);

    @Query("SELECT * FROM messages_table WHERE conversation_id = :chat_convo  AND starred = :starred ORDER BY time_stamp ASC")
    DataSource.Factory<Integer, Message> getAllStarredMessagesFromChat(String chat_convo, Boolean starred);

    @Query("SELECT * FROM messages_table WHERE starred = :starred ORDER BY time_stamp ASC")
    DataSource.Factory<Integer, Message> getAllStarredMessages(Boolean starred);

    @Query("SELECT * FROM messages_table WHERE read = :unread AND conversation_id = :chat_convo_id AND receiver = :number")
    LiveData<List<Message>> getUnreadMessagesFromChat(String chat_convo_id, String number, Boolean unread);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Message> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Query("UPDATE messages_table SET has_pending_writes = :has_pending_writes AND from_local_cache = :from_cache WHERE message_id = :message_id AND conversation_id = :conversation_id")
    void messageSentUpdate(String message_id, String conversation_id, Boolean has_pending_writes, Boolean from_cache);

    @Query("UPDATE messages_table SET delivered = :delivered, delivered_time_stamp = :delivery_timestamp WHERE message_id = :message_id AND conversation_id = :conversation_id")
    void messageDeliveredUpdate(String message_id, String conversation_id, Boolean delivered, Long delivery_timestamp);

    @Query("UPDATE messages_table SET read = :read, read_time_stamp = :read_timestamp WHERE message_id = :message_id AND conversation_id = :conversation_id")
    void messageReadUpdate(String message_id, String conversation_id, Boolean read, Long read_timestamp);

    @Query("UPDATE messages_table SET starred = :starred WHERE message_id = :message_id")
    void starMessage(String message_id, Boolean starred);

    @Delete
    void delete(Message message);

    @Query("DELETE FROM messages_table")
    void deleteAllMessage();

}
