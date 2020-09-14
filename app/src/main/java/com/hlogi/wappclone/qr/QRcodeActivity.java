package com.hlogi.wappclone.qr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.calls.CallsFragment;
import com.hlogi.wappclone.camera.CameraFragment;
import com.hlogi.wappclone.chats.ui.screen.ChatsFragment;
import com.hlogi.wappclone.databinding.ActivityQrCodeBinding;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModel;
import com.hlogi.wappclone.settings.viewmodel.SettingsViewModelFactory;
import com.hlogi.wappclone.status.StatusFragment;
import com.hlogi.wappclone.ui.main.MainFragment;

import java.util.ArrayList;

public class QRcodeActivity extends AppCompatActivity {

    private ActivityQrCodeBinding binding;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SettingsViewModelFactory viewModelFactory = SettingsViewModelFactory.createFactory(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);

        setupViewPager();

    }

    private void setupViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.viewPager.setCurrentItem(0);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.tabs.getTabAt(0).setText("My Code");
        binding.tabs.getTabAt(1).setText("Scan Code");
    }

    static class SectionsPagerAdapter extends FragmentPagerAdapter {

        static final int NUMBER_OF_FRAGMENTS = 2;
        ArrayList<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            mFragmentList.add(new MyCodeFragment());
            mFragmentList.add(new ScanCodeFragment());
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return NUMBER_OF_FRAGMENTS;
        }
    }

}
