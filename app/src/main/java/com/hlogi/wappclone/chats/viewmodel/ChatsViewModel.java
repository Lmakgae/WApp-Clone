package com.hlogi.wappclone.chats.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.hlogi.wappclone.chats.data.ChatConversationRepository;
import com.hlogi.wappclone.chats.data.OnlineStatusRepository;
import com.hlogi.wappclone.chats.data.model.ChatConversation;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.data.model.OnlineStatus;
import com.hlogi.wappclone.contacts.data.ContactsRepository;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.firebase.DatabaseDocumentLiveData;

import java.util.List;

public class ChatsViewModel extends ViewModel {

    private final ChatConversationRepository mChatConversationRepository;
    private final ContactsRepository mContactsRepository;
    private final OnlineStatusRepository mOnlineStatusRepository;
    private final LiveData<PagedList<ChatConversation>> mChatConvoList;

    public ChatsViewModel(ChatConversationRepository conversationRepository,
                          ContactsRepository contactsRepository,
                          OnlineStatusRepository onlineStatusRepository) {
        this.mContactsRepository = contactsRepository;
        this.mChatConversationRepository = conversationRepository;
        this.mOnlineStatusRepository = onlineStatusRepository;
        mChatConvoList = getChatConversationRepository().getAllChatConvo();
    }

    private ChatConversationRepository getChatConversationRepository() {
        return mChatConversationRepository;
    }

    public LiveData<PagedList<ChatConversation>> getChatConvoList() {
        return mChatConvoList;
    }

    public LiveData<Contact> getContact(String number) {
        return mContactsRepository.getContact(number);
    }

    public DatabaseDocumentLiveData<OnlineStatus> getContactOnlineStatus(String number) {
        return mOnlineStatusRepository.getContactStatus(number);
    }

    public LiveData<List<Message>> getUnreadMessages(String chat_convo_id) {
        return mChatConversationRepository.getNumberOfUnread(chat_convo_id);
    }

    public LiveData<Message> getChatLastMessage(String chat_convo_id) {
        return mChatConversationRepository.getChatLastMessage(chat_convo_id);
    }

}
