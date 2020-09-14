package com.hlogi.wappclone.chats.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.DialogViewProfileBinding;

public class ViewProfileDialog extends DialogFragment {

    private ViewProfileDialog.ViewProfileDialogListener listener;
    private DialogViewProfileBinding binding;
    private Contact contact;

    public interface ViewProfileDialogListener {
        void onClickProfilePicture(String profile_photo, String display_name);
        void onClickMessage(String number);
        void onClickCall(String number);
        void onClickVideoCall(String number);
        void onClickInfo(String number);
    }

    public void setActionListener(ViewProfileDialog.ViewProfileDialogListener listener) {
        this.listener = listener;
    }

    public ViewProfileDialog(Contact contact) {
        this.contact = contact;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        binding = null;
        contact = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogViewProfileBinding.inflate(inflater);

        binding.setContact(contact);

        builder.setView(binding.getRoot());

        binding.proPicIv.setOnClickListener(v -> {
            if (!(contact.getProfile_photo_url() == null || contact.getProfile_photo_url().equals(getString(R.string.null_t)))) {
                if(contact.getDisplay_name() == null || contact.getDisplay_name().equals(getString(R.string.null_t)))
                    listener.onClickProfilePicture(contact.getProfile_photo_url(), contact.getNumber());
                else
                    listener.onClickProfilePicture(contact.getProfile_photo_url(), contact.getDisplay_name());
                dismiss();
            }
        });


        binding.messageIv.setOnClickListener(v -> {
            listener.onClickMessage(contact.getNumber());
            dismiss();
        });

        binding.callIv.setOnClickListener(v -> {
            listener.onClickCall(contact.getNumber());
            dismiss();
        });

        binding.videoCallIv.setOnClickListener(v -> {
            listener.onClickVideoCall(contact.getNumber());
            dismiss();
        });

        binding.infoIv.setOnClickListener(v -> {
            listener.onClickInfo(contact.getNumber());
            dismiss();
        });
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
    }
}