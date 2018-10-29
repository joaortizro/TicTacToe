package com.example.jonas.tictactoe;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final ListPreference difficultyLevelPref = (ListPreference) findPreference("difficulty_level");
        String difficulty = prefs.getString("difficulty_level",
                getResources().getString(R.string.difficutly_expert));
        difficultyLevelPref.setSummary((CharSequence) difficulty);
        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence) newValue);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("difficulty_level", newValue.toString());
                ed.commit();
                return true;
            }
        });
    }

}
