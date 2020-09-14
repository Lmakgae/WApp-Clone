package com.hlogi.wappclone.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AuthenticationService extends Service {

    private static final String TAG = AuthenticationService.class.getSimpleName();
    private Authenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        this.authenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
