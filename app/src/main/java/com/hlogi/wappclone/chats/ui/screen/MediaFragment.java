package com.hlogi.wappclone.chats.ui.screen;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModel;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModelFactory;
import com.hlogi.wappclone.databinding.FragmentMediaBinding;

public class MediaFragment extends Fragment {

    static final String ARG_NUMBER = "number";
    static final String ARG_MESSAGE_ID = "message_id";
    static final String ARG_CHAT_CONVO = "chat_convo";

    private FragmentMediaBinding binding;
    private MessagesViewModel messagesViewModel;
    private String number;
    private String message_id;
    private String chat_convo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessagesViewModelFactory messagesViewModelFactory = MessagesViewModelFactory.createFactory(requireActivity());
        messagesViewModel = new ViewModelProvider(requireActivity(), messagesViewModelFactory).get(MessagesViewModel.class);

        if (getArguments() != null) {
            number = getArguments().getString(ARG_NUMBER);
            message_id = getArguments().getString(ARG_MESSAGE_ID);
            chat_convo = getArguments().getString(ARG_CHAT_CONVO);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMediaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_media_star:

                    return true;
                case R.id.menu_media_share:

                    return true;
            }
            return false;
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });

        messagesViewModel.getMessageLiveData(chat_convo, message_id).observe(getViewLifecycleOwner(), message -> {

            if (message != null) {
                switch (message.getMedia_type()) {
                    case Message.MEDIA_PHOTO:
                        binding.videoView.setVisibility(View.GONE);
                        binding.videoPlay.setVisibility(View.GONE);
                        binding.imageView.setVisibility(View.VISIBLE);
                        binding.imageView.setImageURI(Uri.parse(message.getMedia_path()));
                        break;
                    case Message.MEDIA_VIDEO:
                        binding.imageView.setVisibility(View.GONE);
                        binding.videoView.setVisibility(View.VISIBLE);
                        binding.videoView.setVideoURI(Uri.parse(message.getMedia_path()));
                        binding.videoView.setOnPreparedListener(mp -> {
//                            duration = mp.getDuration();
                            binding.videoView.requestFocus();
                            mp.setScreenOnWhilePlaying(true);
                            mp.start();
                            mp.pause();
                        });
                        binding.videoView.setOnCompletionListener(mp -> {
                            binding.videoPlay.setVisibility(View.VISIBLE);
                        });
                        binding.videoPlay.setVisibility(View.VISIBLE);
                        binding.videoPlay.setOnClickListener(v -> {
                            if (binding.videoView.isPlaying()) {
                                binding.videoView.pause();
                                binding.videoPlay.setVisibility(View.VISIBLE);
                            } else {
                                binding.videoView.start();
                                binding.videoPlay.setVisibility(View.INVISIBLE);
                            }
                        });

                        binding.videoView.setOnTouchListener((v, event) -> {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (binding.videoView.isPlaying()) {
                                    binding.videoView.pause();
                                    binding.videoPlay.setVisibility(View.VISIBLE);
                                } else {
                                    binding.videoView.start();
                                    binding.videoPlay.setVisibility(View.INVISIBLE);
                                }
                            }
                            return true;
                        });

                        break;
                }

                binding.mediaCaption.setText(message.getMedia_caption());
            }

        });

        if (number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
            binding.displayName.setText(getResources().getString(R.string.you));
        } else {

            messagesViewModel.getContact(number).observe(getViewLifecycleOwner(), contact -> {
                if (contact != null) {
                    if (contact.getDisplay_name() == null || contact.getDisplay_name().equals("null")) {
                        binding.displayName.setText(contact.getNumber());
                    } else {
                        binding.displayName.setText(contact.getDisplay_name());
                    }

                }
            });

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            binding.videoPlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }


}
