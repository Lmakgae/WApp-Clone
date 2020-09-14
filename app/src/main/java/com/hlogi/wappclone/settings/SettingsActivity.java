package com.hlogi.wappclone.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    static final String ARG_TITLE = "title";
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle(getString(R.string.settings));

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_settings);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
////        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.recycleViewFragment) {
                    if (arguments != null) {
                        String title = arguments.getString(ARG_TITLE);
                        binding.toolbar.setTitle(title);
                    }
                } else {
                    binding.toolbar.setTitle(destination.getLabel());
                }
            }
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            if (navController.getCurrentDestination().getId() == R.id.settingsFragment)
                finish();
            else
                navController.navigateUp();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}