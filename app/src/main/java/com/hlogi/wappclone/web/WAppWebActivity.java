package com.hlogi.wappclone.web;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hlogi.wappclone.databinding.ActivityWappWebBinding;

//TODO: Implement signing in to WApp Web using QR code
public class WAppWebActivity extends AppCompatActivity {

    private ActivityWappWebBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWappWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}