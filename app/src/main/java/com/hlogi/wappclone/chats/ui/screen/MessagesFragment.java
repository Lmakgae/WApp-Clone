package com.hlogi.wappclone.chats.ui.screen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.adapters.MessagesAdapter;
import com.hlogi.wappclone.chats.data.model.MediaMetadata;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.ui.dialog.TakePhotoVideoBottomSheetDialog;
import com.hlogi.wappclone.chats.util.SendMessageAudioView;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModel;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModelFactory;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModel;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModelFactory;
import com.hlogi.wappclone.databinding.FragmentMessagesBinding;
import com.hlogi.wappclone.util.FileUtil;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MessagesFragment extends Fragment {

    static final String TAG = MessagesFragment.class.getSimpleName();
    static final String ARG_NUMBER = "number";
    static final String ARG_CONVO_ID = "convo_id";
    static final String ARG_UNREAD = "unread";
    static final String ARG_UNREAD_MESSAGE_ID = "unread_id";
    static final String ARG_KEY_URI = "uri";
    static final String ARG_KEY_MEDIA_TYPE = "media_type";
    static final String ARG_IMAGE_MEDIA_TYPE = "image";
    static final String ARG_VIDEO_MEDIA_TYPE = "video";
    static final String ARG_KEY_MESSAGE_CAPTION = "message_caption";
    static final String ARG_KEY_CAPTURED = "captured";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int REQUEST_PICK_IMAGE = 3;
    static final int REQUEST_PICK_VIDEO = 4;
    public static String selected_number = "";
    public static boolean fragment_running = false;
    public static boolean movedToEnd = false;
    private boolean isAttachLayoutOpened = false;

    private FragmentMessagesBinding binding;
    private ContactsViewModel contactsViewModel;
    private MessagesViewModel messagesViewModel;
    private SendMessageAudioView sendMessageAudioView;

    private Boolean opened = false;
    private String convo_id;
    private String number;
    private Integer unread;
    private String unread_id;

    private String file;
    private MediaRecorder mediaRecorder = null;
    private Long size= 0L;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movedToEnd = false;

        ContactsViewModelFactory contactsViewModelFactory = ContactsViewModelFactory.createFactory(requireActivity());
        MessagesViewModelFactory messagesViewModelFactory = MessagesViewModelFactory.createFactory(requireActivity());
        contactsViewModel = new ViewModelProvider(requireActivity(), contactsViewModelFactory).get(ContactsViewModel.class);
        messagesViewModel = new ViewModelProvider(requireActivity(), messagesViewModelFactory).get(MessagesViewModel.class);

        if (getArguments() != null) {
            convo_id = getArguments().getString(ARG_CONVO_ID);
            number = getArguments().getString(ARG_NUMBER);
            unread = getArguments().getInt(ARG_UNREAD);
            unread_id = getArguments().getString(ARG_UNREAD_MESSAGE_ID);
            selected_number = number;
            assert number != null;
            if (convo_id.equals(getString(R.string.null_t)) || convo_id == null) {
                String receiver = number;
                String sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                String chat_convo_id;
                assert sender != null;
                if (receiver.compareTo(sender) > 0) {
                    chat_convo_id = receiver + sender;
                } else {
                    chat_convo_id = sender + receiver;
                }
                convo_id = chat_convo_id;
                messagesViewModel.setCurrentChatConvoID(chat_convo_id);
            } else {
                messagesViewModel.setCurrentChatConvoID(convo_id);
            }
            contactsViewModel.setCurrentContactNumber(number);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        sendMessageAudioView = new SendMessageAudioView();
        sendMessageAudioView.initView(binding);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragment_running = true;
        if (sendMessageAudioView.isTyping())
            contactsViewModel.setUserTypingStatus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragment_running = false;
        if (sendMessageAudioView.isTyping())
            contactsViewModel.setUserTypingStatus(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setUserTyping(false);

        contactsViewModel.getCurrentContact().observe(getViewLifecycleOwner(), contact -> {
            if (contact != null) {
                if (contact.getNumber().equals(number)) {
                    binding.setContact(contact);
                    if (contact.getDisplay_name().equals("null")) {
                        binding.onlineDisplayNameTv.setText(contact.getNumber());
                        binding.offlineDisplayNameTv.setText(contact.getNumber());
                    } else {
                        binding.offlineDisplayNameTv.setText(contact.getDisplay_name());
                        binding.onlineDisplayNameTv.setText(contact.getDisplay_name());
                    }
                }
            }

        });

        contactsViewModel.getCurrentContactOnlineStatus().observe(getViewLifecycleOwner(), (onlineStatus -> {
            if (onlineStatus != null) {
                if (onlineStatus.isSuccessful()) {
                    binding.setOnline(onlineStatus.data().getOnline());
                    binding.setLastSeen(onlineStatus.data().getLast_seen());
                    binding.setTyping(onlineStatus.data().getTyping());
                    binding.setRecording(onlineStatus.data().getRecording());
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

        }));

        binding.navBackIv.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.proPicIv.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());

        binding.callIv.setOnClickListener(v -> {});
        binding.videoCallIv.setOnClickListener(v -> {
            messagesViewModel.deleteAll();
        });
        binding.menuIv.setOnClickListener(v -> { });

        binding.offlineDisplayNameTv.setOnClickListener(v -> navigateToViewProfile());
        binding.onlineDisplayNameTv.setOnClickListener(v -> navigateToViewProfile());
        binding.onlineStatusTv.setOnClickListener(v -> navigateToViewProfile());

        MessagesAdapter adapter = new MessagesAdapter(
                this.number, this.unread , this.unread_id, getViewLifecycleOwner());

        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.recyclerView.setAdapter(adapter);

        messagesViewModel.getMessagesList().observe(getViewLifecycleOwner(), list -> {
            if (!list.isEmpty()) {
                if (list.get(0).getConversation_id().equals(convo_id)) {
                    adapter.submitList(list);
                    if (!movedToEnd) {
                        if (adapter.getItemCount() > 0) {
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            movedToEnd = true;
                        }
                    }
                }
            }
        });

        messagesViewModel.getUnreadMessagesList().observe(getViewLifecycleOwner(), list ->  {
            if (!list.isEmpty()) {
                if (list.get(0).getConversation_id().equals(convo_id)) {
                    if (adapter.getUnread_id() == null || adapter.getUnread_id().equals("null")) {
                        adapter.setUnreadNotification(list.size(), list.get(0).getMessage_id());
                        adapter.notifyItemChanged(adapter.getItemCount() - list.size() + 1);
                    } else {
                        if (opened) {
                            adapter.setUnreadNotification(adapter.getUnread() + list.size(), adapter.getUnread_id());
                            adapter.notifyItemChanged(adapter.getItemCount() - adapter.getUnread() + 1);
                        }
                    }
                    opened = true;
                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    messagesViewModel.sendMessageReceipts(list, requireActivity().getApplicationContext());
                }
            }
        });

        adapter.setOnItemAction(new MessagesAdapter.ItemAction() {
            @Override
            public void onClick(Message message) {

            }

            @Override
            public void onLongClick(Message message) {

            }

            @Override
            public void onViewMedia(String message_id, String chat_convo, String number) {
                Bundle bundle = new Bundle();
                bundle.putString(MediaFragment.ARG_NUMBER, number);
                bundle.putString(MediaFragment.ARG_MESSAGE_ID, message_id);
                bundle.putString(MediaFragment.ARG_CHAT_CONVO, chat_convo);
                Navigation.findNavController(requireView()).navigate(R.id.action_messagesFragment_to_mediaFragment, bundle);
            }

            @Override
            public void onReuploadMedia(Message message) {
                messagesViewModel.reuploadMediaWork(message, requireActivity().getApplicationContext());
            }

            @Override
            public void onRedownloadMedia(Message message) {
                messagesViewModel.redownloadMediaWork(message, requireActivity().getApplicationContext());
            }

            @Override
            public void onCancelMediaWork(Message message) {
                messagesViewModel.cancelMediaWork(message, requireActivity().getApplicationContext());
            }
        });

        binding.attachIc.setOnClickListener(v -> {
            if (binding.attachmentLayout.getVisibility() == View.VISIBLE)
                binding.attachmentLayout.setVisibility(View.GONE);
            else
                binding.attachmentLayout.setVisibility(View.VISIBLE);
        });
        binding.emojiIc.setOnClickListener(v -> {

        });

        binding.cameraIc.setOnClickListener(v -> {
            TakePhotoVideoBottomSheetDialog dialog = new TakePhotoVideoBottomSheetDialog();
            dialog.show(getChildFragmentManager(), TakePhotoVideoBottomSheetDialog.class.getSimpleName());
            dialog.setListener(new TakePhotoVideoBottomSheetDialog.DialogClickListener() {
                @Override
                public void onDialogPhotoClick() {
                    dispatchTakePictureIntent();
                }

                @Override
                public void onDialogVideoClick() {
                    dispatchTakeVideoIntent();
                }
            });
        });

        binding.attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.attachmentLayout.setVisibility(View.GONE);
                dispatchGalleryImagePickIntent();
            }
        });

        binding.attachVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.attachmentLayout.setVisibility(View.GONE);
                dispatchGalleryVideoPickIntent();
            }
        });

        sendMessageAudioView.setTypingListener(new SendMessageAudioView.TypingListener() {
            @Override
            public void onTypingStarted() {
                if (!sendMessageAudioView.isTyping())
                    contactsViewModel.setUserTypingStatus(true);
            }

            @Override
            public void onStillTyping() {
                if (!sendMessageAudioView.isTyping())
                    contactsViewModel.setUserTypingStatus(true);
            }

            @Override
            public void onTypingStopped() {
                if (sendMessageAudioView.isTyping())
                    contactsViewModel.setUserTypingStatus(false);
            }

            @Override
            public void onSendMessage() {
                //Sending Text Message
                if (!binding.messageEditText.getText().toString().isEmpty()) {
                    movedToEnd = false;
                    messagesViewModel.sendMessage(new Message(
                            "",
                            messagesViewModel.getCurrentChatConvoID().getValue(),
                            FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                            number,
                            Timestamp.now().toDate().getTime(),
                            Message.TYPE_TEXT,
                            binding.messageEditText.getText().toString(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            false,
                            false,
                            0L,
                            false,
                            0L
                    ), requireActivity().getApplicationContext());
                }
                binding.messageEditText.setText("");
            }
        });

        sendMessageAudioView.setRecordingListener(new SendMessageAudioView.RecordingListener() {
            @Override
            public void onRecordingStarted() {
                MediaPlayer mp = MediaPlayer.create(requireActivity().getApplicationContext(), R.raw.recording_started_sound);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                });
                mp.start();

                File vnFile = null;

                try {
                    vnFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_VOICE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert vnFile != null;
                file = vnFile.getAbsolutePath();

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                mediaRecorder.setOutputFile(file);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onViewCreated: prepare() failed", e);
                }

                mediaRecorder.start();
                vnFile = null;
                contactsViewModel.setUserRecordingStatus(true);
            }

            @Override
            public void onRecordingLocked() {

            }

            @Override
            public void onRecordingCompleted() {
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }

                MediaPlayer mp = MediaPlayer.create(requireActivity().getApplicationContext(), R.raw.recording_stopped_sound);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                });
                mp.start();

                movedToEnd = false;

                messagesViewModel.sendMediaMessage(new Message(
                        "",
                        messagesViewModel.getCurrentChatConvoID().getValue(),
                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                        number,
                        Timestamp.now().toDate().getTime(),
                        Message.TYPE_MEDIA,
                        "Voice message",
                        Message.MEDIA_VOICE_NOTE,
                        null,
                        file,
                        null,
                        new MediaMetadata(size, sendMessageAudioView.getAudioTotalTime()),
                        false,
                        false,
                        0L,
                        false,
                        0L
                ), requireActivity().getApplicationContext());
                contactsViewModel.setUserRecordingStatus(false);
            }

            @Override
            public void onRecordingCanceled() {
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    FileUtil.deleteFile(file);
                }
                contactsViewModel.setUserRecordingStatus(false);
            }

            @Override
            public void onRecordingCanceledQuickly() {
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    FileUtil.deleteFile(file);
                }
                contactsViewModel.setUserRecordingStatus(false);
                //TODO: Show the layout for canceling the recording too quickly
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (sendMessageAudioView != null)
            sendMessageAudioView.onDetach();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_IMAGE){
            assert data != null;
            Uri imageUri = data.getData();
            Bundle bundle = new Bundle();
            assert imageUri != null;
            bundle.putString(ARG_KEY_URI, imageUri.toString());
            bundle.putString(ARG_KEY_MEDIA_TYPE, ARG_IMAGE_MEDIA_TYPE);
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_KEY_MESSAGE_CAPTION, binding.messageEditText.getEditableText().toString());
            bundle.putBoolean(ARG_KEY_CAPTURED, false);
            Navigation.findNavController(requireView()).navigate(R.id.action_messagesFragment_to_sendMediaFragment, bundle);
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_VIDEO){
            assert data != null;
            Uri videoUri = data.getData();
            Bundle bundle = new Bundle();
            assert videoUri != null;
            bundle.putString(ARG_KEY_URI, videoUri.toString());
            bundle.putString(ARG_KEY_MEDIA_TYPE, ARG_VIDEO_MEDIA_TYPE);
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_KEY_MESSAGE_CAPTION, binding.messageEditText.getEditableText().toString());
            bundle.putBoolean(ARG_KEY_CAPTURED, false);
            Navigation.findNavController(requireView()).navigate(R.id.action_messagesFragment_to_sendMediaFragment, bundle);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_KEY_URI, file);
                    bundle.putString(ARG_KEY_MEDIA_TYPE, ARG_IMAGE_MEDIA_TYPE);
                    bundle.putString(ARG_NUMBER, number);
                    bundle.putString(ARG_KEY_MESSAGE_CAPTION, binding.messageEditText.getEditableText().toString());
                    bundle.putBoolean(ARG_KEY_CAPTURED, true);
                    Navigation.findNavController(requireView()).navigate(R.id.action_messagesFragment_to_sendMediaFragment, bundle);
                    break;
                case RESULT_CANCELED:
                    FileUtil.deleteFile(file);
                    break;
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bundle bundle = new Bundle();
                    bundle.putString(ARG_KEY_URI, file);
                    bundle.putString(ARG_KEY_MEDIA_TYPE, ARG_VIDEO_MEDIA_TYPE);
                    bundle.putString(ARG_NUMBER, number);
                    bundle.putString(ARG_KEY_MESSAGE_CAPTION, binding.messageEditText.getEditableText().toString());
                    bundle.putBoolean(ARG_KEY_CAPTURED, true);
                    Navigation.findNavController(requireView()).navigate(R.id.action_messagesFragment_to_sendMediaFragment, bundle);
                    break;
                case RESULT_CANCELED:
                    FileUtil.deleteFile(file);
                    break;
            }
        }
    }

    private void dispatchGalleryImagePickIntent() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if (gallery.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(gallery, REQUEST_PICK_IMAGE);
        }
    }

    private void dispatchGalleryVideoPickIntent() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        if (gallery.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(gallery, REQUEST_PICK_VIDEO);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_IMAGE);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                file = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(requireActivity().getPackageManager()) != null) {

            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = FileUtil.createMediaFile(FileUtil.FILE_TYPE_VIDEO);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                Uri videoURI = Uri.fromFile(videoFile);
                file = videoURI.toString();
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }

        }
    }

    private void navigateToViewProfile() {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_NUMBER, number);
        Navigation.findNavController(requireView()).navigate(R.id.action_messagesFragment_to_viewProfileFragment, bundle);
    }

}