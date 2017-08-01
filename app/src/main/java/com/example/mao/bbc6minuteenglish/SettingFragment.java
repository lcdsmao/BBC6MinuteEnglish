package com.example.mao.bbc6minuteenglish;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import com.example.mao.bbc6minuteenglish.sync.BBCSyncUtility;


public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);

        setMaxHistorySummary();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.setting_history_key).equals(key)) {
            BBCSyncUtility.contentListSync(getActivity());
            setMaxHistorySummary();
        }
    }

    private void setMaxHistorySummary() {
        String key = getString(R.string.setting_history_key);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        ListPreference preference = (ListPreference) preferenceScreen.findPreference(key);
        preference.setSummary(preference.getValue());
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
