package com.signakey.sktrack;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Info extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		_initVersion();
		_initCameraFeatures();
	}

	private void _initVersion() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			TextView title = (TextView) findViewById(R.id.app_name);
			title.setText(getString(R.string.app_name) + " "
					+ packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void _initCameraFeatures() {
		// Open camera and get parameters.
		Camera camera = Camera.open();
		if (camera == null) {
			return;
		}
		Camera.Parameters params = camera.getParameters();
		if (params == null) {
			return;
		}

		camera.release();

	}
}
