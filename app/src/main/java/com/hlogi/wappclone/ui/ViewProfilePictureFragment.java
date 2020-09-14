package com.hlogi.wappclone.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hlogi.wappclone.databinding.FragmentViewProfilePictureBinding;

public class ViewProfilePictureFragment extends Fragment {

    static final String ARG_PROFILE_PHOTO = "profile_photo";
    static final String ARG_DISPLAY_NAME = "display_name";
    private FragmentViewProfilePictureBinding binding;
    private String profile_photo_url = "null";
    private String display_name = "null";

    public ViewProfilePictureFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewProfilePictureBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            profile_photo_url = getArguments().getString(ARG_PROFILE_PHOTO);
            display_name = getArguments().getString(ARG_DISPLAY_NAME);
        }
        binding.setProfilePhotoUrl(profile_photo_url);
        binding.setDisplayName(display_name);
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }

}
