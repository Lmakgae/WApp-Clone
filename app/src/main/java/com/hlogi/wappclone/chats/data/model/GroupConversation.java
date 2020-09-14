package com.hlogi.wappclone.chats.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.hlogi.wappclone.chats.data.DataChatConversationNames;
import com.hlogi.wappclone.chats.data.DataGroupConvoNames;

import java.util.List;
import java.util.Objects;

public class GroupConversation {

    @ColumnInfo(name = DataGroupConvoNames.COL_ID)
    private String id;

    @ColumnInfo(name = DataGroupConvoNames.COL_CREATION_USER)
    private String creation_user;

    @ColumnInfo(name = DataGroupConvoNames.COL_CREATION_TIMESTAMP)
    private long creation_timestamp;

    @ColumnInfo(name = DataGroupConvoNames.COL_GROUP_SUBJECT)
    private String group_subject;

    @ColumnInfo(name = DataGroupConvoNames.COL_DESCRIPTION)
    private String description;

    @ColumnInfo(name = DataGroupConvoNames.COL_ADMINS)
    private List<String> admins;

    @ColumnInfo(name = DataGroupConvoNames.COL_GROUP_PHOTO_URL)
    private String group_photo_url;

    @Ignore
    public GroupConversation() {
    }

    public GroupConversation(String id, String creation_user, long creation_timestamp, String group_subject, String description, List<String> admins, String group_photo_url) {
        this.id = id;
        this.creation_user = creation_user;
        this.creation_timestamp = creation_timestamp;
        this.group_subject = group_subject;
        this.description = description;
        this.admins = admins;
        this.group_photo_url = group_photo_url;
    }

    public String getCreation_user() {
        return creation_user;
    }

    public void setCreation_user(String creation_user) {
        this.creation_user = creation_user;
    }

    public long getCreation_timestamp() {
        return creation_timestamp;
    }

    public void setCreation_timestamp(long creation_timestamp) {
        this.creation_timestamp = creation_timestamp;
    }

    public String getGroup_subject() {
        return group_subject;
    }

    public void setGroup_subject(String group_subject) {
        this.group_subject = group_subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public String getGroup_photo_url() {
        return group_photo_url;
    }

    public void setGroup_photo_url(String group_photo_url) {
        this.group_photo_url = group_photo_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GroupConversation{" +
                "id='" + id + '\'' +
                ", creation_user='" + creation_user + '\'' +
                ", creation_timestamp=" + creation_timestamp +
                ", group_subject='" + group_subject + '\'' +
                ", description='" + description + '\'' +
                ", admins=" + admins +
                ", group_photo_url='" + group_photo_url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupConversation)) return false;
        GroupConversation that = (GroupConversation) o;
        return getCreation_timestamp() == that.getCreation_timestamp() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getCreation_user(), that.getCreation_user()) &&
                Objects.equals(getGroup_subject(), that.getGroup_subject()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getAdmins(), that.getAdmins()) &&
                Objects.equals(getGroup_photo_url(), that.getGroup_photo_url());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreation_user(), getCreation_timestamp(), getGroup_subject(), getDescription(), getAdmins(), getGroup_photo_url());
    }
}
