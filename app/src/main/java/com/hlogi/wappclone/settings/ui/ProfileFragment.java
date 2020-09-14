package com.hlogi.wappclone.settings.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.data.model.User;
import com.hlogi.wappclone.auth.ui.dialog.ProfilePictureBottomSheetDialog;
import com.hlogi.wappclone.databinding.FragmentProfileBinding;
import com.hlogi.wappclone.settings.dialog.EditTextBottomSheetDialog;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModel;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModelFactory;
import com.hlogi.wappclone.util.FileUtil;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2;
    static final String ARG_PROFILE_PHOTO = "profile_photo";
    static final String ARG_DISPLAY_NAME = "display_name";
    private FragmentProfileBinding binding;
    private SettingsViewModel viewModel;
    private String file;
    private Boolean profileSet = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        SettingsViewModelFactory viewModelFactory = SettingsViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SettingsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.isSuccessful()) {
                binding.setUser(user.data());
                profileSet = user.data().getProfile_photo_url() != null &&
                        !user.data().getProfile_photo_url().equals("null") &&
                        !user.data().getProfile_photo_url().equals("");

            }
        });

        binding.profilePicture.setOnClickListener(v -> {
            if (profileSet){
                Bundle bundle = new Bundle();
                bundle.putString(ARG_PROFILE_PHOTO, viewModel.getUser().getValue().data().getProfile_photo_url());
                bundle.putString(ARG_DISPLAY_NAME, getString(R.string.profile_photo));
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_viewProfilePictureFragment2, bundle);
            }
        });

        binding.addProfilePicture.setOnClickListener(v -> {
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

        binding.nameView.setOnClickListener(v -> {
            EditTextBottomSheetDialog dialog = new EditTextBottomSheetDialog(binding.profileName.getText().toString(), EditTextBottomSheetDialog.Change.NAME);
            dialog.showNow(getChildFragmentManager(), dialog.getClass().getSimpleName());
            dialog.setDialogOnClickSaveListener(new EditTextBottomSheetDialog.DialogOnSaveListener() {
                @Override
                public void onDialogSaveClick(String text) {
                    User user = viewModel.getUser().getValue().data();
                    if (!user.getName().equals(text)) {
                        user.setName(text);
                        viewModel.updateProfile(user);
                    }
                }
            });
        });

        binding.aboutView.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_fragmentAbout);
        });

        binding.phoneView.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_changeNumberFragment);
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                            binding.progressBar.setVisibility(View.VISIBLE);
                        } else if (workInfos.get(0).getState() == WorkInfo.State.RUNNING){
                            binding.profilePicture.setVisibility(View.INVISIBLE);
                            binding.progressBar.setVisibility(View.VISIBLE);
                        } else if (workInfos.get(0).getState() == WorkInfo.State.SUCCEEDED){
                            binding.profilePicture.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(requireActivity(), "Profile picture updated", Toast.LENGTH_LONG).show();
                        } else if (workInfos.get(0).getState() == WorkInfo.State.FAILED){
                            binding.profilePicture.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(requireActivity(), "Profile picture failed to update.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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


}
