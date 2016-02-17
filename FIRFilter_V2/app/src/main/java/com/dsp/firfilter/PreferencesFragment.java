//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	private Monitor main;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		main = Settings.getCallbackInterface();
 		addPreferencesFromResource(R.xml.prefs);
 		
 		Preference preference = findPreference(getString(R.string.prefPlayback));
 		preference.setSummary("Current: " + (Settings.playback?"Enabled":"Disabled"));
 		preference = findPreference(getString(R.string.prefOutputStream));
 		preference.setSummary("Current: " + Settings.getOutput()); 		
        preference = findPreference(getString(R.string.prefSamplingFreq));
        ((ListPreference)preference).setEntries(Settings.samplingRates);
        ((ListPreference)preference).setEntryValues(Settings.samplingRateValues);
        preference.setSummary("Current: " + Settings.Fs + "Hz");
        preference = findPreference(getString(R.string.prefBlockSize));
        preference.setSummary("Current: " + Settings.blockSize + " samples");
        preference = findPreference(getString(R.string.prefDebug));
        preference.setSummary("Current: " + Settings.getDebug());
	}

	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		Preference preference = findPreference(key);
		if (key.equals(getString(R.string.prefPlayback))) {
			boolean setting = sharedPreferences.getBoolean(key, false);
			if(Settings.setPlayback(setting)) {
				String[] result = {"Playback disabled.", "Playback enabled for " + Settings.getOutput().toLowerCase(Locale.US) + " output."};
				preference.setSummary("Current: " + (Settings.playback?"Enabled":"Disabled"));
				preference = findPreference(getString(R.string.prefOutputStream));
		 		preference.setSummary("Current: " + Settings.getOutput()); 	
				main.notify(result[setting?1:0]);
			}
		} else if (key.equals(getString(R.string.prefOutputStream))) {
			int setting = Integer.parseInt(sharedPreferences.getString(key, "2"));
			if(Settings.setOutput(setting)) {
				preference.setSummary("Current: " + Settings.getOutput());
				String[] result = {"How could you let this happen?", "Playback set to original output.", "Playback set to filtered output."};
				main.notify(result[setting]);
			}
		} else if (key.equals(getString(R.string.prefSamplingFreq))) {
			int setting = Integer.parseInt(sharedPreferences.getString(key, "8000"));
			if(Settings.setSamplingFrequency(setting)) {
				preference.setSummary("Current: " + Settings.Fs + "Hz");
				main.notify("Sampling rate set to " + setting + "Hz.");
			}
		} else if (key.equals(getString(R.string.prefBlockSize))) {
			int result = Integer.parseInt(sharedPreferences.getString(key, "256"));
			result = Settings.setBlockSize(result);
			if(result == 1) {
				preference.setSummary("Current: " + Settings.blockSize + " samples");
				main.notify("Frame size set to " + Settings.blockSize + " samples.");
			} else if(result == -1) {
				main.notify("Frame length outside 32 to 2048 sample range.");
			}
		} else if (key.equals(getString(R.string.prefDebug))) {
			int setting = Integer.parseInt(sharedPreferences.getString(key, "4"));
			if(Settings.setDebugLevel(setting)) {
				preference.setSummary("Current: " + Settings.getDebug());
				String[] result = {"All debug outputs enabled.", "Text file output enabled.", "PCM ouput enabled.", "Debug ouput disabled."};
				main.notify(result[setting-1]);
			}
		}
	}
}