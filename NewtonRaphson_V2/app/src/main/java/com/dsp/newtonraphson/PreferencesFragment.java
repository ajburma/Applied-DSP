//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.newtonraphson;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	private Monitor main;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		main = Settings.getCallbackInterface();
 		addPreferencesFromResource(R.xml.prefs);
 		
 		Preference preference = findPreference(getString(R.string.prefNumerator));
        preference.setSummary("Current: " + Settings.numeratorFloat);
        preference.setOnPreferenceChangeListener(preferenceChange);
        
 		preference = findPreference(getString(R.string.prefDenominator));
        preference.setSummary("Current: " + Settings.denominatorFloat);
        preference.setOnPreferenceChangeListener(preferenceChange);
        
        preference = findPreference(getString(R.string.prefIterations));
        preference.setSummary("Current: " + Settings.iterations);
        preference.setOnPreferenceChangeListener(preferenceChange);
        
        preference = findPreference(getString(R.string.prefNumeratorShort));
        preference.setSummary("Current: " + Settings.numeratorShort);
        preference.setOnPreferenceChangeListener(preferenceChange);
        
 		preference = findPreference(getString(R.string.prefDenominatorShort));
        preference.setSummary("Current: " + Settings.denominatorShort);
        preference.setOnPreferenceChangeListener(preferenceChange);
	}

	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}
	
	private OnPreferenceChangeListener preferenceChange = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object changedValue) {
			String testString = changedValue.toString().trim();
			if(testString == "") {
				return false;
			} else {
				try {
					Float.parseFloat(testString);
					return true;
				} catch(NumberFormatException e){}
				return false;
			}
		}
	};

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		Preference preference = findPreference(key);
		if (key.equals(getString(R.string.prefNumerator))) {
			float setting = Float.parseFloat(sharedPreferences.getString(key, "2.71"));
			if(Settings.setNumerator(setting)) {
				preference.setSummary("Current: " + Settings.numeratorFloat);
				main.notify("Numerator set to " + Settings.numeratorFloat);
			}
		} else if (key.equals(getString(R.string.prefDenominator))) {
			float setting = Float.parseFloat(sharedPreferences.getString(key, "3.14"));
			if(Settings.setDenominator(setting)) {
				preference.setSummary("Current: " + Settings.denominatorFloat);
				main.notify("Denominator set to " + Settings.denominatorFloat);
			}
		} else if (key.equals(getString(R.string.prefIterations))) {
			int setting = Integer.parseInt(sharedPreferences.getString(key, "3"));
			if(Settings.setIterations(setting)) {
				preference.setSummary("Current: " + Settings.iterations);
				main.notify("Iterations set to " + Settings.iterations);
			}
		} else if (key.equals(getString(R.string.prefNumeratorShort))) {
			short setting = (short) Integer.parseInt(sharedPreferences.getString(key, "6000"));
			if(Settings.setNumeratorShort(setting)) {
				preference.setSummary("Current: " + Settings.numeratorShort);
				main.notify("Numerator set to " + Settings.numeratorShort);
			}
		} else if (key.equals(getString(R.string.prefDenominatorShort))) {
			short setting = (short) Integer.parseInt(sharedPreferences.getString(key, "8192"));
			if(Settings.setDenominatorShort(setting)) {
				preference.setSummary("Current: " + Settings.denominatorShort);
				main.notify("Denominator set to " + Settings.denominatorShort);
			}
		}
	}
}