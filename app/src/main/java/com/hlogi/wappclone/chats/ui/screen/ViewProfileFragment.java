package com.hlogi.wappclone.chats.ui.screen;

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

import com.google.android.material.appbar.AppBarLayout;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.adapters.OtherNumbersAdapter;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModel;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModelFactory;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.FragmentViewProfileBinding;
import com.hlogi.wappclone.util.AppBarStateChangeListener;

import java.util.List;

public class ViewProfileFragment extends Fragment {

    static final String TAG = ViewProfileFragment.class.getSimpleName();
    static final String ARG_NUMBER = "number";
    static final String ARG_CONVO_ID = "convo_id";
    static final String ARG_PROFILE_PHOTO = "profile_photo";
    static final String ARG_DISPLAY_NAME = "display_name";
    private FragmentViewProfileBinding binding;
    private ContactsViewModel mViewModel;
    private String number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewProfileBinding.inflate(getLayoutInflater());

        if (getArguments() != null) {
            number = getArguments().getString(ARG_NUMBER);
        }

        ContactsViewModelFactory viewModelFactory = ContactsViewModelFactory.createFactory(requireActivity());
        mViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(ContactsViewModel.class);

        mViewModel.setCurrentContactNumber(number);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, Integer state) {
                switch (state) {
                    case State.IDLE:

                        break;
                    case State.EXPANDED:

                        break;
                    case State.COLLAPSED:

                        break;
                }
            }
        });

        binding.setMedia(false);
        binding.setStarred(false);

        mViewModel.getCurrentContact().observe(getViewLifecycleOwner(), contact -> {
            if (contact != null) {
                if (contact.getNumber().equals(number)){
                    binding.setContact(contact);
                    if (contact.getDisplay_name().equals("null")) {
                        binding.viewProfileToolbar.setTitle(contact.getNumber());
                    } else {
                        binding.viewProfileToolbar.setTitle(contact.getDisplay_name());
                    }
                }
            }


        });

        mViewModel.getCurrentContactOnlineStatus().observe(getViewLifecycleOwner(), online -> {
            if (online != null) {
                if (online.isSuccessful()) {
                    binding.setOnline(online.data().getOnline());
                    binding.setLastSeen(online.data().getLast_seen());
                    binding.setTyping(online.data().getTyping());
                    binding.setRecording(online.data().getRecording());
                } else {
                    binding.setOnline(false);
                    binding.setLastSeen(0L);
                    binding.setTyping(false);
                    binding.setRecording(false);
                }
            } else {
                binding.setOnline(false);
                binding.setLastSeen(0L);
                binding.setTyping(false);
                binding.setRecording(false);
            }
        });

        OtherNumbersAdapter adapter = new OtherNumbersAdapter();

        binding.otherPhonesRecycleview.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.otherPhonesRecycleview.setAdapter(adapter);

        mViewModel.getOtherCurrentContactNumbers().observe(getViewLifecycleOwner(), contacts -> {
            if(contacts != null) {
                if (contacts.size() > 1) {
                    binding.setOtherNumbers(true);
                    List<Contact> list = contacts;
                    for (int i = 0; i < list.size(); i++) {
                        if (contacts.get(i).getNumber().equals(mViewModel.getCurrentContactNumber().getValue())){
                            list.remove(i);
                        }
                    }
                    adapter.submitList(list);
                } else {
                    binding.setOtherNumbers(false);
                }
            }
        });

        adapter.setOnItemAction(new OtherNumbersAdapter.ItemAction() {
            @Override
            public void onClickMessage(String number) {
                Bundle bundle = new Bundle();
                bundle.putString(ARG_NUMBER, number);
                bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
                Navigation.findNavController(requireView()).navigate(R.id.action_viewProfileFragment_to_messagesFragment, bundle);
            }

            @Override
            public void onClickVoiceCall(String number) {

            }

            @Override
            public void onClickVideoCall(String number) {

            }
        });

        binding.viewProfileToolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());

        binding.profilePhotoIv.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_PROFILE_PHOTO, mViewModel.getCurrentContact().getValue().getProfile_photo_url());
            bundle.putString(ARG_DISPLAY_NAME, mViewModel.getCurrentContact().getValue().getDisplay_name());
            Navigation.findNavController(requireView()).navigate(R.id.action_viewProfileFragment_to_viewProfilePictureFragment, bundle);
        });

        binding.phoneNumberTextview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
            Navigation.findNavController(requireView()).navigate(R.id.action_viewProfileFragment_to_messagesFragment);
        });

        binding.phoneNumberTypeTextview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
            Navigation.findNavController(requireView()).navigate(R.id.action_viewProfileFragment_to_messagesFragment);
        });

        binding.phoneNoMsgImgView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
            Navigation.findNavController(requireView()).navigate(R.id.action_viewProfileFragment_to_messagesFragment);
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }
}
