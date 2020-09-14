package com.hlogi.wappclone.chats.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.hlogi.wappclone.chats.data.DataMessageNames;

import java.util.Objects;

public class MessageMetadata {

    @ColumnInfo(name = DataMessageNames.COL_HAS_PENDING_WRITES)
    private Boolean hasPendingWrites;

    @ColumnInfo(name = DataMessageNames.COL_FROM_CACHE)
    private Boolean fromCache;

    @Ignore
    public MessageMetadata() {
    }

    public MessageMetadata(Boolean hasPendingWrites, Boolean fromCache) {
        this.hasPendingWrites = hasPendingWrites;
        this.fromCache = fromCache;
    }

    public Boolean getHasPendingWrites() {
        return hasPendingWrites;
    }

    public void setHasPendingWrites(Boolean hasPendingWrites) {
        this.hasPendingWrites = hasPendingWrites;
    }

    public Boolean getFromCache() {
        return fromCache;
    }

    public void setFromCache(Boolean fromCache) {
        this.fromCache = fromCache;
    }

    @Override
    public String toString() {
        return "MessageMetadata{" +
                "hasPendingWrites=" + hasPendingWrites +
                ", fromCache=" + fromCache +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageMetadata)) return false;
        MessageMetadata that = (MessageMetadata) o;
        return Objects.equals(getHasPendingWrites(), that.getHasPendingWrites()) &&
                Objects.equals(getFromCache(), that.getFromCache());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHasPendingWrites(), getFromCache());
    }
}
