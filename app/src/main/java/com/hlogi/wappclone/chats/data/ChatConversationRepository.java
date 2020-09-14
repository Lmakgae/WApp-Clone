package com.hlogi.wappclone.chats.data;

import android.content.Context;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.chats.data.model.ChatConversation;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.data.WAppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ChatConversationRepository {

    private final ChatConversationDao mChat_Convo_Dao;
    private final MessagesDao mMessages_Dao;
    private final ExecutorService mIoExecutor;
    private static volatile ChatConversationRepository sInstance = null;

    public static ChatConversationRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ChatConversationRepository.class) {
                if (sInstance == null) {
                    WAppDatabase database = WAppDatabase.getInstance(context);
                    sInstance = new ChatConversationRepository(database.chatConversationDao(),
                            database.messagesDao(),
                            WAppDatabase.databaseRWExecutor);
                }
            }
        }
        return sInstance;
    }

    private ChatConversationRepository(ChatConversationDao chat_convo_id, MessagesDao messages_dao, ExecutorService executor) {
        mIoExecutor = executor;
        mChat_Convo_Dao = chat_convo_id;
        mMessages_Dao = messages_dao;
    }

    @WorkerThread
    public LiveData<PagedList<ChatConversation>> getAllChatConvo() {
        return new LivePagedListBuilder<>(mChat_Convo_Dao.getAllConversations(), 15).build();
    }

    public LiveData<ChatConversation> getChatConversation(String id){
        return mChat_Convo_Dao.getConversation(id);
    }

    public void insertChatConversation(ChatConversation conversation) {
        mIoExecutor.execute(() -> mChat_Convo_Dao.insert(conversation));
    }

    public void insertChatConversations(List<ChatConversation> conversations) {
        mIoExecutor.execute(() -> mChat_Convo_Dao.insert(conversations));
    }

    public LiveData<List<Message>> getNumberOfUnread(String chat_convo_id) {
        return mMessages_Dao.getUnreadMessagesFromChat(chat_convo_id,
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                false);
    }

    public LiveData<Message> getChatLastMessage(String chat_convo_id) {
        return mMessages_Dao.getLastMessageFromChatConvo(chat_convo_id);
    }

    public void deleteChatConversation(ChatConversation conversation) {
        mIoExecutor.execute(() -> mChat_Convo_Dao.delete(conversation));
    }

    public void deleteAllChatConversations() {
        mIoExecutor.execute(mChat_Convo_Dao::deleteAllChat_Conversations);
    }

}
