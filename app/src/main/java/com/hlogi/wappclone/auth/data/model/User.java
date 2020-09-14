package com.hlogi.wappclone.auth.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.hlogi.wappclone.auth.data.DataUserNames;

import java.util.Objects;

@Entity(tableName = DataUserNames.TABLE_NAME)
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DataUserNames.COL_USER_KEY)
    @Exclude
    private int key;

    @NonNull
    @ColumnInfo(name = DataUserNames.COL_UID)
    private String uid;

    @ColumnInfo(name = DataUserNames.COL_CODE)
    private String country_code;

    @ColumnInfo(name = DataUserNames.COL_COUNTRY)
    private String country_name;

    @ColumnInfo(name = DataUserNames.COL_NUMBER)
    private String phone_number;

    @ColumnInfo(name = DataUserNames.COL_NAME)
    private String name;

    @ColumnInfo(name = DataUserNames.COL_PROFILE_PHOTO_PATH)
    private String profile_photo_path;

    @ColumnInfo(name = DataUserNames.COL_PROFILE_PHOTO_URL)
    private String profile_photo_url;

    @ColumnInfo(name = DataUserNames.COL_DEVICE_INSTANCE_ID)
    private String device_instance_id;

    @ColumnInfo(name = DataUserNames.COL_STATUS)
    private String status;

    @ColumnInfo(name = DataUserNames.COL_STATUS_TIMESTAMP)
    private long status_timestamp;

    @Ignore
    public User() {
    }

    public User(@NonNull String uid, String country_code, String country_name, String phone_number, String name, String profile_photo_path, String profile_photo_url, String device_instance_id, String status, long status_timestamp) {
        this.key = 1;
        this.uid = uid;
        this.country_code = country_code;
        this.country_name = country_name;
        this.phone_number = phone_number;
        this.name = name;
        this.profile_photo_path = profile_photo_path;
        this.profile_photo_url = profile_photo_url;
        this.device_instance_id = device_instance_id;
        this.status = status;
        this.status_timestamp = status_timestamp;
    }


    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_photo_path() {
        return profile_photo_path;
    }

    public void setProfile_photo_path(String profile_photo_path) {
        this.profile_photo_path = profile_photo_path;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getDevice_instance_id() {
        return device_instance_id;
    }

    public void setDevice_instance_id(String device_instance_id) {
        this.device_instance_id = device_instance_id;
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

    @Exclude
    public int getKey() {
        return key;
    }

    @Exclude
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", country_code='" + country_code + '\'' +
                ", country_name='" + country_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", name='" + name + '\'' +
                ", profile_photo_path='" + profile_photo_path + '\'' +
                ", profile_photo_url='" + profile_photo_url + '\'' +
                ", device_instance_id=" + device_instance_id +
                ", status='" + status + '\'' +
                ", status_timestamp=" + status_timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getKey() == user.getKey() &&
                getStatus_timestamp() == user.getStatus_timestamp() &&
                getUid().equals(user.getUid()) &&
                Objects.equals(getCountry_code(), user.getCountry_code()) &&
                Objects.equals(getCountry_name(), user.getCountry_name()) &&
                Objects.equals(getPhone_number(), user.getPhone_number()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getProfile_photo_path(), user.getProfile_photo_path()) &&
                Objects.equals(getProfile_photo_url(), user.getProfile_photo_url()) &&
                Objects.equals(getDevice_instance_id(), user.getDevice_instance_id()) &&
                Objects.equals(getStatus(), user.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getUid(), getCountry_code(), getCountry_name(), getPhone_number(), getName(), getProfile_photo_path(), getProfile_photo_url(), getDevice_instance_id(), getStatus(), getStatus_timestamp());
    }
}
