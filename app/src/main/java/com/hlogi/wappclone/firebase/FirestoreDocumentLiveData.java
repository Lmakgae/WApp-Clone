package com.hlogi.wappclone.firebase;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.hlogi.wappclone.util.Resource;

import java.util.concurrent.ExecutorService;

public class FirestoreDocumentLiveData<T> extends LiveData<Resource<T>> implements EventListener<DocumentSnapshot> {

    private final static String TAG = "FStoreDocumentLiveData";
    private final Class<T> type;
    private ListenerRegistration registration;
    private final DocumentReference mDocumentReference;
    private final ExecutorService mExecutorService;

    public FirestoreDocumentLiveData(DocumentReference mDocumentReference, Class<T> type, ExecutorService executorService) {
        this.mDocumentReference = mDocumentReference;
        this.type = type;
        this.mExecutorService = executorService;
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            setValue(new Resource<>(e));
            return;
        }
        if(documentSnapshot != null && documentSnapshot.exists()){
            mExecutorService.execute(() -> {
                postValue(new Resource<>(documentSnapshot.toObject(type), documentSnapshot.getMetadata()));
            });
        }else {
            setValue(new Resource<>());
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        registration = mDocumentReference.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (!hasActiveObservers()) {
            if (registration != null) {
                registration.remove();
                registration = null;
            }
        }
    }
}
