package com.hlogi.wappclone.auth.ui.screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Override
    public void onResume() {
        super.onResume();
        checkIfSignedIn();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);

        binding.agreeBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_welcomeFragment_to_verifyNoFragment);
        });

        binding.restoreBtn.setOnClickListener(v -> {

        });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void checkIfSignedIn(){
        boolean profileUpdated = PreferenceManager.getDefaultSharedPreferences(requireActivity())
                .getBoolean("profile_updated", false);

        if (FirebaseAuth.getInstance().getCurrentUser() != null && !profileUpdated) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_auth);
            navController.navigate(R.id.action_welcomeFragment_to_profileInfoFragment);
        }
    }
}