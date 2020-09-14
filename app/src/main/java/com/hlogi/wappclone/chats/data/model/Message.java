package com.hlogi.wappclone.chats.data.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.hlogi.wappclone.chats.data.DataMessageNames;

import java.util.Objects;

@Entity(tableName = DataMessageNames.TABLE_NAME)
public class Message {

    @Exclude
    public static final String TYPE_TEXT = "text";

    @Exclude
    public static final String TYPE_MEDIA = "media";

    @Exclude
    public static final String MEDIA_VOICE_NOTE = "voice_note";

    @Exclude
    public static final String MEDIA_PHOTO = "photo";

    @Exclude
    public static final String MEDIA_VIDEO = "video";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DataMessageNames.COL_ID)
    private String message_id;

    @ColumnInfo(name = DataMessageNames.COL_CONVERSATION_ID)
    private String conversation_id;

    @ColumnInfo(name = DataMessageNames.COL_SENDER)
    private String sender;

    @ColumnInfo(name = DataMessageNames.COL_RECEIVER)
    private String receiver;

    @ColumnInfo(name = DataMessageNames.COL_TIME_STAMP)
    private long time_stamp;

    @ColumnInfo(name = DataMessageNames.COL_TYPE)
    private String type;

    @ColumnInfo(name = DataMessageNames.COL_MESSAGE)
    private String message;

    @ColumnInfo(name = DataMessageNames.COL_MEDIA_TYPE)
    private String media_type;

    @Exclude
    @ColumnInfo(name = DataMessageNames.COL_MEDIA_PATH)
    private String media_path;

    @ColumnInfo(name = DataMessageNames.COL_MEDIA_URL)
    private String media_url;

    @ColumnInfo(name = DataMessageNames.COL_CAPTION)
    private String media_caption;

    @Embedded
    private MediaMetadata media_metadata;

    @ColumnInfo(name = DataMessageNames.COL_MEDIA_PLAYED)
    private Boolean media_played;

    @ColumnInfo(name = DataMessageNames.COL_DELIVERED)
    private Boolean delivered;

    @ColumnInfo(name = DataMessageNames.COL_DELIVERED_TIME_STAMP)
    private long delivered_time_stamp;

    @ColumnInfo(name = DataMessageNames.COL_READ)
    private Boolean read;

    @ColumnInfo(name = DataMessageNames.COL_READ_TIME_STAMP)
    private long read_time_stamp;

    @Exclude
    @ColumnInfo(name = DataMessageNames.COL_STARRED)
    private Boolean starred;

    @Exclude
    @Embedded
    private MessageMetadata messageMetadata;

    @Ignore
    public Message() {
        this.starred = false;
    }

    public Message(@NonNull String message_id, String conversation_id, String sender, String receiver,
                   long time_stamp, String type, String message, String media_type, String media_path,
                   String media_url, String media_caption, MediaMetadata media_metadata, Boolean media_played, Boolean delivered,
                   long delivered_time_stamp, Boolean read, long read_time_stamp) {

        this.message_id = message_id;
        this.conversation_id = conversation_id;
        this.sender = sender;
        this.receiver = receiver;
        this.time_stamp = time_stamp;
        this.type = type;
        this.message = message;
        this.media_type = media_type;
        this.media_path = media_path;
        this.media_url = media_url;
        this.media_caption = media_caption;
        this.media_metadata = media_metadata;
        this.media_played = media_played;
        this.delivered = delivered;
        this.delivered_time_stamp = delivered_time_stamp;
        this.read = read;
        this.read_time_stamp = read_time_stamp;
        this.starred = false;
        this.messageMetadata = null;
    }

    @NonNull
    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(@NonNull String message_id) {
        this.message_id = message_id;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    @Exclude
    public String getMedia_path() {
        return media_path;
    }

    @Exclude
    public void setMedia_path(String media_path) {
        this.media_path = media_path;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getMedia_caption() {
        return media_caption;
    }

    public void setMedia_caption(String media_caption) {
        this.media_caption = media_caption;
    }

    public MediaMetadata getMedia_metadata() {
        return media_metadata;
    }

    public void setMedia_metadata(MediaMetadata media_metadata) {
        this.media_metadata = media_metadata;
    }

    public Boolean getMedia_played() {
        return media_played;
    }

    public void setMedia_played(Boolean media_played) {
        this.media_played = media_played;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public long getDelivered_time_stamp() {
        return delivered_time_stamp;
    }

    public void setDelivered_time_stamp(long delivered_time_stamp) {
        this.delivered_time_stamp = delivered_time_stamp;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public long getRead_time_stamp() {
        return read_time_stamp;
    }

    public void setRead_time_stamp(long read_time_stamp) {
        this.read_time_stamp = read_time_stamp;
    }

    @Exclude
    public Boolean getStarred() {
        return starred;
    }

    @Exclude
    public void setStarred(Boolean starred) {
        this.starred = starred;
    }

    @Exclude
    public MessageMetadata getMessageMetadata() {
        return messageMetadata;
    }

    @Exclude
    public void setMessageMetadata(MessageMetadata messageMetadata) {
        this.messageMetadata = messageMetadata;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message_id='" + message_id + '\'' +
                ", conversation_id='" + conversation_id + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", time_stamp=" + time_stamp +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", media_type='" + media_type + '\'' +
                ", media_path='" + media_path + '\'' +
                ", media_url='" + media_url + '\'' +
                ", media_caption='" + media_caption + '\'' +
                ", media_metadata=" + media_metadata +
                ", media_played=" + media_played +
                ", delivered=" + delivered +
                ", delivered_time_stamp=" + delivered_time_stamp +
                ", read=" + read +
                ", read_time_stamp=" + read_time_stamp +
                ", starred=" + starred +
                ", messageMetadata=" + messageMetadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return getTime_stamp() == message1.getTime_stamp() &&
                getDelivered_time_stamp() == message1.getDelivered_time_stamp() &&
                getRead_time_stamp() == message1.getRead_time_stamp() &&
                getMessage_id().equals(message1.getMessage_id()) &&
                Objects.equals(getConversation_id(), message1.getConversation_id()) &&
                Objects.equals(getSender(), message1.getSender()) &&
                Objects.equals(getReceiver(), message1.getReceiver()) &&
                Objects.equals(getType(), message1.getType()) &&
                Objects.equals(getMessage(), message1.getMessage()) &&
                Objects.equals(getMedia_type(), message1.getMedia_type()) &&
                Objects.equals(getMedia_path(), message1.getMedia_path()) &&
                Objects.equals(getMedia_url(), message1.getMedia_url()) &&
                Objects.equals(getMedia_caption(), message1.getMedia_caption()) &&
                Objects.equals(getMedia_metadata(), message1.getMedia_metadata()) &&
                Objects.equals(getMedia_played(), message1.getMedia_played()) &&
                Objects.equals(getDelivered(), message1.getDelivered()) &&
                Objects.equals(getRead(), message1.getRead()) &&
                Objects.equals(getStarred(), message1.getStarred()) &&
                Objects.equals(getMessageMetadata(), message1.getMessageMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage_id(), getConversation_id(), getSender(), getReceiver(), getTime_stamp(), getType(), getMessage(), getMedia_type(), getMedia_path(), getMedia_url(), getMedia_caption(), getMedia_metadata(), getMedia_played(), getDelivered(), getDelivered_time_stamp(), getRead(), getRead_time_stamp(), getStarred(), getMessageMetadata());
    }

    @Ignore
    public static DiffUtil.ItemCallback<Message> DIFF_CALLBACK = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getMessage_id().equals(newItem.getMessage_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.equals(newItem);
        }
    };
}
