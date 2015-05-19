package com.mypowerapps.android.jsfiddlequery.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.mypowerapps.android.jsfiddlequery.R;

public class PreferencesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_prefs_fragment);
	}

	public static class UserPreferenceFragment extends PreferenceFragment {
		private OnSharedPreferenceChangeListener prefListener;
		private Preference userNamePreference;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.user_prefs);

			userNamePreference= (Preference) getPreferenceManager().findPreference(Constants.PREFERENCE_USERNAME);

			prefListener = new OnSharedPreferenceChangeListener() {
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
					String defaultValue = getResources().getString(R.string.default_username);
					userNamePreference.setSummary(sharedPreferences.getString(Constants.PREFERENCE_USERNAME, defaultValue));
				}
			};

			SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

			prefs.registerOnSharedPreferenceChangeListener(prefListener);
			prefListener.onSharedPreferenceChanged(prefs, Constants.PREFERENCE_USERNAME);
		}
	}
}
