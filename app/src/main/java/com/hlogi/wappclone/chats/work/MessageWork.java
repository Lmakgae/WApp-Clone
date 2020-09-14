package com.hlogi.wappclone.chats.work;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.MessagesRepository;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.ui.screen.MessagesFragment;
import com.hlogi.wappclone.contacts.data.ContactsRepository;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;
import com.hlogi.wappclone.ui.main.MainActivity;

import java.util.concurrent.ExecutionException;

public class MessageWork extends Worker {

    private static final String MESSAGE_CHANNEL_ID = "notify-new-message";
    private static final int MESSAGE_NOTIFICATION_ID = 1;
    private final MessagesRepository messagesRepository;
    private final ContactsRepository contactsRepository;
    private final CollectionReference reference;

    public MessageWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        messagesRepository = MessagesRepository.getInstance(context.getApplicationContext());
        contactsRepository = ContactsRepository.getInstance(context.getApplicationContext());
        reference = FirebaseFirestore.getInstance().collection(FirebaseDatabasePaths.COLLECTION_CHATS);
    }

    @NonNull
    @Override
    public Result doWork() {
        String message_id = getInputData().getString(WorkConstants.ARG_KEY_MESSAGE_ID);
        String sender = getInputData().getString(WorkConstants.ARG_KEY_SENDER);
        String chat_convo_id = getInputData().getString(WorkConstants.ARG_KEY_CONVERSATION_ID);

        assert chat_convo_id != null;
        assert message_id != null;

        Task<DocumentSnapshot> readMessageTask = reference.document(chat_convo_id)
                .collection(FirebaseDatabasePaths.COLLECTION_MESSAGES)
                .document(message_id).get();

        try {
            DocumentSnapshot documentSnapshot = Tasks.await(readMessageTask);
            if (readMessageTask.isComplete()) {
                if (readMessageTask.isSuccessful()) {
                    if (documentSnapshot.exists()){
                        Message message = documentSnapshot.toObject(Message.class);
                        assert message != null;

                        Contact contact = contactsRepository.getContactObject(message.getSender());
                        if (contact == null) {
                            Task<DocumentSnapshot> snapshotTask = FirebaseFirestore.getInstance()
                                    .collection(FirebaseDatabasePaths.COLLECTIONS_USERS_PUBLIC)
                                    .document(message.getSender()).get();
                            DocumentSnapshot documentSnapshot1 = Tasks.await(snapshotTask);
                            if (snapshotTask.isComplete()) {
                                if (snapshotTask.isSuccessful()) {
                                    Contact contact1 = documentSnapshot1.toObject(Contact.class);
                                    contactsRepository.insertContact(new Contact(
                                            contact1.getNumber(),
                                            null,
                                            contact1.getServer_id(),
                                            "null",
                                            contact1.getName(),
                                            null,
                                            0L,
                                            contact1.getProfile_photo_url(),
                                            null,
                                            contact1.getDevice_instance_id(),
                                            contact1.getStatus(),
                                            contact1.getStatus_timestamp()
                                    ));
                                }
                            }
                        }

                        if (MessagesFragment.fragment_running && MessagesFragment.selected_number.equals(sender)) {
                            MessagesFragment.movedToEnd = false;

                            messagesRepository.insertMessage(message);

                            playReceivedSoundNotification();

                        } else {
                            messagesRepository.insertMessage(message);

                            sendMessageNotification(message);
                        }

                        if (message.getType().equals(Message.TYPE_MEDIA)) {
                            downloadMediaWork(message.getMedia_url(), message_id, chat_convo_id, message.getMedia_type());
                        }

                        Task<Void> deleteMessageTask = reference.document(chat_convo_id)
                                .collection(FirebaseDatabasePaths.COLLECTION_MESSAGES)
                                .document(message_id).delete();

                        Tasks.await(deleteMessageTask);

                        if (deleteMessageTask.isComplete()) {
                            if (deleteMessageTask.isSuccessful()) {
                                return Result.success();
                            }
                        }

                    }
                } else {
                    Log.e("MessageWork", "onComplete: ", readMessageTask.getException());

                }

            }

            return Result.failure();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void playReceivedSoundNotification(){
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("conversation_tones_key", true)) {
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.message_received_sound);
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

    private void downloadMediaWork(String file_name_path, String message_id, String chat_convo_id, String media_type) {
        Data messageReceiptData = new Data.Builder()
                .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_RECEIVE_MEDIA)
                .putString(WorkConstants.ARG_KEY_FILE_NAME, file_name_path)
                .putString(WorkConstants.ARG_KEY_MESSAGE_ID, message_id)
                .putString(WorkConstants.ARG_KEY_CONVERSATION_ID, chat_convo_id)
                .putString(WorkConstants.ARG_KEY_MEDIA_TYPE, media_type)
                .build();

        String uniqueWorkName = chat_convo_id + message_id;

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest downloadMediaWork =
                new OneTimeWorkRequest.Builder(MediaMessageWork.class)
                        .setConstraints(constraints)
                        .setInputData(messageReceiptData)
                        .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.KEEP, downloadMediaWork);
    }

    private void createMessageNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.message_notification_channel_name);
            String description = getApplicationContext().getString(R.string.message_notification_channel_description);

            NotificationChannel channel = new NotificationChannel(MESSAGE_CHANNEL_ID,
                    name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendMessageNotification(@NonNull Message message) {

        createMessageNotificationChannel();

        Contact contact = contactsRepository.getContactObject(message.getSender());
        String sender;
        if (contact.getDisplay_name().equals("null")) {
            sender = contact.getNumber();
        } else {
            sender = contact.getDisplay_name();
        }

        NotificationManager notificationManager = getApplicationContext()
                .getSystemService(NotificationManager.class);

        createMessageNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //TODO: set content of the intent
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_call_green_24dp)
                .setContentTitle(sender)
                .setStyle( new NotificationCompat.InboxStyle()
                    .addLine(message.getMessage()))
                .setContentText(message.getMessage())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        assert notificationManager != null;
        notificationManager.notify(MESSAGE_NOTIFICATION_ID, builder.build());
    }

}
