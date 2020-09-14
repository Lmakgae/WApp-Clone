package com.hlogi.wappclone.chats.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hlogi.wappclone.chats.data.model.MessageReceipt;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;

import java.util.concurrent.ExecutionException;

public class MessageReceiptWork extends Worker {

    private static final String ARG_KEY_MESSAGE_ID = "message_id_key";
    private static final String ARG_KEY_SENDER = "sender_key";
    private static final String ARG_KEY_CONVERSATION_ID = "conversation_id_key";
    private static final String ARG_KEY_TIMESTAMP = "timestamp_key";
    private final CollectionReference reference;

    public MessageReceiptWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        reference = FirebaseFirestore.getInstance().collection(FirebaseDatabasePaths.COLLECTION_CHATS);
    }

    @NonNull
    @Override
    public Result doWork() {
        String message_id = getInputData().getString(ARG_KEY_MESSAGE_ID);
        String sender = getInputData().getString(ARG_KEY_SENDER);
        String chat_convo_id = getInputData().getString(ARG_KEY_CONVERSATION_ID);
        Long timestamp = getInputData().getLong(ARG_KEY_TIMESTAMP, Timestamp.now().toDate().getTime());

        assert chat_convo_id != null;
        assert message_id != null;
        assert sender != null;

        MessageReceipt messageReceipt = new MessageReceipt(sender, message_id, timestamp);

        Task<Void> task = reference.document(chat_convo_id)
                .collection(FirebaseDatabasePaths.COLLECTION_MESSAGES_RECEIPTS)
                .document(message_id).set(messageReceipt);

        try {
            Tasks.await(task);
            if (task.isComplete()) {
                if (task.isSuccessful()) {
                    return Result.success();
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
