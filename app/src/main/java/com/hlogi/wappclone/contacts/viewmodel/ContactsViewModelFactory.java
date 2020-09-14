package com.hlogi.wappclone.contacts.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hlogi.wappclone.chats.data.OnlineStatusRepository;
import com.hlogi.wappclone.contacts.data.ContactsRepository;

import java.lang.reflect.InvocationTargetException;

public class ContactsViewModelFactory implements ViewModelProvider.Factory {

    private final ContactsRepository mRepository;
    private final OnlineStatusRepository mOnlineStatusRepository;

    public static ContactsViewModelFactory createFactory(Activity activity) {
        Context context = activity.getApplicationContext();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new ContactsViewModelFactory(ContactsRepository.getInstance(context), OnlineStatusRepository.getInstance(databaseReference));
    }

    private ContactsViewModelFactory(ContactsRepository repository, OnlineStatusRepository onlineStatusRepository) {
        this.mRepository = repository;
        this.mOnlineStatusRepository = onlineStatusRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(ContactsRepository.class, OnlineStatusRepository.class)
                    .newInstance(mRepository, mOnlineStatusRepository);
        } catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
