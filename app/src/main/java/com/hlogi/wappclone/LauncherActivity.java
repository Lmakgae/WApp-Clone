package com.hlogi.wappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hlogi.wappclone.auth.AuthActivity;
import com.hlogi.wappclone.ui.main.MainActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            startActivity(new Intent(this, AuthActivity.class));
        } else {
            boolean profileUpdated = PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("profile_updated", false);
            if (profileUpdated) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, AuthActivity.class));
            }
        }
        finish();
    }
}