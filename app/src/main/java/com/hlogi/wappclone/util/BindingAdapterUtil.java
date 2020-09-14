package com.hlogi.wappclone.util;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BindingAdapterUtil {

    @BindingAdapter(value = {"imageUrl", "placeholder"})
    public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
        if (url == null || url.equals("null")) {
            imageView.setImageDrawable(placeHolder);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .placeholder(placeHolder)
                    .into(imageView);
        }
    }

    @BindingAdapter(value = {"chat_time"})
    public static void setChatTime(TextView textView, Long time) {
        if (time == null || time == 0) {
            textView.setText("");
        } else {
            Calendar message_cal = Calendar.getInstance(Locale.ENGLISH);
            Calendar current_cal = Calendar.getInstance(Locale.ENGLISH);
            message_cal.setTimeInMillis(time);
            String day = DateFormat.format("dd MM yyyy", message_cal).toString();
            String current_day = DateFormat.format("dd MM yyyy", current_cal).toString();

            current_cal.add(Calendar.DAY_OF_YEAR, -1);
            String yesterday = DateFormat.format("dd MM yyyy", current_cal).toString();

            if (day.equals(current_day)) {
                textView.setText(DateFormat.format("HH:mm", message_cal).toString());
            } else if (yesterday.equals(day)) {
                textView.setText(textView.getContext().getString(R.string.yesterday));
            } else {
                textView.setText(DateFormat.format("M/dd/yy", message_cal).toString());
            }
        }
    }


    @BindingAdapter(value = {"online_status", "last_seen", "typing_status", "recording_status"})
    public static void setOnlineLastSeenStatus(TextView textView, boolean online, Long last_seen, boolean typing, boolean recording) {

        if (online) {
            if (typing || recording) {
                if(typing)
                    textView.setText(textView.getContext().getString(R.string.typing));
                else
                    textView.setText(textView.getContext().getString(R.string.recording));

            } else {
                textView.setText(textView.getContext().getString(R.string.online));
            }

        } else {
            if (last_seen != null ) {

                Calendar last_seen_cal = Calendar.getInstance(Locale.ENGLISH);
                Calendar current_time_cal = Calendar.getInstance(Locale.ENGLISH);
                last_seen_cal.setTimeInMillis(last_seen);

                String last_seen_day = DateFormat.format("dd MM yyyy", last_seen_cal).toString();
                String current_time_day = DateFormat.format("dd MM yyyy", current_time_cal).toString();

                current_time_cal.set(Calendar.DAY_OF_YEAR, -1);
                // Current time calender instance is now a day behind
                String yesterday = DateFormat.format("dd MM yyyy", current_time_cal).toString();

                current_time_cal.set(Calendar.DAY_OF_YEAR, -6);
                // Current time calender instance is now a week behind

                if (last_seen_day.equals(current_time_day)) {
                    String time = DateFormat.format("HH:mm", last_seen_cal).toString();
                    textView.setText(textView.getResources().getString(R.string.last_seen_today, time));
                } else if (last_seen_day.equals(yesterday)) {
                    String time = DateFormat.format("HH:mm", last_seen_cal).toString();
                    textView.setText(textView.getResources().getString(R.string.last_seen_yesterday, time));
                } else {
                    if (last_seen_cal.compareTo(current_time_cal) > 0){
                        String time = DateFormat.format("HH:mm", last_seen_cal).toString();
                        String day = DateFormat.format("EEE", last_seen_cal).toString();
                        textView.setText(textView.getResources().getString(R.string.last_seen_day, day, time));
                    } else {
                        String time = DateFormat.format("HH:mm", last_seen_cal).toString();
                        String day = DateFormat.format("dd", last_seen_cal).toString();
                        String month = DateFormat.format("MMM", last_seen_cal).toString();
                        textView.setText(textView.getResources().getString(R.string.last_seen_month_day, month, day, time));
                    }
                }

            }
        }
    }

    @BindingAdapter(value = {"chat_convo_typing", "chat_convo_recording"})
    public static void setChatConvoTextView(TextView textView, boolean typing, boolean recording) {
        if (typing) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(textView.getContext().getString(R.string.typing));
        } else if (recording) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(textView.getContext().getString(R.string.recording));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ResourceType")
    @BindingAdapter(value = {"chat_convo_typing", "chat_convo_recording"})
    public static void setChatConvoTextGroup(Group group, boolean typing, boolean recording) {
        if (typing || recording) {
            group.setBackgroundColor(group.getContext().getColor(R.color.colorWhite));
        } else {
            group.setBackgroundColor(group.getResources().getColor(R.color.colorTransparent, null));
        }
    }

    @BindingAdapter(value = {"status_timestamp"})
    public static void setViewProfileStatusTime(@NonNull TextView textView, long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("MMMM dd, yyyy", calendar).toString();
        textView.setText(date);
    }

    @BindingAdapter(value = {"display_name", "online_status", "last_seen", "typing_status", "recording_status"})
    public static void setDisplayNameWithOnlineStatus(MaterialToolbar toolbar, String name, boolean online, Long last_seen, boolean typing, boolean recording) {

        if (online) {
            if (typing || recording) {
                if (typing) {
                    toolbar.setTitle(toolbar.getContext().getString(R.string.name_with_online_status,
                            name, toolbar.getContext().getString(R.string.typing)));
                    toolbar.setSubtitle(toolbar.getContext().getString(R.string.typing));
                } else {
                    toolbar.setTitle(toolbar.getContext().getString(R.string.name_with_online_status,
                            name, toolbar.getContext().getString(R.string.recording)));
                    toolbar.setSubtitle(toolbar.getContext().getString(R.string.recording));
                }

            } else {
                toolbar.setTitle(toolbar.getContext().getString(R.string.name_with_online_status,
                        name, toolbar.getContext().getString(R.string.online)));
                toolbar.setSubtitle(toolbar.getContext().getString(R.string.online));
            }
        } else {
            toolbar.setTitle(name);
            toolbar.setSubtitle(toolbar.getContext().getString(R.string.online));
        }

//        if (last_seen != null ) {
//            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//            calendar.setTimeInMillis(last_seen);
//            String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
//            //TODO: Show the proper text
//            textView.setText(date);
//        }

    }


    @BindingAdapter(value = {"message_time"})
    public static void setMessageTime(@NonNull TextView textView, long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("HH:mm", calendar).toString();
        textView.setText(date);
    }

    @BindingAdapter(value = {"sent_message_pending", "sent_message_delivered", "sent_message_read"})
    public static void setSentMessageIcon(ImageView imageView, boolean pending, boolean delivered, boolean read) {
        if (pending) {
            imageView.setBackground(imageView.getContext().getDrawable(R.drawable.ic_schedule_grey_24dp));
            imageView.setForeground(imageView.getContext().getDrawable(R.drawable.ic_schedule_grey_24dp));
        } else {
            if (delivered || read) {
                if(read) {
                    imageView.setBackground(imageView.getContext().getDrawable(R.drawable.ic_read_blue_24dp));
                    imageView.setForeground(imageView.getContext().getDrawable(R.drawable.ic_read_blue_24dp));
                } else {
                    imageView.setBackground(imageView.getContext().getDrawable(R.drawable.ic_delivered_grey_24dp));
                    imageView.setForeground(imageView.getContext().getDrawable(R.drawable.ic_delivered_grey_24dp));
                }
            } else {
                imageView.setBackground(imageView.getContext().getDrawable(R.drawable.ic_sent_grey_24dp));
                imageView.setForeground(imageView.getContext().getDrawable(R.drawable.ic_sent_grey_24dp));
            }
        }

    }

    @BindingAdapter(value = {"voice_note_played"})
    public static void setVoiceNotePlayedIcon(ImageView imageView, boolean played) {
        if (played) {
            imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_vn_media_ic_blue_24dp));
        } else {
            imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_vn_media_ic_grey_24dp));
        }

    }

    @BindingAdapter(value = {"duration", "media_played"})
    public static void setVoiceNoteProgressBar(SeekBar seekBar, Integer duration, boolean played) {
        if (played) {
            seekBar.setThumbTintList(ColorStateList.valueOf(seekBar.getContext().getColor(R.color.colorBlue)));
            seekBar.setProgressTintList(ColorStateList.valueOf(seekBar.getContext().getColor(R.color.colorBlue)));
        } else {
            seekBar.setThumbTintList(ColorStateList.valueOf(seekBar.getContext().getColor(R.color.colorGrey)));
            seekBar.setProgressTintList(ColorStateList.valueOf(seekBar.getContext().getColor(R.color.colorGrey)));
        }
    }

    @BindingAdapter(value = {"media_type", "media_played"})
    public static void setChatMediaIcons(ImageView imageView, @NonNull String media_type, boolean played) {
        switch (media_type) {
            case Message.MEDIA_PHOTO:
                imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_photo_media_ic_24dp));
                break;
            case Message.MEDIA_VIDEO:
                imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_videocam_24dp));
                break;
            case Message.MEDIA_VOICE_NOTE:
                if (played) {
                    imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_vn_media_ic_blue_24dp));
                } else {
                    imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_vn_media_ic_grey_24dp));
                }
                break;
        }

    }

    @BindingAdapter(value = {"media_type", "caption", "media_duration"})
    public static void setChatMediaMessage(TextView textView, @NonNull String media_type, String caption, int duration) {
        boolean aNull = caption == null || caption.equals("null") || caption.equals("");
        switch (media_type) {
            case Message.MEDIA_PHOTO:
                if (aNull) {
                    textView.setText(textView.getContext().getString(R.string.photo));
                } else {
                    textView.setText(caption);
                }
                break;
            case Message.MEDIA_VIDEO:
                if (aNull) {
                    textView.setText(textView.getContext().getString(R.string.video));
                } else {
                    textView.setText(caption);
                }
                break;
            case Message.MEDIA_VOICE_NOTE:
                formatMediaDuration(textView, duration);
                break;
        }

    }

    public static void formatMediaDuration(@NonNull TextView textView, int duration) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("m:ss", Locale.ENGLISH);
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        textView.setText(timeFormatter.format(new Date(duration * 1000)));
    }

}
