package com.hlogi.wappclone.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.SnapshotMetadata;

import java.util.List;

public final class Resource<T> {

    @Nullable
    private final T data;
    @Nullable
    private final Boolean isEmpty;
    @Nullable
    private final SnapshotMetadata hasPendingWrites;
    @Nullable
    private final Exception error;
    @Nullable
    private final List<T> list;
    @Nullable
    private final List<SnapshotMetadata> hasPendingWritesList;

    public Resource(@Nullable T data, @Nullable SnapshotMetadata hasPendingWrites){
        this(data, hasPendingWrites,null,null, null, false);
    }

    public Resource(@NonNull List<T> list, @Nullable List<SnapshotMetadata> hasPendingWritesList){
        this(null, null, list, hasPendingWritesList ,null, false);
    }

    public Resource(@NonNull Exception exception) {
        this(null,null, null , null ,exception, false);
    }

    public Resource() {
        this(null,null, null , null ,null, true);
    }

    private Resource(@Nullable T data,@Nullable SnapshotMetadata hasPendingWrites ,@Nullable List<T> list, @Nullable List<SnapshotMetadata> hasPendingWritesList, @Nullable Exception error, @Nullable Boolean isEmpty) {
        this.data = data;
        this.hasPendingWrites = hasPendingWrites;
        this.list = list;
        this.hasPendingWritesList = hasPendingWritesList;
        this.error = error;
        this.isEmpty = isEmpty;
    }

    public boolean isSuccessful() {
        return (data != null && error == null)||(list != null && error == null);
    }

    public boolean isEmpty() {
        return (data == null && list == null && error == null);
    }

    public T data() {
        if (error != null) {
            throw new IllegalStateException("error is not null. Call isSuccessful() first.");
        }
        return data;
    }

    @Nullable
    public SnapshotMetadata dataHasPendingWrites() {
        if (error != null) {
            throw new IllegalStateException("error is not null. Call isSuccessful() first.");
        }
        return hasPendingWrites;
    }

    @Nullable
    public List<T> list() {
        if (error != null) {
            throw new IllegalStateException("error is not null. Call isSuccessful() first.");
        }
        return list;
    }

    @Nullable
    public List<SnapshotMetadata> listHasPendingWrites() {
        if (error != null) {
            throw new IllegalStateException("error is not null. Call isSuccessful() first.");
        }
        return hasPendingWritesList;
    }

    @Nullable
    public Exception error() {
        if (data != null || list != null) {
            throw new IllegalStateException("data or list is not null. Call isSuccessful() first.");
        }
        return error;
    }

}
