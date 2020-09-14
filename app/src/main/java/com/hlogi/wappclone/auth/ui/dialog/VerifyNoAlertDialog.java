package com.hlogi.wappclone.auth.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hlogi.wappclone.R;

public class VerifyNoAlertDialog extends DialogFragment {

    private VerifyNoAlertDialogListener listener;
    private String code;
    private String no;

    public interface VerifyNoAlertDialogListener {
        void onDialogPositiveClick(DialogInterface dialog);
    }

    public void setDialogListener(VerifyNoAlertDialogListener listener) {
        this.listener = listener;
    }

    public VerifyNoAlertDialog(String code, String no) {
        this.code = code;
        this.no = no;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setMessage(getString(R.string.verify_no_alert, code, no))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    listener.onDialogPositiveClick(dialog);
                })
                .setNegativeButton(R.string.edit, ((dialog, which) -> dialog.dismiss()));
        return builder.create();
    }
}