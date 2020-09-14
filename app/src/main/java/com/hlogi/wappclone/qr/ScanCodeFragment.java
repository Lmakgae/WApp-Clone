package com.hlogi.wappclone.qr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentScanCodeBinding;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static int CAMERA_REQUEST_CODE = 1;
    private FragmentScanCodeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanCodeBinding.inflate(inflater, container, false);

        binding.qrCodeScanner.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        binding.qrCodeScanner.setAutoFocus(true);
        binding.qrCodeScanner.setLaserColor(R.color.colorAccent);
        binding.qrCodeScanner.setMaskColor(R.color.colorAccent);
        if (Build.MANUFACTURER.equals("HUAWEI"))
            binding.qrCodeScanner.setAspectTolerance(0.5f);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{(Manifest.permission.CAMERA)},
                    CAMERA_REQUEST_CODE);
            return;
        }
        binding.qrCodeScanner.startCamera();
        binding.qrCodeScanner.setResultHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.qrCodeScanner.stopCamera();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
            Log.e("ScanCodeFragment", "handleResult: Not empty");
            Log.e("ScanCodeFragment", "handleResult: Text: " + result.toString());
        }
    }
}
