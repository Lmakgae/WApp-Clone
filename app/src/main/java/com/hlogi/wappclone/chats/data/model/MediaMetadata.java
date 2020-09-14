package com.hlogi.wappclone.chats.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.hlogi.wappclone.chats.data.DataMessageNames;

import java.util.Objects;

public class MediaMetadata {

    @ColumnInfo(name = DataMessageNames.COL_MEDIA_SIZE)
    private long size;

    @ColumnInfo(name = DataMessageNames.COL_MEDIA_DURATION)
    private int duration;

    @Ignore
    public MediaMetadata() {
    }

    public MediaMetadata(long size, int duration) {
        this.size = size;
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MediaMetadata{" +
                "size=" + size +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaMetadata)) return false;
        MediaMetadata that = (MediaMetadata) o;
        return getSize() == that.getSize() &&
                getDuration() == that.getDuration();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSize(), getDuration());
    }
}
