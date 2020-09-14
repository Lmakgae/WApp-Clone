package com.hlogi.wappclone.chats.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hlogi.wappclone.chats.data.DataMessageNames;

import java.util.Objects;

@Entity(tableName = DataMessageNames.UPLOAD_MEDIA_SESSION_TABLE_NAME)
public class UploadMediaSession {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DataMessageNames.COL_ID)
    private String message_id;
    @ColumnInfo(name = DataMessageNames.COL_CONVERSATION_ID)
    private String chat_convo;
    @ColumnInfo(name = DataMessageNames.COL_URI_STRING)
    private String uri_string;

    public UploadMediaSession(@NonNull String message_id, String chat_convo, String uri_string) {
        this.message_id = message_id;
        this.chat_convo = chat_convo;
        this.uri_string = uri_string;
    }

    @NonNull
    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(@NonNull String message_id) {
        this.message_id = message_id;
    }

    public String getChat_convo() {
        return chat_convo;
    }

    public void setChat_convo(String chat_convo) {
        this.chat_convo = chat_convo;
    }

    public String getUri_string() {
        return uri_string;
    }

    public void setUri_string(String uri_string) {
        this.uri_string = uri_string;
    }

    @NonNull
    @Override
    public String toString() {
        return "UploadMediaSession{" +
                "message_id='" + message_id + '\'' +
                ", chat_convo='" + chat_convo + '\'' +
                ", uri_string='" + uri_string + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadMediaSession)) return false;
        UploadMediaSession that = (UploadMediaSession) o;
        return getMessage_id().equals(that.getMessage_id()) &&
                Objects.equals(getChat_convo(), that.getChat_convo()) &&
                Objects.equals(getUri_string(), that.getUri_string());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage_id(), getChat_convo(), getUri_string());
    }
}
