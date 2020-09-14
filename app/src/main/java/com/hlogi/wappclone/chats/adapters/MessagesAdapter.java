package com.hlogi.wappclone.chats.adapters;

import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedListAdapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.work.MediaMessageWork;
import com.hlogi.wappclone.databinding.MessageListItemBinding;
import com.hlogi.wappclone.util.BindingAdapterUtil;
import com.hlogi.wappclone.util.TimestampUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MessagesAdapter extends PagedListAdapter<Message, MessagesAdapter.ViewHolder> {

    public enum LAYOUT_SIDE {
        LEFT,
        RIGHT
    }

    public enum MEDIA_STATUS {
        UPLOADING,
        DOWNLOADING,
        CANCELED,
        AVAILABLE
    }

    private MessagesAdapter.ItemAction mItemAction;
    private LifecycleOwner lifecycleOwner;
    private final String number;
    private Integer unread;
    private String unread_id;

    private static MediaPlayer mediaPlayer = null;
    private static String vn_playing_id;
    private static Handler handler;
    private static Timer audioTimer;
    private static TimerTask timerTask = null;

    public MessagesAdapter(String number, Integer unread, String unread_id , LifecycleOwner lifecycleOwner) {
        super(Message.DIFF_CALLBACK);
        this.number = number;
        this.unread = unread;
        this.unread_id = unread_id;
        this.lifecycleOwner = lifecycleOwner;
        handler = new Handler(Looper.getMainLooper());
    }

    public interface ItemAction {
        void onClick(Message message);
        void onLongClick(Message message);
        void onViewMedia(String message_id, String chat_convo, String number);
        void onReuploadMedia(Message message);
        void onRedownloadMedia(Message message);
        void onCancelMediaWork(Message message);
    }

    public void setOnItemAction( ItemAction itemAction) { this.mItemAction = itemAction; }

    public void setUnreadNotification(Integer unread, String unread_id) {
        this.unread = unread;
        this.unread_id = unread_id;
    }

    public Integer getUnread() {
        return unread;
    }

    public String getUnread_id() {
        return unread_id;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageListItemBinding binding = MessageListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false );
        return new MessagesAdapter.ViewHolder(binding, this.number);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        holder.setLifecycleOwner(lifecycleOwner);
        holder.showUnreadNotification(this.unread, this.unread_id);
        if ((position - 1) < 0) {
            holder.showDateNotification(true);
        } else {
            if (TimestampUtil.differentDays(getItem(position - 1).getTime_stamp(), getItem(position).getTime_stamp())) {
                holder.showDateNotification(true);
            } else {
                holder.showDateNotification(false);
            }
        }
        holder.setOnItemClickListener(mItemAction);
        holder.bindTo(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private MessageListItemBinding binding;
        private Message message;
        private Boolean showDateNotification;
        private final String number;
        private ItemAction itemAction;
        private Integer unread;
        private String unread_id;
        private LifecycleOwner lifecycleOwner;
        private MEDIA_STATUS media_status;
        private int vn_play_time = 0;

        ViewHolder(@NonNull MessageListItemBinding binding, String number) {
            super(binding.getRoot());
            this.binding = binding;
            this.number = number;
        }

        void bindTo(Message message) {
            this.message = message;

            if (this.unread > 0) {
                if (message.getMessage_id().equals(this.unread_id)) {
                    binding.unreadMessagesNotiLayout.setVisibility(View.VISIBLE);
                    binding.unreadMessagesText.setText(
                            binding.unreadMessagesText.getResources().getQuantityString(
                                    R.plurals.number_of_messages, this.unread, this.unread));
                } else {
                    binding.unreadMessagesNotiLayout.setVisibility(View.GONE);
                }
            }

            if (showDateNotification) {
                binding.dateNotificationLayout.setVisibility(View.VISIBLE);
                TimestampUtil.setMessageDateNotificationTimestamp(
                        binding.dateText,
                        message.getTime_stamp()
                );
            } else {
                binding.dateNotificationLayout.setVisibility(View.GONE);
            }

            if (message.getSender().equals(this.number)) {
                binding.textTypeLayout.setVisibility(View.GONE);
                binding.photoVideoLayout.setVisibility(View.GONE);
                binding.vnTypeLayout.setVisibility(View.GONE);
                switch (message.getType()) {
                    case Message.TYPE_TEXT:
                        initTextLayout(binding, message, LAYOUT_SIDE.LEFT);
                        break;
                    case Message.TYPE_MEDIA:
                        switch (message.getMedia_type()) {
                            case Message.MEDIA_PHOTO:
                            case Message.MEDIA_VIDEO:
                                initPhotoVideoLayout(binding, message, LAYOUT_SIDE.LEFT);
                                break;
                            case Message.MEDIA_VOICE_NOTE:
                                initVoiceNoteLayout(binding, message, LAYOUT_SIDE.LEFT);
                                break;
                        }
                        break;

                }

            }
            else if (message.getReceiver().equals(this.number)){
                binding.textTypeLayout.setVisibility(View.GONE);
                binding.photoVideoLayout.setVisibility(View.GONE);
                binding.vnTypeLayout.setVisibility(View.GONE);
                switch (message.getType()) {
                    case Message.TYPE_TEXT:
                        initTextLayout(binding, message, LAYOUT_SIDE.RIGHT);
                        break;
                    case Message.TYPE_MEDIA:
                        switch (message.getMedia_type()) {
                            case Message.MEDIA_PHOTO:
                            case Message.MEDIA_VIDEO:
                                initPhotoVideoLayout(binding, message, LAYOUT_SIDE.RIGHT);
                                break;
                            case Message.MEDIA_VOICE_NOTE:
                                initVoiceNoteLayout(binding, message, LAYOUT_SIDE.RIGHT);
                                break;
                        }
                        break;
                }

            }

        }

        public Message getMessage() {
            return message;
        }

        void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
        }

        void setOnItemClickListener(ItemAction itemAction) {
            this.itemAction = itemAction;
        }

        void showDateNotification(Boolean bool) {
            this.showDateNotification = bool;
        }

        void showUnreadNotification(Integer unread, String unread_id) {
            this.unread = unread;
            this.unread_id = unread_id;
        }

        public void initTextLayout(@NonNull MessageListItemBinding binding, @NonNull Message message, @NonNull LAYOUT_SIDE side) {
            binding.textTypeLayout.setVisibility(View.GONE);
            float density = binding.getRoot().getContext().getApplicationContext().getResources().getDisplayMetrics().density;
            TypedValue typedValue = new TypedValue();

            ConstraintSet set = new ConstraintSet();
            set.clone(binding.textTypeLayout);

            if (side.equals(LAYOUT_SIDE.LEFT)) {
                set.setHorizontalBias(binding.textTypeContainer.getId(), 0.0f);
                binding.textTypeLayout.setPadding((int)(8 * density), 0, (int)(100 * density), 0);
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.incomingChatBubble, typedValue, true);
                binding.textStatus.setVisibility(View.GONE);
            } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                set.setHorizontalBias(binding.textTypeContainer.getId(), 1.00f);
                binding.textTypeLayout.setPadding((int)(100 * density), 0, (int)(8 * density), 0);
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.outgoingChatBubble, typedValue, true);
                BindingAdapterUtil.setSentMessageIcon(
                        binding.textStatus,
                        message.getMessageMetadata().getHasPendingWrites(),
                        message.getDelivered(),
                        message.getRead()
                );
                binding.textStatus.setVisibility(View.VISIBLE);
            }

            binding.textTypeContainer.setBackgroundTintList(ColorStateList.valueOf(typedValue.data));
            set.applyTo(binding.textTypeLayout);

            binding.textTypeLayout.setVisibility(View.VISIBLE);
            BindingAdapterUtil.setMessageTime(
                    binding.textTime,
                    message.getTime_stamp()
            );

            if (message.getStarred()) {
                binding.textStarred.setVisibility(View.VISIBLE);
            } else {
                binding.textStarred.setVisibility(View.GONE);
            }

            binding.textMessage.setText(message.getMessage());
        }

        public void initPhotoVideoLayout(@NonNull MessageListItemBinding binding, Message message, @NonNull LAYOUT_SIDE side){
            float density = binding.getRoot().getContext().getApplicationContext().getResources().getDisplayMetrics().density;
            TypedValue typedValue = new TypedValue();

            if (side.equals(LAYOUT_SIDE.LEFT)) {
                binding.photoVideoLayout.setPadding((int)(8 * density), 0, (int)(100 * density), 0);
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.incomingChatBubble, typedValue, true);
                binding.mediaStatus.setVisibility(View.GONE);
            } else if (side.equals(LAYOUT_SIDE.RIGHT))  {
                binding.photoVideoLayout.setPadding((int)(100 * density), 0, (int)(8 * density), 0);
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.outgoingChatBubble, typedValue, true);
                BindingAdapterUtil.setSentMessageIcon(
                        binding.mediaStatus,
                        message.getMessageMetadata().getHasPendingWrites(),
                        message.getDelivered(),
                        message.getRead()
                );
                binding.mediaStatus.setVisibility(View.VISIBLE);
            }

            binding.photoVideoContainer.setBackgroundTintList(ColorStateList.valueOf(typedValue.data));

            binding.photoVideoLayout.setVisibility(View.VISIBLE);

            BindingAdapterUtil.setMessageTime(
                    binding.mediaTime,
                    message.getTime_stamp()
            );

            if (message.getMedia_url() == null || message.getMedia_url().equals("null")) {
                binding.mediaMiddleContainer.setVisibility(View.GONE);
                if (message.getMedia_type().equals(Message.MEDIA_PHOTO)) {
                    binding.mediaImage.setImageURI(Uri.fromFile(new File(message.getMedia_path())));
                } else if (message.getMedia_type().equals(Message.MEDIA_VIDEO)) {

                }
                media_status = MEDIA_STATUS.AVAILABLE;
            } else {
                WorkManager.getInstance(binding.getRoot().getContext().getApplicationContext())
                        .getWorkInfosForUniqueWorkLiveData(message.getConversation_id() + message.getMessage_id())
                        .observe(lifecycleOwner, workInfos -> {
                            if (!workInfos.isEmpty()){
                                binding.mediaMiddleContainer.setVisibility(View.VISIBLE);
                                if (workInfos.get(0).getState() == WorkInfo.State.ENQUEUED) {
                                    binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_cancel_white_24dp));
                                    binding.mediaProgress.setVisibility(View.VISIBLE);
                                    binding.mediaProgress.setIndeterminate(true);
                                    binding.mediaRetry.setVisibility(View.GONE);

                                    if (side.equals(LAYOUT_SIDE.LEFT)) {
                                        media_status = MEDIA_STATUS.DOWNLOADING;
                                    } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                                        media_status = MEDIA_STATUS.UPLOADING;
                                    }

                                } else if (workInfos.get(0).getState() == WorkInfo.State.RUNNING) {
                                    binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_cancel_white_24dp));
                                    if (binding.mediaProgress.isIndeterminate()) {
                                        binding.mediaProgress.setIndeterminate(false);
                                    }
                                    binding.mediaProgress.setMax(100);
                                    if (binding.mediaProgress.getVisibility() == View.GONE || binding.mediaProgress.getVisibility() == View.INVISIBLE) {
                                        binding.mediaProgress.setVisibility(View.VISIBLE);
                                    }
                                    double progress = workInfos.get(0).getProgress().getDouble(MediaMessageWork.PROGRESS, 0);
                                    binding.mediaProgress.setProgress((int) progress);
                                    binding.mediaRetry.setVisibility(View.GONE);
                                    if (side.equals(LAYOUT_SIDE.LEFT)) {
                                        media_status = MEDIA_STATUS.DOWNLOADING;
                                    } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                                        media_status = MEDIA_STATUS.UPLOADING;
                                    }
                                } else if (workInfos.get(0).getState() == WorkInfo.State.CANCELLED ||
                                        workInfos.get(0).getState() == WorkInfo.State.FAILED) {
                                    if (side.equals(LAYOUT_SIDE.LEFT)) {
                                        binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_download_24));
                                    } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                                        binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_upload_24));
                                    }
                                    binding.mediaProgress.setVisibility(View.GONE);
                                    binding.mediaRetry.setVisibility(View.VISIBLE);
                                    media_status = MEDIA_STATUS.CANCELED;
                                }
                            } else {
                                binding.mediaMiddleContainer.setVisibility(View.GONE);
                            }
                        });

            }

            if (message.getMedia_caption() == null || message.getMedia_caption().equals("null") ||
                    message.getMedia_caption().equals("")) {
                binding.mediaCaption.setVisibility(View.GONE);
                binding.mediaTime.setTextColor(binding.getRoot().getResources().getColor(R.color.colorWhite, null));
                binding.mediaStarred.setForegroundTintList(ColorStateList.valueOf(binding.getRoot().getResources().getColor(R.color.colorWhite, null)));
                if (!message.getRead()) {
                    binding.mediaStatus.setForegroundTintList(ColorStateList.valueOf(binding.getRoot().getResources().getColor(R.color.colorWhite, null)));
                }
            } else {
                binding.mediaCaption.setVisibility(View.VISIBLE);
                binding.mediaCaption.setText(message.getMedia_caption());
            }

            if (message.getMedia_type().equals(Message.MEDIA_VIDEO)) {
                binding.videoIcon.setVisibility(View.VISIBLE);
                BindingAdapterUtil.formatMediaDuration(
                        binding.videoDuration,
                        message.getMedia_metadata().getDuration() / 1000
                );
                binding.videoDuration.setVisibility(View.VISIBLE);
                if (media_status != null && media_status == MEDIA_STATUS.AVAILABLE) {
                    binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_white_24dp));
                    binding.mediaMiddleContainer.setVisibility(View.VISIBLE);
                }
            } else {
                binding.videoIcon.setVisibility(View.GONE);
                binding.videoDuration.setVisibility(View.GONE);
            }

            if (message.getStarred()) {
                binding.mediaStarred.setVisibility(View.VISIBLE);
            } else {
                binding.mediaStarred.setVisibility(View.GONE);
            }

            binding.mediaImage.setOnClickListener(v -> {
                if (media_status != null && media_status == MEDIA_STATUS.AVAILABLE) {
                    itemAction.onViewMedia(message.getMessage_id(), message.getConversation_id(), message.getSender());
                }
            });

            binding.mediaMiddleContainer.setOnClickListener(v -> {
                if (media_status != null) {
                    if (media_status == MEDIA_STATUS.UPLOADING || media_status == MEDIA_STATUS.DOWNLOADING) {
                        itemAction.onCancelMediaWork(message);
                    } else if (media_status == MEDIA_STATUS.CANCELED) {
                        if (side.equals(LAYOUT_SIDE.LEFT)) {
                            itemAction.onRedownloadMedia(message);
                        } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                            itemAction.onReuploadMedia(message);
                        }
                    } else if (media_status == MEDIA_STATUS.AVAILABLE) {
                        itemAction.onViewMedia(message.getMessage_id(), message.getConversation_id(), message.getSender());
                    }
                }
            });
        }

        public void initVoiceNoteLayout(@NonNull MessageListItemBinding binding, @NonNull Message message, @NonNull LAYOUT_SIDE side){
            float density = binding.getRoot().getContext().getApplicationContext().getResources().getDisplayMetrics().density;
            TypedValue typedValue = new TypedValue();

            if (side.equals(LAYOUT_SIDE.LEFT)) {
                binding.vnTypeLayout.setPadding((int)(8 * density), 0, (int)(100 * density), 0);
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.incomingChatBubble, typedValue, true);
                binding.vnSentStatus.setVisibility(View.GONE);

                //TODO: Display the proper profile picture
                BindingAdapterUtil.setImageUrl(
                        binding.vnContactImage,
                        null,
                        binding.getRoot().getResources().getDrawable(R.drawable.ic_person_24dp, null)
                );

            } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                binding.vnTypeLayout.setPadding((int)(100 * density), 0, (int)(8 * density), 0);
                binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.outgoingChatBubble, typedValue, true);
                BindingAdapterUtil.setSentMessageIcon(
                        binding.vnSentStatus,
                        message.getMessageMetadata().getHasPendingWrites(),
                        message.getDelivered(),
                        message.getRead()
                );
                binding.vnSentStatus.setVisibility(View.VISIBLE);
                //TODO: Display the proper profile picture
                BindingAdapterUtil.setImageUrl(
                        binding.vnContactImage,
                        null,
                        binding.getRoot().getResources().getDrawable(R.drawable.ic_person_24dp, null)
                );
            }

            binding.vnTypeContainer.setBackgroundTintList(ColorStateList.valueOf(typedValue.data));
            binding.vnTypeLayout.setVisibility(View.VISIBLE);
            BindingAdapterUtil.setMessageTime(
                    binding.vnTime,
                    message.getTime_stamp()
            );
            BindingAdapterUtil.setVoiceNotePlayedIcon(
                    binding.vnMic,
                    message.getMedia_played()
            );
            BindingAdapterUtil.setVoiceNoteProgressBar(
                    binding.vnProgress,
                    message.getMedia_metadata().getDuration(),
                    message.getMedia_played()
            );
            BindingAdapterUtil.formatMediaDuration(
                    binding.vnDuration,
                    message.getMedia_metadata().getDuration()
            );

            if (message.getStarred()) {
                binding.vnStarred.setVisibility(View.VISIBLE);
            } else {
                binding.vnStarred.setVisibility(View.GONE);
            }

            if (message.getMedia_url() == null || message.getMedia_url().equals("null")) {
                binding.vnProgressStatus.setVisibility(View.GONE);
                binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                media_status = MEDIA_STATUS.AVAILABLE;
                binding.vnProgress.setMax(message.getMedia_metadata().getDuration());
                binding.vnProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            BindingAdapterUtil.formatMediaDuration(
                                    binding.vnDuration,
                                    progress
                            );
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        if (mediaPlayer != null) {
                            if (vn_playing_id.equals(message.getMessage_id())) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                }
                            }
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        vn_play_time = seekBar.getProgress();
                        if (mediaPlayer != null) {
                            if (vn_playing_id.equals(message.getMessage_id())) {
                                if (!mediaPlayer.isPlaying()) {
                                    mediaPlayer.seekTo(vn_play_time * 1000);
                                    mediaPlayer.start();
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_pause_24dp));
                                }
                            }
                        }
                    }
                });
            } else {
                WorkManager.getInstance(binding.getRoot().getContext().getApplicationContext())
                        .getWorkInfosForUniqueWorkLiveData(message.getConversation_id() + message.getMessage_id())
                        .observe(lifecycleOwner, workInfos -> {
                            if (!workInfos.isEmpty()){
                                if (workInfos.get(0).getState() == WorkInfo.State.ENQUEUED) {
                                    binding.vnProgressStatus.setVisibility(View.VISIBLE);
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_cancel_white_24dp));
                                    binding.vnProgressStatus.setIndeterminate(true);
                                    if (side.equals(LAYOUT_SIDE.LEFT)) {
                                        media_status = MEDIA_STATUS.DOWNLOADING;
                                    } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                                        media_status = MEDIA_STATUS.UPLOADING;
                                    }
                                } else if (workInfos.get(0).getState() == WorkInfo.State.RUNNING) {
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_cancel_white_24dp));
                                    if (binding.vnProgressStatus.isIndeterminate()) {
                                        binding.vnProgressStatus.setIndeterminate(false);
                                    }
                                    binding.vnProgressStatus.setMax(100);
                                    if (binding.vnProgressStatus.getVisibility() == View.GONE || binding.vnProgressStatus.getVisibility() == View.INVISIBLE) {
                                        binding.vnProgressStatus.setVisibility(View.VISIBLE);
                                    }
                                    double progress = workInfos.get(0).getProgress().getDouble(MediaMessageWork.PROGRESS, 0);
                                    binding.vnProgressStatus.setProgress((int) progress);
                                    if (side.equals(LAYOUT_SIDE.LEFT)) {
                                        media_status = MEDIA_STATUS.DOWNLOADING;
                                    } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                                        media_status = MEDIA_STATUS.UPLOADING;
                                    }

                                } else if (workInfos.get(0).getState() == WorkInfo.State.CANCELLED ||
                                        workInfos.get(0).getState() == WorkInfo.State.FAILED) {
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_cancel_white_24dp));
                                    if (side.equals(LAYOUT_SIDE.LEFT)) {
                                        binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_download_24));
                                    } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                                        binding.mediaMiddleIcon.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_upload_24));
                                    }
                                    binding.vnProgressStatus.setVisibility(View.GONE);
                                    media_status = MEDIA_STATUS.CANCELED;
                                }
                            } else {
                                binding.vnProgressStatus.setVisibility(View.GONE);
                                media_status = MEDIA_STATUS.CANCELED;
                            }
                        });

            }

            binding.vnPlayStop.setOnClickListener(v -> {

                if (media_status != null) {
                    if (media_status == MEDIA_STATUS.UPLOADING || media_status == MEDIA_STATUS.DOWNLOADING) {
                        itemAction.onCancelMediaWork(message);
                    } else if (media_status == MEDIA_STATUS.CANCELED) {
                        if (side.equals(LAYOUT_SIDE.LEFT)) {
                            itemAction.onRedownloadMedia(message);
                        } else if (side.equals(LAYOUT_SIDE.RIGHT)) {
                            itemAction.onReuploadMedia(message);
                        }
                    } else if (media_status == MEDIA_STATUS.AVAILABLE) {

                        if (mediaPlayer != null) {

                            if (vn_playing_id.equals(message.getMessage_id())) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                } else {
                                    mediaPlayer.start();
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_pause_24dp));
                                }
                            } else {

                                timerTask.cancel();

                                try {
                                    mediaPlayer.setDataSource(message.getMedia_path());
                                    mediaPlayer.prepare();
                                    vn_playing_id = message.getMessage_id();

                                    if (audioTimer == null) {
                                        audioTimer = new Timer();
                                    }

                                    timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            handler.post(() -> {
                                                if (mediaPlayer.isPlaying()) {
                                                    vn_play_time++;
                                                    if (binding != null) {
                                                        binding.vnProgress.setProgress(vn_play_time);
                                                        BindingAdapterUtil.formatMediaDuration(
                                                                binding.vnDuration,
                                                                vn_play_time
                                                        );
                                                    }
                                                }
                                            });
                                        }
                                        @Override
                                        public boolean cancel() {
                                            if (mediaPlayer != null) {
                                                if (mediaPlayer.isPlaying())
                                                    mediaPlayer.stop();
                                                binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                                vn_play_time = 0;
                                                binding.vnProgress.setProgress(vn_play_time);
                                                mediaPlayer.reset();
                                                BindingAdapterUtil.formatMediaDuration(
                                                        binding.vnDuration,
                                                        message.getMedia_metadata().getDuration()
                                                );
                                            }
                                            return super.cancel();
                                        }
                                    };

                                    mediaPlayer.seekTo(vn_play_time * 1000);
                                    audioTimer.schedule(timerTask, 0, 1000);
                                    mediaPlayer.start();
                                    binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_pause_24dp));
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            timerTask.cancel();
                                            binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                            vn_play_time = 0;
                                            binding.vnProgress.setProgress(vn_play_time);
                                            mediaPlayer.reset();
                                            mediaPlayer.release();
                                            mediaPlayer = null;
                                            BindingAdapterUtil.formatMediaDuration(
                                                    binding.vnDuration,
                                                    message.getMedia_metadata().getDuration()
                                            );
                                        }
                                    });

                                } catch (IOException e) {
                                    Log.e("MessagesAdapter", "prepare() failed");
                                }

                            }

                        } else {
                            mediaPlayer = new MediaPlayer();

                            try {
                                mediaPlayer.setDataSource(message.getMedia_path());
                                mediaPlayer.prepare();
                                vn_playing_id = message.getMessage_id();

                                if (audioTimer == null) {
                                    audioTimer = new Timer();
                                }

                                timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(() -> {
                                            if (mediaPlayer.isPlaying()) {
                                                vn_play_time++;
                                                if (binding != null) {
                                                    binding.vnProgress.setProgress(vn_play_time);
                                                    BindingAdapterUtil.formatMediaDuration(
                                                            binding.vnDuration,
                                                            vn_play_time
                                                    );
                                                }
                                            }
                                        });
                                    }
                                    @Override
                                    public boolean cancel() {
                                        if (mediaPlayer != null) {
                                            if (mediaPlayer.isPlaying())
                                                mediaPlayer.stop();
                                            binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                            vn_play_time = 0;
                                            binding.vnProgress.setProgress(vn_play_time);
                                            mediaPlayer.reset();
                                            BindingAdapterUtil.formatMediaDuration(
                                                    binding.vnDuration,
                                                    message.getMedia_metadata().getDuration()
                                            );
                                        }
                                        return super.cancel();
                                    }
                                };

                                mediaPlayer.seekTo(vn_play_time * 1000);
                                audioTimer.schedule(timerTask, 0, 1000);
                                mediaPlayer.start();
                                binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_pause_24dp));
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        timerTask.cancel();
                                        binding.vnPlayStop.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                        vn_play_time = 0;
                                        binding.vnProgress.setProgress(vn_play_time);
                                        mediaPlayer.reset();
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                        BindingAdapterUtil.formatMediaDuration(
                                                binding.vnDuration,
                                                message.getMedia_metadata().getDuration()
                                        );
                                    }
                                });



                            } catch (IOException e) {
                                Log.e("MessagesAdapter", "prepare() failed");
                            }

                        }

                    }
                }

            });
        }

    }

}
