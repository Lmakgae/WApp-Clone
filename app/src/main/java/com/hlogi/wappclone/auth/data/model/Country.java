package com.hlogi.wappclone.auth.data.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class Country {

    private String name;
    private String code;

    public Country() {
    }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;
        Country country = (Country) o;
        return Objects.equals(getName(), country.getName()) &&
                Objects.equals(getCode(), country.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCode());
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public static DiffUtil.ItemCallback<Country> DIFF_CALLBACK = new DiffUtil.ItemCallback<Country>() {
        @Override
        public boolean areItemsTheSame(@NonNull Country oldItem, @NonNull Country newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Country oldItem, @NonNull Country newItem) {
            return oldItem.equals(newItem);
        }
    };

}
