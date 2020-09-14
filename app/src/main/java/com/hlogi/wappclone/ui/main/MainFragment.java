package com.hlogi.wappclone.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.calls.CallsFragment;
import com.hlogi.wappclone.camera.CameraFragment;
import com.hlogi.wappclone.chats.ui.screen.ChatsFragment;
import com.hlogi.wappclone.databinding.FragmentMainBinding;
import com.hlogi.wappclone.qr.QRcodeActivity;
import com.hlogi.wappclone.settings.SettingsActivity;
import com.hlogi.wappclone.starred.StarredMessagesFragment;
import com.hlogi.wappclone.status.StatusFragment;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    static final String TAG = "MainActivity";
    static final String ARG_SIDE_KEY = "side";
    static final String ARG_CHATS_SIDE_VALUE = "Chats";
    static final String ARG_CALLS_SIDE_VALUE = "Calls";
    static final String ARG_CONVO_ID = "convo_id";
    private FragmentMainBinding binding;

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.d(TAG, "onPageScrolled: Position: " + position + ". Position Offset: " + positionOffset + ". Position Offset Pixels: " + positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    binding.fab.hide();
                    binding.fab1.hide();
//                    binding.toolbar.inflateMenu(R.menu.menu_main_activity_chats);
                    break;
                case 1:
                    binding.fab.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_comment_white_24dp));
                    binding.fab.show();
                    binding.fab1.hide();
//                    binding.toolbar.inflateMenu(R.menu.menu_main_activity_chats);
                    break;
                case 2:
                    binding.fab.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_camera_white_24dp));
                    binding.fab.show();
                    binding.fab1.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_edit_white_24dp));
                    binding.fab1.show();
//                    binding.toolbar.inflateMenu(R.menu.menu_main_activity_status);
                    break;
                case 3:
                    binding.fab.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_add_ic_call_white_24dp));
                    binding.fab.show();
                    binding.fab1.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_photo_camera_grey_24dp));
                    binding.fab1.show();
//                    binding.toolbar.inflateMenu(R.menu.menu_main_activity_calls);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged: State: " + state);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.viewPager.getCurrentItem() == 1) {
                    requireActivity().finish();
                } else {
                    binding.viewPager.setCurrentItem(1);
                }
            }
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_main_settings:
                    startActivity(new Intent(requireActivity(), SettingsActivity.class));
                    return true;
                case R.id.menu_main_starred_msgs:
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_CONVO_ID, StarredMessagesFragment.ALL_STARRED_MESSAGES);
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_starredMessagesFragment, bundle);
                    return true;
                case R.id.menu_main_wapp_web:
                    requireActivity().startActivity(new Intent(requireActivity(), QRcodeActivity.class));
                    return true;
            }

            return false;
        });

        setupViewPager();

        binding.fab.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            switch (binding.viewPager.getCurrentItem()) {
                case 1:
                    bundle.putString(ARG_SIDE_KEY, ARG_CHATS_SIDE_VALUE);
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_contactsFragment, bundle);
                    break;
                case 2:
                    //Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_contactsFragment);
                    break;
                case 3:
                    bundle.putString(ARG_SIDE_KEY, ARG_CALLS_SIDE_VALUE);
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_contactsFragment, bundle);
                    break;
            }

        });

        binding.fab1.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            switch (binding.viewPager.getCurrentItem()) {
                case 2:
                    //Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_contactsFragment);
                    break;
                case 3:
                    bundle.putString(ARG_SIDE_KEY, ARG_CALLS_SIDE_VALUE);
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_contactsFragment, bundle);
                    break;
            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();
        binding.viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.viewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    void setupViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.viewPager.setCurrentItem(1);
        binding.fab1.hide();
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.tabs.getTabAt(0).setIcon(requireActivity().getDrawable(R.drawable.ic_photo_camera_grey_24dp));
        binding.tabs.getTabAt(1).setText("Chats");
        binding.tabs.getTabAt(2).setText("Status");
        binding.tabs.getTabAt(3).setText("Calls");

    }

    static class SectionsPagerAdapter extends FragmentPagerAdapter {

        static final int NUMBER_OF_FRAGMENTS = 4;
        ArrayList<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            mFragmentList.add(new CameraFragment());
            mFragmentList.add(new ChatsFragment());
            mFragmentList.add(new StatusFragment());
            mFragmentList.add(new CallsFragment());
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
