package com.hlogi.wappclone.settings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.data.model.User;
import com.hlogi.wappclone.databinding.FragmentAboutBinding;
import com.hlogi.wappclone.settings.adapter.AboutAdapter;
import com.hlogi.wappclone.settings.dialog.EditTextBottomSheetDialog;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModel;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModelFactory;
import com.hlogi.wappclone.ui.dialogs.LoadingDialogFragment;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;
    private SettingsViewModel viewModel;
    private LoadingDialogFragment mLoadingDialog = new LoadingDialogFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);

        SettingsViewModelFactory viewModelFactory = SettingsViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SettingsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.loading().observe(getViewLifecycleOwner(), loading -> {
            if (loading)
                mLoadingDialog.show(getChildFragmentManager(), LoadingDialogFragment.class.getSimpleName());
            else
            if (mLoadingDialog.isVisible()) {
                mLoadingDialog.dismiss();
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.isSuccessful()) {
                binding.about.setText(user.data().getStatus());
            }
        });

        AboutAdapter adapter = new AboutAdapter(viewModel.getUser().getValue().data().getStatus());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.recyclerView.setAdapter(adapter);

        adapter.setItemActionListener(about -> {
            if (!viewModel.getUser().getValue().data().getStatus().equals(about)) {
                binding.about.setText(about);
                User user = viewModel.getUser().getValue().data();
                user.setStatus(about);
                user.setStatus_timestamp(Timestamp.now().toDate().getTime());
                viewModel.updateProfile(user);
                adapter.setSelected(about);
                adapter.notifyDataSetChanged();
            }
        });

        binding.about.setOnClickListener(v -> {
            EditTextBottomSheetDialog dialog = new EditTextBottomSheetDialog(binding.about.getText().toString(), EditTextBottomSheetDialog.Change.ABOUT);
            dialog.showNow(getChildFragmentManager(), dialog.getClass().getSimpleName());
            dialog.setDialogOnClickSaveListener(text -> {
                Snackbar.make(requireView(), "Showed the text: " + text, Snackbar.LENGTH_LONG).show();
                User user = viewModel.getUser().getValue().data();
                if (!user.getStatus().equals(text)) {
                    user.setStatus(text);
                    user.setStatus_timestamp(Timestamp.now().toDate().getTime());
                    viewModel.updateProfile(user);
                    adapter.setSelected(text);
                    adapter.notifyDataSetChanged();
                }
            });
        });


    }
}
