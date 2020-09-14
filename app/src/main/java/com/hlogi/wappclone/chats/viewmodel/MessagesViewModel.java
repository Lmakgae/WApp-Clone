package com.hlogi.wappclone.chats.viewmodel;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.firebase.Timestamp;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.MessagesRepository;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.ui.screen.MessagesFragment;
import com.hlogi.wappclone.chats.work.MediaMessageWork;
import com.hlogi.wappclone.chats.work.MessageReceiptWork;
import com.hlogi.wappclone.chats.work.WorkConstants;
import com.hlogi.wappclone.contacts.data.ContactsRepository;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.starred.StarredMessagesFragment;

import java.util.List;

public class MessagesViewModel extends ViewModel {

    private final MessagesRepository mMessagesRepository;
    private ContactsRepository mContactsRepository;
    private final MutableLiveData<String> mCurrentChatConvoID = new MutableLiveData<>("");
    private final LiveData<PagedList<Message>> mMessagesList;
    private final LiveData<List<Message>> mUnreadMessagesList;

    public MessagesViewModel(MessagesRepository messagesRepository, ContactsRepository contactsRepository) {
        this.mMessagesRepository = messagesRepository;
        this.mContactsRepository = contactsRepository;
        this.mMessagesList = Transformations.switchMap(mCurrentChatConvoID, (chat_convo_id) ->
                getMessagesRepository().getAllMessagesFromChatConvo(chat_convo_id));
        this.mUnreadMessagesList = Transformations.switchMap(mCurrentChatConvoID, (chat_convo_id) ->
                getMessagesRepository().getUnreadMessages(chat_convo_id));
    }

    private MessagesRepository getMessagesRepository() {
        return mMessagesRepository;
    }

    public LiveData<PagedList<Message>> getMessagesList() {
        return mMessagesList;
    }

    public void setCurrentChatConvoID(String chat_convo_id) {
        if(mCurrentChatConvoID.getValue() != null) {
            if (!mCurrentChatConvoID.getValue().equals(chat_convo_id))
                mCurrentChatConvoID.setValue(chat_convo_id);
        }
    }

    public MutableLiveData<String> getCurrentChatConvoID() {
        return mCurrentChatConvoID;
    }

    public LiveData<List<Message>> getUnreadMessagesList() {
        return mUnreadMessagesList;
    }

    public LiveData<PagedList<Message>> getStarredMessagesFromChat(@NonNull String chat_convo) {
        if (chat_convo.equals(StarredMessagesFragment.ALL_STARRED_MESSAGES)) {
            return mMessagesRepository.getAllStarredMessages();
        } else {
            return mMessagesRepository.getStarredMessagesFromChatConvo(chat_convo);
        }
    }

    public LiveData<Contact> getContact(String number) {
        return mContactsRepository.getContact(number);
    }

    public Message getMessage(String chat_convo, String message_id) {
        return mMessagesRepository.getMessage(chat_convo, message_id);
    }

    public LiveData<Message> getMessageLiveData(String chat_convo, String message_id) {
        return mMessagesRepository.getMessageLiveData(chat_convo, message_id);
    }

    public void sendMessage(@NonNull Message message, Context context) {
         mMessagesRepository.sendMessage(message).addOnSuccessListener(aVoid -> {
             mMessagesRepository.messageSentUpdate(message.getMessage_id(),
                     message.getConversation_id());

             if (MessagesFragment.fragment_running && MessagesFragment.selected_number.equals(message.getReceiver())) {

                 if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("conversation_tones_key", true)) {
                     MediaPlayer mp = MediaPlayer.create(context, R.raw.message_sent_sound);
                     mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                         @Override
                         public void onCompletion(MediaPlayer mp) {
                             mp.reset();
                             mp.release();
                             mp = null;
                         }
                     });
                     mp.start();
                 }


             }
         });
    }

    public void sendMediaMessage(@NonNull Message message, Context context) {
        String message_id = mMessagesRepository.sendMediaMessage(message);
        Data mediaMessageData = new Data.Builder()
                .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_SEND_MEDIA)
                .putString(WorkConstants.ARG_KEY_FILE_NAME, message.getMedia_url())
                .putString(WorkConstants.ARG_KEY_MESSAGE_ID, message_id)
                .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, message.getConversation_id())
                .putString(WorkConstants.ARG_KEY_MEDIA_TYPE, message.getMedia_type())
                .build();

        String uniqueWorkName = message.getConversation_id() + message_id;

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest mediaMessageWork =
                new OneTimeWorkRequest.Builder(MediaMessageWork.class)
                        .setConstraints(constraints)
                        .setInputData(mediaMessageData)
                        .build();

        WorkManager.getInstance(context).enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.KEEP, mediaMessageWork);
    }

    public void sendMessageReceipts(@NonNull List<Message> messages, Context context) {
        for (Message message : messages) {

            message.setRead(true);
            message.setRead_time_stamp(Timestamp.now().toDate().getTime());

            Data messageReceiptData = new Data.Builder()
                    .putString(WorkConstants.ARG_KEY_MESSAGE_ID, message.getMessage_id())
                    .putString(WorkConstants.ARG_KEY_SENDER, message.getSender())
                    .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, message.getConversation_id())
                    .putLong(WorkConstants.ARG_KEY_TIMESTAMP, message.getRead_time_stamp())
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            WorkRequest sendMessageReceiptWork =
                    new OneTimeWorkRequest.Builder(MessageReceiptWork.class)
                            .setConstraints(constraints)
                            .setInputData(messageReceiptData)
                            .build();

            WorkManager.getInstance(context).enqueue(sendMessageReceiptWork);
        }

        mMessagesRepository.insertMessages(messages);
    }

    public void deleteAll() {
        mMessagesRepository.deleteAllMessages();
    }

    public void cancelMediaWork(@NonNull Message message, Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(message.getConversation_id() +
                message.getMessage_id());
    }

    public void reuploadMediaWork(@NonNull Message message, Context context) {
        mMessagesRepository.reuploadMediaWork(message, context);
    }

    public void redownloadMediaWork(@NonNull Message message, Context context) {
        Data mediaMessageData = new Data.Builder()
                .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_RECEIVE_MEDIA)
                .putString(WorkConstants.ARG_KEY_FILE_NAME, message.getMedia_url())
                .putString(WorkConstants.ARG_KEY_MESSAGE_ID, message.getMessage_id())
                .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, message.getConversation_id())
                .putString(WorkConstants.ARG_KEY_MEDIA_TYPE, message.getMedia_type())
                .build();

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
    }

}
