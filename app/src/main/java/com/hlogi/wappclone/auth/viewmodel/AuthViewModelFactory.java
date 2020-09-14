package com.hlogi.wappclone.auth.viewmodel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hlogi.wappclone.auth.data.CountryRepository;
import com.hlogi.wappclone.auth.data.UserRepository;

import java.lang.reflect.InvocationTargetException;

public class AuthViewModelFactory implements ViewModelProvider.Factory {

    private final CountryRepository mCountryRepository;
    private final UserRepository mUserRepository;

    public static AuthViewModelFactory createFactory(Activity activity) {
        Context context = activity.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new AuthViewModelFactory(CountryRepository.getInstance(context), UserRepository.getInstance(context));
    }

    private AuthViewModelFactory(CountryRepository countryRepository, UserRepository userRepository) {
        this.mCountryRepository = countryRepository;
        this.mUserRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(CountryRepository.class, UserRepository.class)
                    .newInstance(mCountryRepository, mUserRepository);
        } catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
