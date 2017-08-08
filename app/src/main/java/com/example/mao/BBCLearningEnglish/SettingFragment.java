package com.example.mao.BBCLearningEnglish;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.sync.BBCSyncUtility;
import com.example.mao.BBCLearningEnglish.sync.JobDispatcher;

import org.w3c.dom.Text;


public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);

        setMaxHistorySummary();
        setPreferenceClickListener();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.setting_history_key).equals(key)) {
            setMaxHistorySummary();
        } else if (getString(R.string.setting_notification_key).equals(key)) {
            JobDispatcher.dispatcherScheduleSync(getActivity());
            //Test notification
            //NotificationUtility.showNewContentNotification(getActivity());
        }
    }

    private void setMaxHistorySummary() {
        String key = getString(R.string.setting_history_key);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        ListPreference preference = (ListPreference) preferenceScreen.findPreference(key);
        preference.setSummary(preference.getValue());
    }

    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_licenses, null);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.loadUrl("file:///android_asset/Licenses.html");
        new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setTitle(getString(R.string.open_source_license))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void setPreferenceClickListener() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference licensesPreference =
                preferenceScreen.findPreference(getString(R.string.setting_licenses_key));
        licensesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayLicensesAlertDialog();
                return true;
            }
        });
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
