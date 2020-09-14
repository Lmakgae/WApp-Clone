package com.hlogi.wappclone.settings.ui;

import android.content.Intent;
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

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentSettingsBinding;
import com.hlogi.wappclone.qr.QRcodeActivity;
import com.hlogi.wappclone.settings.adapter.IconTitleSubtitleAdapter;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModel;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    static final String ARG_TITLE = "title";
    static final String ARG_TITLES = "titles";
    static final String ARG_SUBTITLES = "subtitles";
    static final String ARG_ICONS = "icons";
    static final String ARG_ACTIONS = "actions";
    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        SettingsViewModelFactory viewModelFactory = SettingsViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SettingsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.isSuccessful()) {
                binding.setUser(user.data());
            }
        });

        List<Integer> titles = new ArrayList<>();
        List<Integer> subtitles = new ArrayList<>();
        List<Integer> icons = new ArrayList<>();

        titles.add(R.string.account);
        titles.add(R.string.chats);
        titles.add(R.string.notifications);
        titles.add(R.string.data_and_storage_usage);
        titles.add(R.string.help);

        subtitles.add(R.string.privacy_security_change_number);
        subtitles.add(R.string.theme_wallpapers_chat_history);
        subtitles.add(R.string.message_group_call_tones);
        subtitles.add(R.string.network_usage_auto_download);
        subtitles.add(R.string.faq_contact_us_privacy_policy);

        icons.add(R.drawable.ic_lock_grey_24dp);
        icons.add(R.drawable.ic_chat_white_24dp);
        icons.add(R.drawable.ic_notifications_grey_24dp);
        icons.add(R.drawable.ic_data_usage_grey_24dp);
        icons.add(R.drawable.ic_help_grey_24dp);

        IconTitleSubtitleAdapter adapter = new IconTitleSubtitleAdapter(titles, subtitles, icons);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.recyclerView.setAdapter(adapter);

        adapter.setItemActionListener(title -> {
            switch (title) {
                case R.string.account:
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_settingsFragment_to_recycleViewFragment, navigateToAccount());
                    break;
                case R.string.chats:
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_settingsFragment_to_chatsPreferenceFragment);
                    break;
                case R.string.notifications:
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_settingsFragment_to_notificationsPreferenceFragment);
                    break;
                case R.string.data_and_storage_usage:
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_settingsFragment_to_dataStorageUsagePreferenceFragment);
                    break;
                case R.string.help:
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_settingsFragment_to_recycleViewFragment, navigateToHelp());
                    break;
            }
        });

        binding.profileName.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_profileFragment));
        binding.profileStatus.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_profileFragment));
        binding.profilePicture.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_profileFragment));
        binding.qr.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), QRcodeActivity.class)));

    }

    @NonNull
    private Bundle navigateToAccount() {
        Bundle bundle = new Bundle();

        ArrayList<Integer> titles = new ArrayList<>();
        ArrayList<Integer> subtitles = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();
        ArrayList<Integer> actions = new ArrayList<>();

        titles.add(R.string.privacy);
        titles.add(R.string.security);
        titles.add(R.string.two_step_verification);
        titles.add(R.string.change_number);
        titles.add(R.string.request_account_info);
        titles.add(R.string.delete_my_account);

        subtitles.add(R.string.null_t);
        subtitles.add(R.string.null_t);
        subtitles.add(R.string.null_t);
        subtitles.add(R.string.null_t);
        subtitles.add(R.string.null_t);
        subtitles.add(R.string.null_t);

        icons.add(R.drawable.ic_lock_grey_24dp);
        icons.add(R.drawable.ic_security_grey_24dp);
        icons.add(R.drawable.ic_baseline_more_horiz_24);
        icons.add(R.drawable.ic_baseline_phonelink_setup_24);
        icons.add(R.drawable.ic_file_grey_24dp);
        icons.add(R.drawable.ic_delete_grey_24dp);

        actions.add(R.id.action_recycleViewFragment_to_privacyFragment);
        actions.add(R.id.action_recycleViewFragment_to_securityFragment);
        actions.add(R.string.null_t);
        actions.add(R.id.action_recycleViewFragment_to_changeNumberFragment);
        actions.add(R.string.null_t);
        actions.add(R.id.action_recycleViewFragment_to_deleteMyNumberFragment);


        bundle.putString(ARG_TITLE, getString(R.string.account));
        bundle.putIntegerArrayList(ARG_TITLES, titles);
        bundle.putIntegerArrayList(ARG_SUBTITLES, subtitles);
        bundle.putIntegerArrayList(ARG_ICONS, icons);
        bundle.putIntegerArrayList(ARG_ACTIONS, actions);

        return bundle;
    }

    @NonNull
    private Bundle navigateToHelp() {
        Bundle bundle = new Bundle();

        ArrayList<Integer> titles = new ArrayList<>();
        ArrayList<Integer> subtitles = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();
        ArrayList<Integer> actions = new ArrayList<>();

        titles.add(R.string.faq);
        titles.add(R.string.contact_us);
        titles.add(R.string.terms_and_privacy_policy);
        titles.add(R.string.app_info);

        subtitles.add(R.string.null_t);
        subtitles.add(R.string.questions_need_help);
        subtitles.add(R.string.null_t);
        subtitles.add(R.string.null_t);

        icons.add(R.drawable.ic_help_grey_24dp);
        icons.add(R.drawable.ic_friend_24dp);
        icons.add(R.drawable.ic_file_grey_24dp);
        icons.add(R.drawable.ic_info_grey_24dp);

        actions.add(R.string.null_t);
        actions.add(R.id.action_recycleViewFragment_to_contactUsFragment);
        actions.add(R.string.null_t);
        actions.add(R.id.action_recycleViewFragment_to_appInfoFragment);

        bundle.putString(ARG_TITLE, getString(R.string.help));
        bundle.putIntegerArrayList(ARG_TITLES, titles);
        bundle.putIntegerArrayList(ARG_SUBTITLES, subtitles);
        bundle.putIntegerArrayList(ARG_ICONS, icons);
        bundle.putIntegerArrayList(ARG_ACTIONS, actions);

        return bundle;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }
}