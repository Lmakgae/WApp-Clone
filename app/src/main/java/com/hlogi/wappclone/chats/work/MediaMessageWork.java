package com.hlogi.wappclone.chats.work;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.MessagesRepository;
import com.hlogi.wappclone.chats.data.model.ChatConversation;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.data.model.MessageMetadata;
import com.hlogi.wappclone.chats.ui.screen.MessagesFragment;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;
import com.hlogi.wappclone.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MediaMessageWork extends Worker {

    public static final String PROGRESS = "PROGRESS";
    private static final String TAG = MediaMessageWork.class.getSimpleName();
    private final MessagesRepository messagesRepository;
    private final CollectionReference reference;
    private String action = null;
    private StorageReference storageReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private StorageTask<FileDownloadTask.TaskSnapshot> downloadTask;
    private Uri uploadUriSession = null;
    private Boolean uploadSessionSaved = false;

    public MediaMessageWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        storageReference = FirebaseStorage.getInstance().getReference();
        messagesRepository = MessagesRepository.getInstance(context.getApplicationContext());
        reference = FirebaseFirestore.getInstance().collection(FirebaseDatabasePaths.COLLECTION_CHATS);
        setProgressAsync(new Data.Builder()
                .putDouble(PROGRESS, 0)
                .build());
    }

    @NonNull
    @Override
    public Result doWork() {

        action = getInputData().getString(WorkConstants.ARG_KEY_ACTION);
        String file_name_path = getInputData().getString(WorkConstants.ARG_KEY_FILE_NAME);
        String message_id = getInputData().getString(WorkConstants.ARG_KEY_MESSAGE_ID);
        String chat_convo_id = getInputData().getString(WorkConstants.ARG_KEY_CONVERSATION_ID);
        String media_type = getInputData().getString(WorkConstants.ARG_KEY_MEDIA_TYPE);
        String upload_uri_string = getInputData().getString(WorkConstants.ARG_KEY_UPLOAD_URI);

        if (upload_uri_string != null) {
            uploadUriSession = Uri.parse(upload_uri_string);
        }

        assert action != null;
        assert media_type != null;
        switch (action) {
            case WorkConstants.ACTION_RECEIVE_MEDIA:
                return downloadMedia(file_name_path, message_id, chat_convo_id, media_type);
            case WorkConstants.ACTION_SEND_MEDIA:
                return sendMedia(file_name_path, message_id, chat_convo_id, media_type);
        }

        return Result.failure();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.e(TAG, "onStopped: Stopped started");
        switch (action) {
            case WorkConstants.ACTION_RECEIVE_MEDIA:
                Log.e(TAG, "onStopped: download");
                if (downloadTask != null) {
                    if (downloadTask.isInProgress()) {
                        Log.e(TAG, "onStopped: download in progress");
                        if (downloadTask.pause()) {
                            Log.e(TAG, "onStopped: download stopped");
                        } else {
                            Log.e(TAG, "onStopped: download did NOT stop");
                        }
                    }
                }
                break;
            case WorkConstants.ACTION_SEND_MEDIA:
                if (uploadTask != null) {
                    if (uploadTask.isInProgress()) {
                        uploadTask.pause();
                    }
                }
                break;
        }

    }

    @NonNull
    private Result downloadMedia(String file_name_path, String message_id, String chat_convo, @NonNull String media_type)  {
        Log.e(TAG, "downloadMedia: Starting download media function");
        StorageReference mediaRef = storageReference.child(file_name_path);

        File localFile;

        try {
            Log.e(TAG, "downloadMedia: Creating file" );

            switch (media_type) {
                case Message.MEDIA_PHOTO:
                    localFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_IMAGE);
                    break;
                case Message.MEDIA_VIDEO:
                    localFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_VIDEO);
                    break;
                case Message.MEDIA_VOICE_NOTE:
                    localFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_VOICE);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + media_type);
            }

            assert localFile != null;
            Log.e(TAG, "downloadMedia: File created with path: " + localFile.getAbsolutePath() );
            Log.e(TAG, "downloadMedia: Downloading");
            downloadTask = mediaRef.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.e(TAG, "onProgress: Download Progress is " + progress + "% done");
                    setProgressAsync(new Data.Builder().putDouble(PROGRESS, progress).build());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: On failure", e);
                }
            }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onPaused(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e(TAG, "onPaused: Paused" );
                }
            });

            FileDownloadTask.TaskSnapshot taskSnapshot = Tasks.await(downloadTask);

            if (downloadTask.isComplete()) {
                Log.e(TAG, "downloadMedia: Download is complete");
                if (downloadTask.isSuccessful()) {
                    Log.e(TAG, "downloadMedia: Download is complete and successful");

                    Message message = messagesRepository.getMessage(chat_convo, message_id);

                    if (message == null)
                        return Result.failure();

                    message.setMedia_url(null);
                    message.setMedia_path(localFile.getAbsolutePath());

                    messagesRepository.insertMessage(message);

                    return Result.success();
                } else if (downloadTask.isCanceled()){
                    Log.e(TAG, "downloadMedia: Download is complete and cancelled");
                    return Result.failure();
                } else {
                    Log.e(TAG, "downloadMedia: Exception: ", downloadTask.getException());
                    return Result.failure();
                }
            }

        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        return Result.failure();
    }

    @NonNull
    private UploadTask uploadTask(StorageReference mediaRef, File localFile, @Nullable Uri uploadSession) {
        if (uploadSession == null)
            return mediaRef.putFile(Uri.fromFile(localFile));
        else
            return mediaRef.putFile(
                    Uri.fromFile(localFile),
                    new StorageMetadata.Builder().build(),
                    uploadSession);
    }

    @NonNull
    private Result sendMedia(String file_name_path, String message_id, String chat_convo, @NonNull String media_type) {
        StorageReference mediaRef = storageReference.child(chat_convo + "/" + message_id);

        try {

            File localFile = null;

            Uri fileUri = Uri.parse(file_name_path);

            if (Objects.equals(fileUri.getScheme(), "file")){
                localFile = new File(file_name_path);
            } else if (Objects.equals(fileUri.getScheme(), "content")) {
                switch (media_type) {
                    case Message.MEDIA_PHOTO:
                        localFile = FileUtil.copyFileFromUri(getApplicationContext(), fileUri, FileUtil.FILE_TYPE_IMAGE);
                        break;
                    case Message.MEDIA_VIDEO:
                        localFile = FileUtil.copyFileFromUri(getApplicationContext(), fileUri, FileUtil.FILE_TYPE_VIDEO);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + media_type);
                }
            }

            assert localFile != null;

            uploadTask = uploadTask(mediaRef, localFile, uploadUriSession).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    uploadUriSession = taskSnapshot.getUploadSessionUri();
                    if (uploadUriSession != null && !uploadSessionSaved) {
                        uploadSessionSaved = true;
                        //Save the uploadUri;
                        messagesRepository.insertUploadMediaSession(message_id, chat_convo, uploadUriSession);
                    }
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    setProgressAsync(new Data.Builder().putDouble(PROGRESS, progress).build());
                }
            });

            UploadTask.TaskSnapshot taskSnapshot = Tasks.await(uploadTask);

            if (uploadTask.isComplete()) {
                if (uploadTask.isSuccessful()) {

                    Message message = messagesRepository.getMessage(chat_convo, message_id);

                    if (message == null)
                        return Result.failure();

                    message.setMedia_url(chat_convo + "/" + message_id);

                    WriteBatch batch = FirebaseFirestore.getInstance().batch();

                    DocumentReference newMessageRef = reference
                            .document(message.getConversation_id())
                            .collection(FirebaseDatabasePaths.COLLECTION_MESSAGES)
                            .document(message.getMessage_id());

                    ChatConversation conversation = new ChatConversation(message.getConversation_id(),
                            FirebaseDatabasePaths.CONVERSATION_INDIVIDUAL_TYPE,
                            Arrays.asList(message.getSender(), message.getReceiver()),
                            Timestamp.now().toDate().getTime(),
                            null);

                    batch.set(newMessageRef, message)
                            .set(reference.document(message.getConversation_id()), conversation, SetOptions.merge());

                    Task<Void> batchTask = batch.commit();

                    Tasks.await(batchTask);

                    if (batchTask.isComplete()) {
                        if (batchTask.isSuccessful()) {

                            message.setMedia_url(null);

                            message.setMedia_path(localFile.getAbsolutePath());

                            MessageMetadata metadata = new MessageMetadata(false, false);

                            message.setMessageMetadata(metadata);

                            messagesRepository.insertMessage(message);

                            if (MessagesFragment.fragment_running && MessagesFragment.selected_number.equals(message.getReceiver())) {

                                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.message_sent_sound);
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
                    }

                    return Result.success();
                } else if (uploadTask.isCanceled()){
                    return Result.failure();
                } else {
                    Log.e(TAG, "sendMedia: Exception: ", uploadTask.getException());
                    return Result.failure();
                }
            }

        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }



}
