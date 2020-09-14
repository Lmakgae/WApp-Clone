package com.hlogi.wappclone.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hlogi.wappclone.util.Resource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseDocumentLiveData<T> extends LiveData<Resource<T>> implements ValueEventListener {

    private final static String TAG = "DatabaseDocuLiveData";
    private final Class<T> type;
    private ValueEventListener listener;
    private final DatabaseReference mDatabaseReference;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    public DatabaseDocumentLiveData(DatabaseReference databaseReference, Class<T> type) {
        this.mDatabaseReference = databaseReference;
        this.type = type;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
            mExecutorService.execute(() -> {
                postValue(new Resource<>(snapshot.getValue(type), null));
            });
        } else {
            setValue(new Resource<>());
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        setValue(new Resource<T>(error.toException()));
    }

    @Override
    protected void onActive() {
        super.onActive();
        listener = mDatabaseReference.addValueEventListener(this);

    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (!hasActiveObservers()) {
            if (listener != null) {
                mDatabaseReference.removeEventListener(listener);
                listener = null;
            }
        }

    }
}
