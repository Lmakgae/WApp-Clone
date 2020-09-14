package com.hlogi.wappclone.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;
import com.hlogi.wappclone.util.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class FirestoreQueryLiveData<T> extends LiveData<Resource<T>> implements EventListener<QuerySnapshot> {

    private static final String TAG = "FirestoreQueryLiveData";
    private final Query query;
    private final  Class<T> type;
    private ListenerRegistration registration;
    private final ExecutorService mExecutorService;

    public FirestoreQueryLiveData(Query query, Class<T> type, ExecutorService executorService) {
        this.query = query;
        this.type = type;
        this.mExecutorService = executorService;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            setValue(new Resource<>(e));
            return;
        }

        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
            mExecutorService.execute(() -> {
                postValue(new Resource<T>(queryDocumentSnapshots.toObjects(type), documentToListMetadata(queryDocumentSnapshots)));
            });
        } else {
            setValue(new Resource<>());
        }

    }

    @Override
    protected void onActive() {
        super.onActive();
        registration = query.addSnapshotListener(this);
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

    @NonNull
    private List<SnapshotMetadata> documentToListMetadata(QuerySnapshot snapshots) {
        final List<SnapshotMetadata> retListHasPendingWrites = new ArrayList<>();
        for (DocumentSnapshot document : snapshots.getDocuments()) {
            retListHasPendingWrites.add(document.getMetadata());
        }
        return retListHasPendingWrites;
    }
}
