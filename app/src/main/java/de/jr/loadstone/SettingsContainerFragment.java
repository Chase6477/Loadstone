package de.jr.loadstone;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsContainerFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_container, rootKey);
    }
}