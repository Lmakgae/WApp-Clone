package com.hlogi.wappclone.chats.data.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MessageReceipt {
    public String sender;
    public String message_id;
    public Long timestamp;

    public MessageReceipt(@NonNull String sender, @NonNull String message_id, @NonNull Long timestamp) {
        this.sender = sender;
        this.message_id = message_id;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageReceipt{" +
                "sender='" + sender + '\'' +
                ", message_id='" + message_id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageReceipt)) return false;
        MessageReceipt that = (MessageReceipt) o;
        return getSender().equals(that.getSender()) &&
                getMessage_id().equals(that.getMessage_id()) &&
                getTimestamp().equals(that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender(), getMessage_id(), getTimestamp());
    }
}
