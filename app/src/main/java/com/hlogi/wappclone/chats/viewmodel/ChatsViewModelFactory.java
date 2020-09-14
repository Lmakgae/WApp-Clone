package com.hlogi.wappclone.chats.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hlogi.wappclone.chats.data.ChatConversationRepository;
import com.hlogi.wappclone.chats.data.OnlineStatusRepository;
import com.hlogi.wappclone.contacts.data.ContactsRepository;

import java.lang.reflect.InvocationTargetException;

public class ChatsViewModelFactory implements ViewModelProvider.Factory {

    private final ChatConversationRepository conversationRepository;
    private final ContactsRepository contactsRepository;
    private final OnlineStatusRepository mOnlineStatusRepository;

    public static ChatsViewModelFactory createFactory(@NonNull Activity activity) {
        Context context = activity.getApplicationContext();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new ChatsViewModelFactory(ChatConversationRepository.getInstance(context), ContactsRepository.getInstance(context), OnlineStatusRepository.getInstance(databaseReference));
    }

    private ChatsViewModelFactory(ChatConversationRepository conversationRepository, ContactsRepository contactsRepository, OnlineStatusRepository onlineStatusRepository) {
        this.contactsRepository = contactsRepository;
        this.conversationRepository = conversationRepository;
        this.mOnlineStatusRepository = onlineStatusRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(ChatConversationRepository.class, ContactsRepository.class, OnlineStatusRepository.class)
                    .newInstance(conversationRepository, contactsRepository, mOnlineStatusRepository);
        } catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
