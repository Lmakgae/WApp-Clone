package com.hlogi.wappclone.firebase;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hlogi.wappclone.auth.data.DataUserNames;
import com.hlogi.wappclone.chats.data.MessagesRepository;
import com.hlogi.wappclone.chats.work.MessageWork;
import com.hlogi.wappclone.chats.work.WorkConstants;

import java.util.HashMap;
import java.util.Map;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseCMService";
    private static final String PAYLOAD_ACTION = "action";
    private static final String PAYLOAD_ACTION_NEW_MESSAGE_RECEIVED = "NEW_MESSAGE_RECEIVED";
    private static final String PAYLOAD_ACTION_NEW_MESSAGE_SENT = "NEW_MESSAGE_SENT";
    private static final String PAYLOAD_ACTION_NEW_MESSAGE_DELIVERED = "NEW_MESSAGE_DELIVERED";
    private static final String PAYLOAD_ACTION_NEW_MESSAGE_READ = "NEW_MESSAGE_READ";
    private static final String PAYLOAD_MESSAGE_ID = "message_id";
    private static final String PAYLOAD_SENDER = "sender";
    private static final String PAYLOAD_RECEIVER = "receiver";
    private static final String PAYLOAD_CONVERSATION_ID = "conversation_id";
    private static final String PAYLOAD_DELIVERY_TIMESTAMP = "delivery_timestamp";
    private static final String PAYLOAD_READ_TIMESTAMP = "read_timestamp";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        switch (remoteMessage.getData().get(PAYLOAD_ACTION)) {
            case PAYLOAD_ACTION_NEW_MESSAGE_RECEIVED:
                receiveNewMessage(remoteMessage.getData());
                break;
            case PAYLOAD_ACTION_NEW_MESSAGE_SENT:
                newMessageSent(remoteMessage.getData());
                break;
            case PAYLOAD_ACTION_NEW_MESSAGE_DELIVERED:
                messageDelivered(remoteMessage.getData());
                break;
            case PAYLOAD_ACTION_NEW_MESSAGE_READ:
                messageRead(remoteMessage.getData());
                break;
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sendTokenToServer(s);
    }

    private void receiveNewMessage(@NonNull Map<String, String> data) {
        Data messageData = new Data.Builder()
                .putString(WorkConstants.ARG_KEY_MESSAGE_ID, data.get(PAYLOAD_MESSAGE_ID))
                .putString(WorkConstants.ARG_KEY_SENDER, data.get(PAYLOAD_SENDER))
                .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, data.get(PAYLOAD_CONVERSATION_ID))
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkRequest receiveNewMessageWork =
                new OneTimeWorkRequest.Builder(MessageWork.class)
                .setConstraints(constraints)
                .setInputData(messageData)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(receiveNewMessageWork);
    }

    private void newMessageSent(@NonNull Map<String, String> data) {
        MessagesRepository repository = MessagesRepository.getInstance(getApplicationContext());
        repository.messageSentUpdate(data.get(PAYLOAD_MESSAGE_ID), data.get(PAYLOAD_CONVERSATION_ID));
    }

    private void messageDelivered(@NonNull Map<String, String> data){
        MessagesRepository repository = MessagesRepository.getInstance(getApplicationContext());
        repository.messageDeliveredUpdate(
                data.get(PAYLOAD_MESSAGE_ID),
                data.get(PAYLOAD_CONVERSATION_ID),
                Long.parseLong(data.get(PAYLOAD_DELIVERY_TIMESTAMP))
        );
    }

    private void messageRead(@NonNull Map<String, String> data) {
        MessagesRepository repository = MessagesRepository.getInstance(getApplicationContext());
        repository.messageReadUpdate(
                data.get(PAYLOAD_MESSAGE_ID),
                data.get(PAYLOAD_CONVERSATION_ID),
                Long.parseLong(data.get(PAYLOAD_READ_TIMESTAMP))
        );
    }

    private void sendTokenToServer(String token){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DocumentReference reference = FirebaseFirestore.getInstance()
                    .collection(FirebaseDatabasePaths.COLLECTION_USERS)
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, String> data = new HashMap<>();
            data.put(DataUserNames.COL_DEVICE_INSTANCE_ID, token);
            reference.set(data, SetOptions.merge());
        }
    }
}
