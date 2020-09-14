package com.hlogi.wappclone.qr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hlogi.wappclone.databinding.FragmentQrMyCodeBinding;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModel;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModelFactory;

public class MyCodeFragment extends Fragment {

    FragmentQrMyCodeBinding binding;
    private SettingsViewModel viewModel;
    private FirebaseUser user;
    Bitmap bitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQrMyCodeBinding.inflate(inflater, container, false);
        SettingsViewModelFactory viewModelFactory = SettingsViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SettingsViewModel.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.isSuccessful()) {
                binding.setUser(user.data());
            }
        });

        //TODO: Choose what to put in the QR code
        bitmap = QRCodeHelper
                .newInstance(requireActivity())
                .setContent(user.getPhoneNumber())
                .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                .setMargin(2)
                .getQRCOde();

        binding.qrCode.setImageBitmap(bitmap);

    }
}
