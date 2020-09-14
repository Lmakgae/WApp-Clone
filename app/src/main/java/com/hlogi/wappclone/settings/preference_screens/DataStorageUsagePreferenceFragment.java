package com.hlogi.wappclone.settings.preference_screens;

import android.os.Bundle;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.hlogi.wappclone.R;

import java.util.Set;

public class DataStorageUsagePreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.data_storage_usage_preference, rootKey);

        MultiSelectListPreference mobileDataPreference = findPreference("mobile_data_key");
        MultiSelectListPreference onWifiPreference = findPreference("on_wifi_key");
        MultiSelectListPreference roamingPreference = findPreference("roaming_key");

        if (mobileDataPreference != null) {
            mobileDataPreference.setSummaryProvider((Preference.SummaryProvider<MultiSelectListPreference>)
                    preference -> summary(preference.getValues()));
        }

        if (onWifiPreference != null) {
            onWifiPreference.setSummaryProvider((Preference.SummaryProvider<MultiSelectListPreference>)
                    preference -> summary(preference.getValues()));
        }

        if (roamingPreference != null) {
            roamingPreference.setSummaryProvider((Preference.SummaryProvider<MultiSelectListPreference>)
                    preference -> summary(preference.getValues()));
        }

    }

    public CharSequence summary(Set<String> stringSet) {
        if (stringSet.isEmpty()) {
            return "No media";
        } else if (stringSet.size() == 4) {
            return "All media";
        } else {
            StringBuilder summary = new StringBuilder();
            Object[] stringList =  stringSet.toArray();
            for (int i = 0; i < stringList.length; i++) {
                if(i < (stringList.length - 1)) {
                    summary.append(stringList[i]);
                    summary.append(", ");
                } else {
                    summary.append(stringList[i]);
                }
            }
            return summary;
        }
    }
}