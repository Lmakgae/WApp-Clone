package com.hlogi.wappclone.chats.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hlogi.wappclone.chats.data.model.UploadMediaSession;

@Dao
public interface UploadMediaSessionDao {

    @Query("SELECT * FROM upload_media_sessions_table WHERE message_id = :message_id AND conversation_id = :chat_convo_id")
    UploadMediaSession getUploadMediaSession(String chat_convo_id, String message_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UploadMediaSession uploadMediaSession);

    @Delete
    void delete(UploadMediaSession uploadMediaSession);

}
