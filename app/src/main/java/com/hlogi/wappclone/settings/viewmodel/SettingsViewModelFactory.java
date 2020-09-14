package com.hlogi.wappclone.settings.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hlogi.wappclone.auth.data.UserRepository;

import java.lang.reflect.InvocationTargetException;

public class SettingsViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository mUserRepository;

    public static SettingsViewModelFactory createFactory(Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new SettingsViewModelFactory(UserRepository.getInstance(context));
    }

    private SettingsViewModelFactory(UserRepository userRepository) {
        this.mUserRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(UserRepository.class)
                    .newInstance(mUserRepository);
        } catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
