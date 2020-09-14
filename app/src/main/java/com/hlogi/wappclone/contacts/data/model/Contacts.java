package com.hlogi.wappclone.contacts.data.model;

import java.util.ArrayList;

public class Contacts {

    private String id;
    private String display_name;
    private ArrayList<String> numbers;
    private ArrayList<String> numbersType;
    private ArrayList<Integer> numbersTypeInts;
    private long last_updated_timestamp;

    public Contacts(String id, String display_name, ArrayList<String> numbers, ArrayList<String> numbersType, ArrayList<Integer> numbersTypeInts, long last_updated_timestamp) {
        this.id = id;
        this.display_name = display_name;
        this.numbers = numbers;
        this.numbersType = numbersType;
        this.numbersTypeInts = numbersTypeInts;
        this.last_updated_timestamp = last_updated_timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<String> numbers) {
        this.numbers = numbers;
    }

    public ArrayList<String> getNumbersType() {
        return numbersType;
    }

    public void setNumbersType(ArrayList<String> numbersType) {
        this.numbersType = numbersType;
    }

    public long getLast_updated_timestamp() {
        return last_updated_timestamp;
    }

    public void setLast_updated_timestamp(long last_updated_timestamp) {
        this.last_updated_timestamp = last_updated_timestamp;
    }

    public ArrayList<Integer> getNumbersTypeInts() {
        return numbersTypeInts;
    }

    public void setNumbersTypeInts(ArrayList<Integer> numbersTypeInts) {
        this.numbersTypeInts = numbersTypeInts;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "id='" + id + '\'' +
                ", display_name='" + display_name + '\'' +
                ", numbers=" + numbers +
                ", numbersType=" + numbersType +
                ", numbersTypeInts=" + numbersTypeInts +
                ", last_updated_timestamp=" + last_updated_timestamp +
                '}';
    }
}
