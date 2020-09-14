package com.hlogi.wappclone.settings.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentChangeAboutBottomSheetBinding;
import com.hlogi.wappclone.databinding.FragmentChangeNameBottomSheetBinding;


import java.util.Objects;

public class EditTextBottomSheetDialog extends BottomSheetDialogFragment {

    private FragmentChangeNameBottomSheetBinding mNameBinding;
    private FragmentChangeAboutBottomSheetBinding mAboutBinding;
    private DialogOnSaveListener listener;
    private Integer change;
    private String text;


    public EditTextBottomSheetDialog(String text, Integer change) {
        this.change = change;
        this.text = text;
    }

    public interface DialogOnSaveListener {
        void onDialogSaveClick(String text);
    }

    public void setDialogOnClickSaveListener(DialogOnSaveListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (change == Change.NAME) {
            mNameBinding = FragmentChangeNameBottomSheetBinding.inflate(inflater, container, false);
            return mNameBinding.getRoot();
        } else {
            mAboutBinding = FragmentChangeAboutBottomSheetBinding.inflate(inflater, container, false);
            return mAboutBinding.getRoot();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (change == Change.NAME) {
            mNameBinding.editText.setText(text);
            mNameBinding.save.setOnClickListener(v -> {
                if (mNameBinding.editText.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Name can't be empty", Toast.LENGTH_LONG).show();
                } else {
                    listener.onDialogSaveClick(Objects.requireNonNull(mNameBinding.editText.getText()).toString());
                    dismiss();
                }
            });

            mNameBinding.cancel.setOnClickListener(v -> dismiss());

        } else {
            mAboutBinding.editText.setText(text);
            mAboutBinding.save.setOnClickListener(v -> {
                if (mAboutBinding.editText.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Name can't be empty", Toast.LENGTH_LONG).show();
                } else {
                    listener.onDialogSaveClick(Objects.requireNonNull(mAboutBinding.editText.getText()).toString());
                    dismiss();
                }
            });

            mAboutBinding.cancel.setOnClickListener(v -> dismiss());
        }




    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        mNameBinding = null;
        mAboutBinding = null;
    }

    public static class Change {
        public static final int NAME = 0;
        public static final int ABOUT = 1;
    }
}
