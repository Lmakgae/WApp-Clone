package com.hlogi.wappclone.auth.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hlogi.wappclone.databinding.FragmentProPicBottomSheetBinding;

public class ProfilePictureBottomSheetDialog extends BottomSheetDialogFragment {

    private FragmentProPicBottomSheetBinding binding;

    private ProfilePictureBottomSheetDialog.DialogClickListener listener;

    private Boolean profileSet;

    public ProfilePictureBottomSheetDialog(Boolean profileSet) {
        this.profileSet = profileSet;
    }

    public interface DialogClickListener {
        void onDialogGalleryClick();
        void onDialogCameraClick();
        void onDialogRemoveClick();
    }

    public void setDialogClickListener(ProfilePictureBottomSheetDialog.DialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProPicBottomSheetBinding.inflate(inflater, container, false);

        if (profileSet){
            binding.removeIv.setVisibility(View.VISIBLE);
            binding.removeTv.setVisibility(View.VISIBLE);
        } else {
            binding.removeIv.setVisibility(View.GONE);
            binding.removeTv.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.removeIv.setOnClickListener(v -> {
            listener.onDialogRemoveClick();
            dismiss();
        });

        binding.removeTv.setOnClickListener(v -> {
            listener.onDialogRemoveClick();
            dismiss();
        });

        binding.galleryImgView.setOnClickListener(v -> {
            listener.onDialogGalleryClick();
            dismiss();
        });

        binding.galleryTv.setOnClickListener(v -> {
            listener.onDialogGalleryClick();
            dismiss();
        });

        binding.cameraImgView.setOnClickListener(v -> {
            listener.onDialogCameraClick();
            dismiss();
        });

        binding.cameraTv.setOnClickListener(v -> {
            listener.onDialogCameraClick();
            dismiss();
        });

    }
}
