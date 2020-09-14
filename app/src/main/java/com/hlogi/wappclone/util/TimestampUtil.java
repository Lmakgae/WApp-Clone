package com.hlogi.wappclone.util;

import android.text.format.DateFormat;
import android.widget.TextView;

import com.hlogi.wappclone.R;

import java.util.Calendar;
import java.util.Locale;

public class TimestampUtil {

    public static Boolean differentDays(Long timestamp1, Long timestamp2) {
        Calendar time1_cal = Calendar.getInstance(Locale.ENGLISH);
        Calendar time2_cal = Calendar.getInstance(Locale.ENGLISH);
        time1_cal.setTimeInMillis(timestamp1);
        time2_cal.setTimeInMillis(timestamp2);
        int time1_cal_day =
                Integer.parseInt(DateFormat.format("dd", time1_cal).toString());
        int time2_cal_day =
                Integer.parseInt(DateFormat.format("dd", time2_cal).toString());
        if (time1_cal_day == time2_cal_day) {
            return false;
        } else {
            return true;
        }

    }

    public static void setMessageDateNotificationTimestamp(TextView textView, Long time) {
        if (time == null || time == 0) {
            textView.setText("");
        } else {
            Calendar message_cal = Calendar.getInstance(Locale.ENGLISH);
            Calendar current_cal = Calendar.getInstance(Locale.ENGLISH);
            message_cal.setTimeInMillis(time);
            int day = Integer.parseInt(DateFormat.format("dd", message_cal).toString());
            int current_day =
                    Integer.parseInt(DateFormat.format("dd", current_cal).toString());

            current_cal.add(Calendar.DAY_OF_YEAR, -1);
            int yesterday =
                    Integer.parseInt(DateFormat.format("dd", current_cal).toString());
            if (day == current_day) {
                textView.setText(textView.getContext().getString(R.string.today));
            } else if (yesterday == day) {
                textView.setText(textView.getContext().getString(R.string.yesterday));
            } else {
                textView.setText(DateFormat.format("MMM dd, yyyy", message_cal).toString());
            }
        }
    }

}
