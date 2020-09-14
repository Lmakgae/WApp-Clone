package com.hlogi.wappclone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class SyncService extends Service {

    private static final String TAG = SyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter mSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Sync Service created.");
        synchronized (sSyncAdapterLock) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new SyncAdapter(getApplicationContext(), true, false);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Sync Service binded.");
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
