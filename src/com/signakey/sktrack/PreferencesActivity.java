package com.signakey.sktrack;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener{
	
//	public static final String PREFERENCE_QRCODE = "settings_qrcode";
//	public static final String PREFERENCE_DATAMATRIX = "settings_dmcode";
//	public static final String PREFERENCE_1D_EAN = "settings_ean";
//	public static final String PREFERENCE_1D_CODE39 = "settings_code39";
//	public static final String PREFERENCE_1D_CODE128 = "settings_code128";
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		Log.i("SKScanner", "PrefAct line 22.");
		PreferenceScreen preferences = getPreferenceScreen();
		preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.i("SKScanner", "PrefAct Sharedprefchg line 29.");
	}
	
	

	

}
