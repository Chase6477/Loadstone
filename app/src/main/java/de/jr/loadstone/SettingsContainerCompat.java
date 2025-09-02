package de.jr.loadstone;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsContainerCompat extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_container, rootKey);

        PreferenceScreen screen = getPreferenceScreen();
        int count = screen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference pref = screen.getPreference(i);
            applyEditTextSettings(pref);
        }
    }

    private void applyEditTextSettings(Preference pref) {
        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) pref;

            editTextPref.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setSelection(editText.getText().length());
            });

            editTextPref.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        } else if (pref instanceof androidx.preference.PreferenceCategory) {
            androidx.preference.PreferenceCategory category = (androidx.preference.PreferenceCategory) pref;
            int count = category.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                applyEditTextSettings(category.getPreference(i));
            }
        }
    }
}