package com.hlogi.wappclone.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModel;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModelFactory;
import com.hlogi.wappclone.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AuthViewModelFactory viewModelFactory = AuthViewModelFactory.createFactory(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AuthViewModel.class);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_auth);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
