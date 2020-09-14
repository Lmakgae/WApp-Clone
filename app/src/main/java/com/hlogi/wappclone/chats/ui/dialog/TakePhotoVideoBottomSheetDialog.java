package com.hlogi.wappclone.chats.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hlogi.wappclone.databinding.FragmentTakePhotoVideoBottomsheetBinding;

public class TakePhotoVideoBottomSheetDialog extends BottomSheetDialogFragment {

    private FragmentTakePhotoVideoBottomsheetBinding binding;

    private TakePhotoVideoBottomSheetDialog.DialogClickListener listener;

    public interface DialogClickListener {
        void onDialogPhotoClick();
        void onDialogVideoClick();
    }

    public void setListener(DialogClickListener listener) {
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
        binding = FragmentTakePhotoVideoBottomsheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageText.setOnClickListener(v -> {
            listener.onDialogPhotoClick();
            dismiss();
        });

        binding.takePhoto.setOnClickListener(v -> {
            listener.onDialogPhotoClick();
            dismiss();
        });

        binding.takeVideo.setOnClickListener(v -> {
            listener.onDialogVideoClick();
            dismiss();
        });

        binding.videoText.setOnClickListener(v -> {
            listener.onDialogVideoClick();
            dismiss();
        });

    }
}
