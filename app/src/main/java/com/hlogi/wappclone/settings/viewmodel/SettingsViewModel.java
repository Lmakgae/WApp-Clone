package com.hlogi.wappclone.settings.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.auth.data.UserRepository;
import com.hlogi.wappclone.auth.data.model.User;
import com.hlogi.wappclone.auth.work.UpdateProfilePictureWork;
import com.hlogi.wappclone.chats.work.WorkConstants;
import com.hlogi.wappclone.util.Resource;

public class SettingsViewModel extends ViewModel {

    private final UserRepository mUserRepository;
    private final LiveData<Resource<User>> user;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);


    public SettingsViewModel(@NonNull UserRepository userRepository) {
        this.mUserRepository = userRepository;
        this.user = userRepository.getProfile(FirebaseAuth.getInstance().getUid());
    }

    public UserRepository getUserRepository() {
        return mUserRepository;
    }

    public MutableLiveData<Boolean> loading() {
        return loading;
    }

    public LiveData<Resource<User>> getUser() {
        return user;
    }

    public void updateProfile(User user) {
        loading.setValue(true);
        mUserRepository.updateProfile(FirebaseAuth.getInstance().getUid(), user)
                .addOnCompleteListener(task -> loading.setValue(false));
    }

    public void uploadProfilePicture(@NonNull String file, Context context) {
        Data mediaMessageData = new Data.Builder()
                .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_UPDATE_PROFILE_PICTURE)
                .putString(WorkConstants.ARG_KEY_FILE_NAME, file)
                .build();

        String uniqueWorkName = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest mediaMessageWork =
                new OneTimeWorkRequest.Builder(UpdateProfilePictureWork.class)
                        .setConstraints(constraints)
                        .setInputData(mediaMessageData)
                        .build();

        WorkManager.getInstance(context).enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.KEEP, mediaMessageWork);
    }

    public void removeProfilePicture(Context context) {
        Data mediaMessageData = new Data.Builder()
                .putString(WorkConstants.ARG_KEY_ACTION, WorkConstants.ACTION_REMOVE_PROFILE_PICTURE)
                .build();

        String uniqueWorkName = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest mediaMessageWork =
                new OneTimeWorkRequest.Builder(UpdateProfilePictureWork.class)
                        .setConstraints(constraints)
                        .setInputData(mediaMessageData)
                        .build();

        WorkManager.getInstance(context).enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.KEEP, mediaMessageWork);
    }
}
