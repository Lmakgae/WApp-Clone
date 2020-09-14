package com.hlogi.wappclone.auth.viewmodel;

import android.app.Activity;
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

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hlogi.wappclone.auth.data.model.Country;
import com.hlogi.wappclone.auth.data.CountryRepository;
import com.hlogi.wappclone.auth.data.model.User;
import com.hlogi.wappclone.auth.data.UserRepository;
import com.hlogi.wappclone.auth.work.UpdateProfilePictureWork;
import com.hlogi.wappclone.chats.work.WorkConstants;
import com.hlogi.wappclone.firebase.FirestoreDocumentLiveData;
import com.hlogi.wappclone.util.Resource;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthViewModel extends ViewModel {

    private final static String TAG = "AuthViewModel";
    private final static long TIME_DURATION = 60;

    private FirebaseAuth mAuth;
    private final UserRepository mUserRepository;
    private final CountryRepository mCountryRepository;
    private final MutableLiveData<Resource<FirebaseUser>> mFirebaseUser;
    private final MutableLiveData<Country> mSelectedCountry = new MutableLiveData<>( new Country("South Africa", "27"));
    private final MutableLiveData<Boolean> mLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mCodeSent = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mFinished = new MutableLiveData<>(false);
    private final MutableLiveData<String> mCountryCode = new MutableLiveData<>("0");
    private final MutableLiveData<String> mPhoneNumber = new MutableLiveData<>("0");
    private final MutableLiveData<FirebaseException> mVerifyNumberException = new MutableLiveData<>(null);
    private FirestoreDocumentLiveData<User> mUserInfo = null;
    private String mVerificationId;
    private String firebaseInstanceId;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    public AuthViewModel(CountryRepository countryRepository, UserRepository userRepository) {
        this.mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = new MutableLiveData<>(new Resource<>(mAuth.getCurrentUser(), null));
        this.mCountryRepository = countryRepository;
        this.mUserRepository = userRepository;
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.getResult() != null)
                firebaseInstanceId = task.getResult().getToken();
        });
    }

    public LiveData<List<Country>> getCountryList() {
        return mCountryRepository.getCountryList();
    }

    public MutableLiveData<Country> getSelectedCountry() {
        return mSelectedCountry;
    }

    public MutableLiveData<Boolean> loading() {
        return mLoading;
    }

    public MutableLiveData<Boolean> getCodeSentStatus() {
        return mCodeSent;
    }

    public MutableLiveData<String> getCountryCode() {
        return mCountryCode;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return mPhoneNumber;
    }

    public MutableLiveData<Resource<FirebaseUser>> getFirebaseUser() {
        return mFirebaseUser;
    }

    public MutableLiveData<FirebaseException> getVerifyNumberException() {
        return mVerifyNumberException;
    }

    public FirestoreDocumentLiveData<User> getUserInfo() {
        if (mUserInfo == null) {
            mUserInfo = mUserRepository.getProfile(mFirebaseUser.getValue().data().getUid());
        }
        return mUserInfo;
    }

    public MutableLiveData<Boolean> finished() {
        return mFinished;
    }

    public void verifyNo(String countryCode, String number, Activity activity) {
        if (Objects.equals(mCountryCode.getValue(), countryCode) && Objects.equals(mPhoneNumber.getValue(), number)) {
            getCodeSentStatus().setValue(true);
        } else {
            mCountryCode.setValue(countryCode);
            mPhoneNumber.setValue(number);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+" + mCountryCode.getValue() + mPhoneNumber.getValue(),
                    TIME_DURATION,
                    TimeUnit.SECONDS,
                    activity,
                    mCallbacks
            );
            loading().setValue(true);
        }
    }

    public void verifyCode(String code) {
        //noinspection ConstantConditions
        if (getCodeSentStatus().getValue()) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
    }

    public void resendCode(Activity activity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+" + mCountryCode.getValue() + mPhoneNumber.getValue(),
                TIME_DURATION,
                TimeUnit.SECONDS,
                activity,
                mCallbacks,
                mForceResendingToken
        );
    }

    public void updateProfile(String name) {
        User user = mUserInfo.getValue().data();
        if (user != null) {
            mLoading.setValue(true);
            user.setName(name);
            user.setDevice_instance_id(firebaseInstanceId);
            mUserRepository.updateProfile(user.getUid(), user).addOnCompleteListener(task -> {
                mLoading.setValue(false);
                if (task.isSuccessful()) {
                    mFinished.setValue(true);
                }
            });
        }

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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mLoading.setValue(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    loading().setValue(false);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = task.getResult().getUser();

                        assert firebaseUser != null;

                        User user = new User();

                        user.setUid(firebaseUser.getUid());
                        user.setCountry_code(mSelectedCountry.getValue().getCode());
                        user.setCountry_name(mSelectedCountry.getValue().getName());
                        user.setPhone_number(firebaseUser.getPhoneNumber());
                        user.setDevice_instance_id(firebaseInstanceId);
                        user.setStatus("Available");
                        user.setStatus_timestamp(Timestamp.now().toDate().getTime());

                        mUserRepository.updateProfile(user.getUid(), user).addOnCompleteListener(task1 -> {
                            mFirebaseUser.setValue(new Resource<>(firebaseUser, null));
                        });

                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.getException() != null)
                            mFirebaseUser.setValue(new Resource<>(task.getException()));

                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            loading().setValue(false);
//            getCodeSentStatus().setValue(true);
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            loading().setValue(false);
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            mVerifyNumberException.setValue(e);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            loading().setValue(false);
            getCodeSentStatus().setValue(true);
            mVerificationId = s;
            mForceResendingToken = forceResendingToken;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
        }
    };
}
