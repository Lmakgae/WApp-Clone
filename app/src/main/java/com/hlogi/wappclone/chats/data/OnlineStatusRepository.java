package com.hlogi.wappclone.chats.data;

import com.google.firebase.database.DatabaseReference;
import com.hlogi.wappclone.chats.data.model.OnlineStatus;
import com.hlogi.wappclone.data.WAppDatabase;
import com.hlogi.wappclone.firebase.DatabaseDocumentLiveData;

import java.util.concurrent.ExecutorService;

public class OnlineStatusRepository {

    private static final String DATABASE_REF_ONLINE_STATUS = "online_status";
    private static volatile OnlineStatusRepository sInstance = null;
    private final ExecutorService mIoExecutor;
    private final DatabaseReference mDatabase;

    public static OnlineStatusRepository getInstance(DatabaseReference databaseReference) {
        if (sInstance == null) {
            synchronized (OnlineStatusRepository.class) {
                if (sInstance == null) {
                    sInstance = new OnlineStatusRepository(WAppDatabase.databaseRWExecutor,
                                        databaseReference);
                }
            }
        }
        return sInstance;
    }

    private OnlineStatusRepository(ExecutorService executor, DatabaseReference databaseReference) {
        this.mIoExecutor = executor;
        this.mDatabase = databaseReference.child(DATABASE_REF_ONLINE_STATUS);
    }

    public void setUserStatus(String number, Boolean online, Long timestamp, Boolean typing, Boolean recording) {
        OnlineStatus onlineStatus = new OnlineStatus(number, online, timestamp, typing, recording);
        mDatabase.child(number).setValue(onlineStatus);
    }

    public DatabaseDocumentLiveData<OnlineStatus> getContactStatus(String number){
        return new DatabaseDocumentLiveData<>(mDatabase.child(number), OnlineStatus.class);
    }

}
