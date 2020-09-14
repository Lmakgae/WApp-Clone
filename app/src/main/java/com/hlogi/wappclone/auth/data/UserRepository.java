package com.hlogi.wappclone.auth.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hlogi.wappclone.auth.data.model.User;
import com.hlogi.wappclone.data.WAppDatabase;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;
import com.hlogi.wappclone.firebase.FirestoreDocumentLiveData;
import com.hlogi.wappclone.util.Resource;

import java.util.concurrent.ExecutorService;

public class UserRepository {

    private static volatile UserRepository sInstance = null;
    private final UserDao mDao;
    private final ExecutorService mIoExecutor;
    private final CollectionReference mProfile_Col_Ref;

    public static UserRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserRepository.class) {
                if (sInstance == null) {
                    WAppDatabase database = WAppDatabase.getInstance(context);
                    sInstance = new UserRepository(database.userDao(),
                            WAppDatabase.databaseRWExecutor,
                            FirebaseFirestore.getInstance().collection(FirebaseDatabasePaths.COLLECTION_USERS));
                }
            }
        }

        return sInstance;
    }

    private UserRepository(UserDao dao, ExecutorService executor, CollectionReference reference) {
        this.mDao = dao;
        this.mIoExecutor = executor;
        this.mProfile_Col_Ref = reference;

    }

    public Task<Void> updateProfile(String uid, User user) {
        insertUser(user);
        DocumentReference documentReference = mProfile_Col_Ref.document(uid);
        return documentReference.set(user, SetOptions.merge());
    }

    public LiveData<User> getUser() {
        return mDao.getUser();
    }

    public void insertUser(User user) {
        mIoExecutor.execute(() -> {
            mDao.insert(user);
        });
    }

    public FirestoreDocumentLiveData<User> getProfile(String uid) {
        return new FirestoreDocumentLiveData<>(mProfile_Col_Ref.document(uid), User.class, mIoExecutor);
    }

}
