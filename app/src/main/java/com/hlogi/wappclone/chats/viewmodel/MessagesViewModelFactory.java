package com.hlogi.wappclone.chats.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hlogi.wappclone.chats.data.MessagesRepository;
import com.hlogi.wappclone.contacts.data.ContactsRepository;

import java.lang.reflect.InvocationTargetException;

public class MessagesViewModelFactory implements ViewModelProvider.Factory {

    private final MessagesRepository messagesRepository;
    private final ContactsRepository contactsRepository;

    public static MessagesViewModelFactory createFactory(@NonNull Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new MessagesViewModelFactory(MessagesRepository.getInstance(context),
                                            ContactsRepository.getInstance(context));
    }

    private MessagesViewModelFactory(MessagesRepository messagesRepository, ContactsRepository contactsRepository) {
        this.messagesRepository = messagesRepository;
        this.contactsRepository = contactsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(MessagesRepository.class, ContactsRepository.class)
                    .newInstance(messagesRepository, contactsRepository);
        } catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
