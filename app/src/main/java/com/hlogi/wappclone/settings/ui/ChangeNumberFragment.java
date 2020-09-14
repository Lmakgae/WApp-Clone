package com.hlogi.wappclone.settings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hlogi.wappclone.databinding.FragmentChangeNumberBinding;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModel;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModelFactory;

public class ChangeNumberFragment extends Fragment {

    private FragmentChangeNumberBinding binding;
    private SettingsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangeNumberBinding.inflate(inflater, container, false);

        SettingsViewModelFactory viewModelFactory = SettingsViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SettingsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }
}
