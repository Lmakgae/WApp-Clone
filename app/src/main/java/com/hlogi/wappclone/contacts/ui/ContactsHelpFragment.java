package com.hlogi.wappclone.contacts.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentContactsHelpBinding;

public class ContactsHelpFragment extends Fragment {

    private FragmentContactsHelpBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactsHelpBinding.inflate(inflater, container, false);
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