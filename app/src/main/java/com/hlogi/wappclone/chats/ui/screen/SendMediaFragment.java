package com.hlogi.wappclone.chats.ui.screen;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.chats.data.model.MediaMetadata;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModel;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModelFactory;
import com.hlogi.wappclone.databinding.FragmentSendImageVideoBinding;
import com.hlogi.wappclone.util.FileUtil;

public class SendMediaFragment extends Fragment {

    static final String ARG_KEY_URI = "uri";
    static final String ARG_KEY_MEDIA_TYPE = "media_type";
    static final String ARG_IMAGE_MEDIA_TYPE = "image";
    static final String ARG_VIDEO_MEDIA_TYPE = "video";
    static final String ARG_NUMBER = "number";
    static final String ARG_KEY_MESSAGE_CAPTION = "message_caption";
    static final String ARG_KEY_CAPTURED = "captured";

    private FragmentSendImageVideoBinding binding;
    private MessagesViewModel messagesViewModel;
    private Uri uri;
    private String number;
    private String media_caption;
    private Long size = 0L;
    private int duration = 0;
    private String TYPE_MEDIA;
    private String TYPE_MEDIA_MESSAGE;
    private Boolean captured = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessagesViewModelFactory messagesViewModelFactory = MessagesViewModelFactory.createFactory(requireActivity());
        messagesViewModel = new ViewModelProvider(requireActivity(), messagesViewModelFactory).get(MessagesViewModel.class);

        if (getArguments() != null) {
            TYPE_MEDIA = getArguments().getString(ARG_KEY_MEDIA_TYPE);
            number = getArguments().getString(ARG_NUMBER);
            media_caption = getArguments().getString(ARG_KEY_MESSAGE_CAPTION);
            captured = getArguments().getBoolean(ARG_KEY_CAPTURED);
            String uriString = getArguments().getString(ARG_KEY_URI);
            uri = Uri.parse(uriString);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSendImageVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FileUtil.deleteFile(uri.getPath());
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                });

        binding.back.setOnClickListener(v -> {
            FileUtil.deleteFile(uri.getPath());
            Navigation.findNavController(requireView()).navigateUp();
        });

        binding.sendBtn1.setOnClickListener(v -> {
            sendMedia();
            if (captured)
                FileUtil.scanMedia(uri.getPath(), requireActivity().getApplicationContext());
            Navigation.findNavController(requireView()).navigateUp();
        });

        binding.sendBtn2.setOnClickListener(v -> {
            sendMedia();
            if (captured)
                FileUtil.scanMedia(uri.getPath(), requireActivity().getApplicationContext());
            Navigation.findNavController(requireView()).navigateUp();
        });

        switch (TYPE_MEDIA) {
            case ARG_IMAGE_MEDIA_TYPE:
                binding.videoView.setVisibility(View.GONE);
                binding.videoPlay.setVisibility(View.GONE);
                binding.imageView.setVisibility(View.VISIBLE);
                binding.imageView.setImageURI(uri);
                break;
            case ARG_VIDEO_MEDIA_TYPE:
                binding.imageView.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.VISIBLE);
                binding.videoView.setVideoURI(uri);
                binding.videoView.setOnPreparedListener(mp -> {
                    duration = mp.getDuration();
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

        binding.mediaCaption.setText(media_caption);

        messagesViewModel.getContact(number).observe(getViewLifecycleOwner(), contact -> {
            if (contact != null) {
                binding.setContact(contact);

                if (contact.getDisplay_name() == null || contact.getDisplay_name().equals("null")) {
                    binding.displayName.setText(contact.getNumber());
                } else {
                    binding.displayName.setText(contact.getDisplay_name());
                }

            }
        });

    }

    private void sendMedia() {
        String message_type_media = "";
        switch (TYPE_MEDIA) {
            case ARG_IMAGE_MEDIA_TYPE:
                message_type_media = Message.MEDIA_PHOTO;
                TYPE_MEDIA_MESSAGE = "Photo";
                break;
            case ARG_VIDEO_MEDIA_TYPE:
                message_type_media = Message.MEDIA_VIDEO;
                TYPE_MEDIA_MESSAGE = "Video";
                break;
        }

        if (!binding.mediaCaption.getEditableText().toString().isEmpty()) {
            TYPE_MEDIA_MESSAGE = binding.mediaCaption.getEditableText().toString();
        }

        messagesViewModel.sendMediaMessage(new Message(
                "",
                messagesViewModel.getCurrentChatConvoID().getValue(),
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                number,
                Timestamp.now().toDate().getTime(),
                Message.TYPE_MEDIA,
                TYPE_MEDIA_MESSAGE,
                message_type_media,
                null,
                uri.toString(),
                binding.mediaCaption.getEditableText().toString(),
                new MediaMetadata(size, duration),
                false,
                false,
                0L,
                false,
                0L
        ), requireActivity().getApplicationContext());
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
