package com.hlogi.wappclone.contacts.data.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.hlogi.wappclone.contacts.data.DataContactNames;

import java.util.Objects;

@Entity(tableName = DataContactNames.TABLE_NAME)
public class Contact {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = DataContactNames.COL_NUMBER)
    private String number;

    @ColumnInfo(name = DataContactNames.COL_ID)
    private String id;

    @ColumnInfo(name = DataContactNames.COL_SERVER_ID)
    private String server_id;

    @ColumnInfo(name = DataContactNames.COL_DISPLAY_NAME)
    private String display_name;

    @ColumnInfo(name = DataContactNames.COL_NAME)
    private String name;

    @ColumnInfo(name = DataContactNames.COL_NUMBER_TYPE)
    private String number_type;

    @ColumnInfo(name = DataContactNames.COL_LAST_UPDATED_TIMESTAMP)
    private long last_updated_timestamp;

    @ColumnInfo(name = DataContactNames.COL_PROFILE_PHOTO_URL)
    private String profile_photo_url;

    @ColumnInfo(name = DataContactNames.COL_PROFILE_PHOTO_PATH)
    private String profile_photo_path;

    @ColumnInfo(name = DataContactNames.COL_DEVICE_INSTANCE_ID)
    private String device_instance_id;

    @ColumnInfo(name = DataContactNames.COL_STATUS)
    private String status;

    @ColumnInfo(name = DataContactNames.COL_STATUS_TIMESTAMP)
    private long status_timestamp;

    @Ignore
    public Contact() {
    }

    public Contact(@NonNull String number, String id, String server_id, String display_name,
                   String name, String number_type, long last_updated_timestamp, String profile_photo_url,
                   String profile_photo_path, String device_instance_id, String status, long status_timestamp) {

        this.number = number;
        this.id = id;
        this.server_id = server_id;
        this.display_name = display_name;
        this.name = name;
        this.number_type = number_type;
        this.last_updated_timestamp = last_updated_timestamp;
        this.profile_photo_url = profile_photo_url;
        this.profile_photo_path = profile_photo_path;
        this.device_instance_id = device_instance_id;
        this.status = status;
        this.status_timestamp = status_timestamp;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    public void setNumber(@NonNull String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber_type() {
        return number_type;
    }

    public void setNumber_type(String number_type) {
        this.number_type = number_type;
    }

    public long getLast_updated_timestamp() {
        return last_updated_timestamp;
    }

    public void setLast_updated_timestamp(long last_updated_timestamp) {
        this.last_updated_timestamp = last_updated_timestamp;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getProfile_photo_path() {
        return profile_photo_path;
    }

    public void setProfile_photo_path(String profile_photo_path) {
        this.profile_photo_path = profile_photo_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatus_timestamp() {
        return status_timestamp;
    }

    public void setStatus_timestamp(long status_timestamp) {
        this.status_timestamp = status_timestamp;
    }

    public String getDevice_instance_id() {
        return device_instance_id;
    }

    public void setDevice_instance_id(String device_instance_id) {
        this.device_instance_id = device_instance_id;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "number='" + number + '\'' +
                ", id='" + id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", display_name='" + display_name + '\'' +
                ", name='" + name + '\'' +
                ", number_type='" + number_type + '\'' +
                ", last_updated_timestamp=" + last_updated_timestamp +
                ", profile_photo_url='" + profile_photo_url + '\'' +
                ", profile_photo_path='" + profile_photo_path + '\'' +
                ", device_instance_id='" + device_instance_id + '\'' +
                ", status='" + status + '\'' +
                ", status_timestamp=" + status_timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return getLast_updated_timestamp() == contact.getLast_updated_timestamp() &&
                getStatus_timestamp() == contact.getStatus_timestamp() &&
                getNumber().equals(contact.getNumber()) &&
                Objects.equals(getId(), contact.getId()) &&
                Objects.equals(getServer_id(), contact.getServer_id()) &&
                Objects.equals(getDisplay_name(), contact.getDisplay_name()) &&
                Objects.equals(getName(), contact.getName()) &&
                Objects.equals(getNumber_type(), contact.getNumber_type()) &&
                Objects.equals(getProfile_photo_url(), contact.getProfile_photo_url()) &&
                Objects.equals(getProfile_photo_path(), contact.getProfile_photo_path()) &&
                Objects.equals(device_instance_id, contact.device_instance_id) &&
                Objects.equals(getStatus(), contact.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getId(), getServer_id(), getDisplay_name(), getName(), getNumber_type(), getLast_updated_timestamp(), getProfile_photo_url(), getProfile_photo_path(), device_instance_id, getStatus(), getStatus_timestamp());
    }

    public static DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.getNumber().equals(newItem.getNumber());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.equals(newItem);
        }
    };

}
