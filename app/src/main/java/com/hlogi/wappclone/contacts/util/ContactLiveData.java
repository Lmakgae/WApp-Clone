package com.hlogi.wappclone.contacts.util;

import androidx.lifecycle.LiveData;

import com.hlogi.wappclone.contacts.data.model.Contact;

public class ContactLiveData extends LiveData<Contact> {

    public ContactLiveData(Contact value) {
        super(value);

        if (value == null) {



        }

    }

}
