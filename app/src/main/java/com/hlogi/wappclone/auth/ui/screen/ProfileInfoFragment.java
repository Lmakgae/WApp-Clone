package com.hlogi.wappclone.auth.ui.screen;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.auth.ui.dialog.ProfilePictureBottomSheetDialog;
import com.hlogi.wappclone.auth.work.UpdateProfilePictureWork;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.work.MediaMessageWork;
import com.hlogi.wappclone.chats.work.WorkConstants;
import com.hlogi.wappclone.ui.dialogs.LoadingDialogFragment;
import com.hlogi.wappclone.ui.main.MainActivity;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModel;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModelFactory;
import com.hlogi.wappclone.databinding.FragmentProfileInfoBinding;
import com.hlogi.wappclone.ui.dialogs.AlertDialogFragment;
import com.hlogi.wappclone.util.Constants;
import com.hlogi.wappclone.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileInfoFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2;
    static final int READ_CONTACTS_REQUEST_CODE = 1;
    static final int CAMERA_REQUEST_CODE = 2;
    static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    static final int RECORD_AUDIO_REQUEST_CODE = 4;
    static final Long SECONDS_IN_A_MINUTE = 60L;
    static final Long NUMBER_OF_MINUTES = 60L;
    static final Long NUMBER_OF_HOURS = 12L;
    static final Long SYNC_INTERVAL = NUMBER_OF_HOURS * NUMBER_OF_MINUTES * SECONDS_IN_A_MINUTE;
    static final String TAG = "ProfileInfoFragment";
    private FragmentProfileInfoBinding binding;
    private AuthViewModel viewModel;
    private LoadingDialogFragment mLoadingDialog = new LoadingDialogFragment();
    private Boolean profileSet = false;
    private String file;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileInfoBinding.inflate(inflater, container, false);
        AuthViewModelFactory viewModelFactory = AuthViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(AuthViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        });

        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    READ_CONTACTS_REQUEST_CODE);
        } else {
            addAppAccount();
        }

        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_REQUEST_CODE);
        }

        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{(Manifest.permission.CAMERA)},
                    CAMERA_REQUEST_CODE);
        }

        viewModel.loading().observe(getViewLifecycleOwner(), loading -> {
            if (loading)
                mLoadingDialog.show(getChildFragmentManager(), LoadingDialogFragment.class.getSimpleName());
            else
                if (mLoadingDialog.isVisible()) {
                    mLoadingDialog.dismiss();
                }
        });

        viewModel.finished().observe(getViewLifecycleOwner(), finished -> {
            if(finished) {
                PreferenceManager.getDefaultSharedPreferences(requireActivity()).edit().putBoolean("profile_updated", true).apply();
                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            }
        });

        viewModel.getUserInfo().observe(getViewLifecycleOwner(), user -> {
            if (user.isSuccessful()) {
                binding.setUser(user.data());
                if (user.data().getName() != null && !user.data().getName().equals("null")) {
                    binding.nameEditText.setText(user.data().getName());
                }

                if (user.data().getProfile_photo_url() != null &&
                        !user.data().getProfile_photo_url().equals("null") &&
                        !user.data().getProfile_photo_url().equals("")){
                    profileSet = true;
                    binding.addPhotoIc.setVisibility(View.INVISIBLE);
                } else {
                    profileSet = false;
                    binding.addPhotoIc.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.profilePicture.setOnClickListener(v -> {
            ProfilePictureBottomSheetDialog dialog = new ProfilePictureBottomSheetDialog(profileSet);
            dialog.showNow(getChildFragmentManager(), ProfilePictureBottomSheetDialog.class.getSimpleName());
            dialog.setDialogClickListener(new ProfilePictureBottomSheetDialog.DialogClickListener() {
                @Override
                public void onDialogGalleryClick() {
                    dispatchGalleryImagePickIntent();
                }

                @Override
                public void onDialogCameraClick() {
                    dispatchTakePictureIntent();
                }

                @Override
                public void onDialogRemoveClick() {
                    viewModel.removeProfilePicture(requireActivity().getApplicationContext());
                }
            });
        });

        binding.next.setOnClickListener(v -> {
            if (!binding.nameEditText.getText().toString().isEmpty()) {
                viewModel.updateProfile(binding.nameEditText.getText().toString());
            } else {
                showAlertDialog(getString(R.string.required_to_enter_name));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_CONTACTS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        addAppAccount();
                    } else {
                        showPermissionsAlert();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_IMAGE){
            assert data != null;
            Uri imageUri = data.getData();
            assert imageUri != null;
            viewModel.uploadProfilePicture(imageUri.toString(), requireActivity().getApplicationContext());
            updateUIafterUpdate();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            switch (resultCode) {
                case RESULT_OK:
                    viewModel.uploadProfilePicture(file, requireActivity().getApplicationContext());
                    updateUIafterUpdate();
                    FileUtil.scanMedia(file, requireActivity().getApplicationContext());
                    break;
                case RESULT_CANCELED:
                    FileUtil.deleteFile(file);
                    break;
            }
        }
    }

    public void updateUIafterUpdate(){
        WorkManager.getInstance(requireActivity().getApplicationContext())
                .getWorkInfosForUniqueWorkLiveData(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .observe(getViewLifecycleOwner(), workInfos -> {
                    if (!workInfos.isEmpty()){
                        if (workInfos.get(0).getState() == WorkInfo.State.ENQUEUED){
                            binding.profilePicture.setVisibility(View.INVISIBLE);
                            binding.addPhotoIc.setVisibility(View.INVISIBLE);
                            binding.progressBar.setVisibility(View.VISIBLE);
                        } else if (workInfos.get(0).getState() == WorkInfo.State.RUNNING){
                            binding.profilePicture.setVisibility(View.INVISIBLE);
                            binding.addPhotoIc.setVisibility(View.INVISIBLE);
                            binding.progressBar.setVisibility(View.VISIBLE);
                        } else if (workInfos.get(0).getState() == WorkInfo.State.SUCCEEDED){
                            binding.profilePicture.setVisibility(View.VISIBLE);
                            binding.addPhotoIc.setVisibility(View.INVISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(requireActivity(), "Profile picture updated", Toast.LENGTH_LONG).show();
                        } else if (workInfos.get(0).getState() == WorkInfo.State.FAILED){
                            binding.profilePicture.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.addPhotoIc.setVisibility(View.VISIBLE);
                            Toast.makeText(requireActivity(), "Profile picture failed to update.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

//        Uri uri = Uri.fromParts("package", packageName, null);

        AlertDialog alert = builder.create();
//        alert.setTitle(getString(R.string.permissions_alert));
        alert.show();
    }

    private Boolean checkIfAppAccountExists() {
        boolean accountExists = false;
        for(Account account: AccountManager.get(requireActivity()).getAccounts()) {
            if (account.type.equals(Constants.ACCOUNT_TYPE)) {
                accountExists = true;
                break;
            }
        }
        return accountExists;
    }

    private void addAppAccount() {
        Account mAccount = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);

        if (!checkIfAppAccountExists()) {
            if (AccountManager.get(requireActivity()).addAccountExplicitly(mAccount, null, null)) {
                ContentResolver.setSyncAutomatically(mAccount, ContactsContract.AUTHORITY, true);
                ContentResolver.addPeriodicSync(mAccount, ContactsContract.AUTHORITY, new Bundle(), SYNC_INTERVAL);
            }
        }
    }

    private void dispatchGalleryImagePickIntent() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if (gallery.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(gallery, REQUEST_PICK_IMAGE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_PROFILE_PHOTO);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                file = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void showAlertDialog(String message) {
        DialogFragment dialog = new AlertDialogFragment(message);
        dialog.show(getChildFragmentManager(), AlertDialogFragment.class.getSimpleName());
    }
}
