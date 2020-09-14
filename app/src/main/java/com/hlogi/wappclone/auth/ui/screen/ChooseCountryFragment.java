package com.hlogi.wappclone.auth.ui.screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hlogi.wappclone.auth.viewmodel.AuthViewModel;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModelFactory;
import com.hlogi.wappclone.auth.adapters.CountryAdapter;
import com.hlogi.wappclone.databinding.FragmentChooseCountryBinding;

public class ChooseCountryFragment extends Fragment {

    private FragmentChooseCountryBinding binding;
    private AuthViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseCountryBinding.inflate(inflater, container, false);
        AuthViewModelFactory viewModelFactory = AuthViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(AuthViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CountryAdapter adapter = new CountryAdapter(viewModel.getSelectedCountry().getValue());

        viewModel.getCountryList().observe(getViewLifecycleOwner(), adapter::submitList);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemAction(country -> {
            viewModel.getSelectedCountry().setValue(country);
            Navigation.findNavController(requireView()).navigateUp();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
