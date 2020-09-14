package com.hlogi.wappclone.chats.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.hlogi.wappclone.chats.data.model.ChatConversation;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.data.model.MessageMetadata;
import com.hlogi.wappclone.chats.data.model.UploadMediaSession;
import com.hlogi.wappclone.chats.work.MediaMessageWork;
import com.hlogi.wappclone.chats.work.MessageReceiptWork;
import com.hlogi.wappclone.chats.work.WorkConstants;
import com.hlogi.wappclone.data.WAppDatabase;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MessagesRepository {

    private final MessagesDao mMessages_Dao;
    private final ChatConversationDao mChats_Dao;
    private final UploadMediaSessionDao mUploadMediaSessionDao;
    private final ExecutorService mIoExecutor;
    private final CollectionReference mChatsReference;
    private static volatile MessagesRepository sInstance = null;

    public static MessagesRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MessagesRepository.class) {
                if (sInstance == null) {
                    WAppDatabase database = WAppDatabase.getInstance(context);
                    CollectionReference reference = FirebaseFirestore.getInstance()
                            .collection(FirebaseDatabasePaths.COLLECTION_CHATS);
                    sInstance = new MessagesRepository(database.messagesDao(),
                            database.chatConversationDao(),
                            database.uploadMediaSessionDao(),
                            WAppDatabase.databaseRWExecutor,
                            reference);
                }
            }
        }
        return sInstance;
    }

    private MessagesRepository(MessagesDao messages_dao, ChatConversationDao chats_dao,
                               UploadMediaSessionDao uploadMediaSessionDao, ExecutorService executor,
                               CollectionReference reference) {
        mIoExecutor = executor;
        mMessages_Dao = messages_dao;
        mChats_Dao = chats_dao;
        mUploadMediaSessionDao = uploadMediaSessionDao;
        mChatsReference = reference;
    }

    @WorkerThread
    public LiveData<PagedList<Message>> getAllMessagesFromChatConvo(String chat_convo) {
        return new LivePagedListBuilder<>(mMessages_Dao.getAllMessagesFromChatConvo(chat_convo), 25).build();
    }

    public LiveData<List<Message>> getUnreadMessages(String chat_convo_id) {
        return mMessages_Dao.getUnreadMessagesFromChat(chat_convo_id,
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                false);
    }

    public LiveData<Message> getMessageLiveData(String chat_convo_id, String id){
        return mMessages_Dao.getMessageLiveData(chat_convo_id, id);
    }

    public Message getMessage(String chat_convo_id, String id){
        return mMessages_Dao.getMessage(chat_convo_id, id);
    }

    @WorkerThread
    public LiveData<PagedList<Message>> getAllStarredMessages() {
        return new LivePagedListBuilder<>(mMessages_Dao.getAllStarredMessages(true), 25).build();
    }

    @WorkerThread
    public LiveData<PagedList<Message>> getStarredMessagesFromChatConvo(String chat_convo) {
        return new LivePagedListBuilder<>(mMessages_Dao.getAllStarredMessagesFromChat(chat_convo, true), 25).build();
    }

    public void insertMessage(Message message) {
        mIoExecutor.execute(() -> {
            mMessages_Dao.insert(message);
            ChatConversation conversation = new ChatConversation(
                                                message.getConversation_id(),
                                                FirebaseDatabasePaths.CONVERSATION_INDIVIDUAL_TYPE,
                                                Arrays.asList(message.getSender(), message.getReceiver()),
                                                Timestamp.now().toDate().getTime(),
                                                null);
            mChats_Dao.insert(conversation);
        });
    }

    public void insertMessages(List<Message> messages) {
        mIoExecutor.execute(() -> {
            mMessages_Dao.insert(messages);
        });
    }

    public void messageSentUpdate(String message_id, String conversation_id) {
        mIoExecutor.execute(() -> mMessages_Dao.messageSentUpdate(message_id,
                conversation_id, false, false));
    }

    public void messageDeliveredUpdate(String message_id, String conversation_id, Long delivery_timestamp) {
        mIoExecutor.execute(() -> {
            mMessages_Dao.messageDeliveredUpdate(message_id, conversation_id, true, delivery_timestamp);
        });
    }

    public void messageReadUpdate(String message_id, String conversation_id, Long read_timestamp) {
        mIoExecutor.execute(() -> {
            mMessages_Dao.messageReadUpdate(message_id, conversation_id, true, read_timestamp);
        });
    }

    public void deleteMessage(Message message) {
        mIoExecutor.execute(() -> {
            mMessages_Dao.delete(message);
        });
    }

    public void deleteAllMessages() {
        mIoExecutor.execute(mMessages_Dao::deleteAllMessage);
    }

    @NonNull
    public Task<Void> sendMessage(@NonNull final Message message){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DocumentReference newMessageRef = mChatsReference
                                        .document(message.getConversation_id())
                                        .collection(FirebaseDatabasePaths.COLLECTION_MESSAGES)
                                        .document();

        message.setMessage_id(newMessageRef.getId());
        message.setMessageMetadata(new MessageMetadata(true, true));

        ChatConversation conversation = new ChatConversation(message.getConversation_id(),
                FirebaseDatabasePaths.CONVERSATION_INDIVIDUAL_TYPE,
                Arrays.asList(message.getSender(), message.getReceiver()),
                Timestamp.now().toDate().getTime(),
                null);

        insertMessage(message);

        batch.set(newMessageRef, message)
                .set(mChatsReference.document(message.getConversation_id()), conversation, SetOptions.merge());

        return batch.commit();
    }

    public String sendMediaMessage(@NonNull final Message message) {
        DocumentReference newMediaMessageRef = mChatsReference
                                            .document(message.getConversation_id())
                                            .collection(FirebaseDatabasePaths.COLLECTION_MESSAGES)
                                            .document();
        message.setMessage_id(newMediaMessageRef.getId());
        message.setMessageMetadata(new MessageMetadata(true, true));

        insertMessage(message);

        return message.getMessage_id();
    }

    public void insertUploadMediaSession(String message_id, String chat_convo_id, Uri uri){
        mIoExecutor.execute(() -> {
            mUploadMediaSessionDao.insert(new UploadMediaSession(message_id, chat_convo_id, uri.toString()));
        });
    }

    public UploadMediaSession getUploadMediaSession(String chat_convo_id, String message_id) {
        return mUploadMediaSessionDao.getUploadMediaSession(chat_convo_id, message_id);
    }


    public void deleteUploadMediaSession(UploadMediaSession uploadMediaSession) {
        mIoExecutor.execute(() -> {
            mUploadMediaSessionDao.delete(uploadMediaSession);
        });
    }

    public void reuploadMediaWork(@NonNull Message message, Context context) {
        mIoExecutor.execute(() -> {

            Data mediaMessageData;

            UploadMediaSession uploadMediaSession = getUploadMediaSession(
                    message.getConversation_id(), message.getMessage_id());

            if (uploadMediaSession != null){
                mediaMessageData = new Data.Builder()
                        .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_SEND_MEDIA)
                        .putString(WorkConstants.ARG_KEY_FILE_NAME, message.getMedia_url())
                        .putString(WorkConstants.ARG_KEY_MESSAGE_ID, message.getMessage_id())
                        .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, message.getConversation_id())
                        .putString(WorkConstants.ARG_KEY_MEDIA_TYPE, message.getMedia_type())
                        .putString(WorkConstants.ARG_KEY_UPLOAD_URI, uploadMediaSession.getUri_string())
                        .build();
            } else {
                mediaMessageData = new Data.Builder()
                        .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_SEND_MEDIA)
                        .putString(WorkConstants.ARG_KEY_FILE_NAME, message.getMedia_url())
                        .putString(WorkConstants.ARG_KEY_MESSAGE_ID, message.getMessage_id())
                        .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, message.getConversation_id())
                        .putString(WorkConstants.ARG_KEY_MEDIA_TYPE, message.getMedia_type())
                        .build();
            }

            String uniqueWorkName = message.getConversation_id() + message.getMessage_id();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest mediaMessageWork =
                    new OneTimeWorkRequest.Builder(MediaMessageWork.class)
                            .setConstraints(constraints)
                            .setInputData(mediaMessageData)
                            .build();

            WorkManager.getInstance(context).enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.KEEP, mediaMessageWork);

        });

    }

}
