package com.signakey.sktrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cvisionlab.zxing.KeyInterface;
import com.cvisionlab.zxing.KeyLoader;
import com.signakey.sktrack.skclient.GPSTracker;
import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.SessionData;

import static com.bad_elf.badelfgps.BadElfDevice.TAG;

public class KeyViewer extends Activity implements View.OnClickListener {
	public static final String EXTRA_PATH_TO_INFO = "extra_path_to_info";
	public static final String LogoPath = "\\sdcard\\logo\\";
	@SuppressWarnings("unused")
	private static final Context KeyViewer = null;

	private KeyInterface mKey = null;
	TextView main;
	Logger logger;
	Button attachment;

	public static String _Gps = null;
	private SharedPreferences mPreferences = null;
	private SignaKeyClient mSkClient = null;

	String GPScordinates;
	GPSTracker gps;
	double latitude, longitude;

	String locationId = "";
	String taskId = "";
	String gpsAccuracy = "";
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.key_viewer);
//        Context KeyViewer = KeyViewer(ACTIVITY_SERVICE);
		Log.i("SKScanner", "KeyViewer: Line 33 onCreate");
		// Extract path to info file.
		Bundle extras = getIntent().getExtras();
		String pathToInfo = extras.getString(EXTRA_PATH_TO_INFO);

		main = (TextView)findViewById(R.id.main);


		main.setOnClickListener(this);
		// String pathtoLogo = extras.getString(LogoPath);

		// Load key.
		mKey = (KeyInterface) getLastNonConfigurationInstance();
		if (mKey == null) {
			mKey = KeyLoader.load(pathToInfo);
		}

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//		_company = mPreferences.getString("company", "");
		locationId = mPreferences.getString("locationid", "");
		taskId = mPreferences.getString("taskid", "");
		// Bitmap.

		Bitmap bitmap = mKey.getBitmap();
		//  Bitmap bitmap = KeyLoader.load(LogoPath + SignaKeyClient.Company+ "." );
		if (bitmap != null) {
			//ImageView view = (ImageView) findViewByPath(LogoPath + );
			ImageView view = (ImageView)findViewById(R.id.image);
			//	ImageView view = (ImageView) findViewByPath(LogoPath + SignaKeyClient.Company+ ".");
			view.setImageBitmap(bitmap);
		}

		// Meta.
		String meta = mKey.getMeta();
		attachment=findViewById(R.id.attachment);
		attachment.setVisibility(View.GONE);
		attachment.setOnClickListener(this);

		if (meta != null) {
			TextView view = (TextView)findViewById(R.id.meta);
			view.setText(meta);

			logger.addLog("SKScanner: " + "Result :" + meta);

			if (view.getText().toString().contains("Media files attached")){
				attachment.setVisibility(View.VISIBLE);
			}else {
				attachment.setVisibility(View.GONE);
			}
		}

		_initInfoButton();
		CaptureActivity.DataMatrixResult = "";


	}
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//    	Log.i("SKScanner", "KeyViewer: Line 68 Back Button pressed keyCode = " + keyCode + " KeyEvent=" + event);
//    	super.onKeyDown(keyCode, event);
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//        	Log.i("SKScanner", "KeyViewer: Line 70 Back Button pressed keyCode = " + keyCode + " KeyEvent=" + event);
//            Intent Act2Intent = new Intent(KeyViewer, SKScanner.class);
//            startActivity(Act2Intent);
//            finish();
//            return true;
//    }
//    return false;
//    }

	/** Initialize "Info" button. */
	private void _initInfoButton() {
		Button button = (Button) findViewById(R.id.btn_info);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KeyViewer.this, Info.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i("SKScanner", "KeyViewer: Line 94 onRetainNonConfigurationInstance");
		return mKey;
	}

	@Override
	public void onBackPressed() {
//		finish();
//		SKScanner skScanner = SessionData.getInstance().getSkScanner();
//		if (skScanner!=null) {
//			Intent attachment = new Intent(KeyViewer.this, skScanner.getClass());
//			startActivity(attachment);
//		}
//				finish();
//
//		super.onBackPressed();


		Intent SKScanner =new Intent(KeyViewer.this, com.signakey.sktrack.SKScanner.class);

//		SKScanner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(SKScanner);
		finish();
//		mSkClient = new SignaKeyClient(this);

//		_loginsignakey2sk();
	}

	@Override
	public void onClick(View v) {

		if(main == v){
			Intent intent = new Intent(KeyViewer.this, SKScanner.class);
			startActivity(intent);
			finish();
		}

        if(attachment==v){
            Intent attachment=new Intent(KeyViewer.this,MediaFiles.class);
            startActivity(attachment);
        }
	}

	@SuppressLint("StaticFieldLeak")
	private void _loginsignakey2sk() {


		Log.i("SKScanner",
				"SKScanner: Line 245 Try to validate user on SignaKey.");
		new AsyncTask<SignaKeyClient, Void, Boolean>() {
			protected Boolean doInBackground(SignaKeyClient... params) {
				SignaKeyClient skclient = params[0];
				return skclient.login();
			}

			protected void onPostExecute(Boolean isLoggedIn) {
				if (!isLoggedIn) {

					Log.i("SKScanner",
							"SKScanner: Line 254 User is not validated on SignaKey.");
					Toast.makeText(getApplicationContext(), "Invalid Username or Password ", Toast.LENGTH_SHORT).show();
					//finish();

//					 Toast.makeText(getApplicationContext(),
//					 "ScanCam: User is not validated on SignaKey.\nCheck your account setting or internet connection."
//					 , Toast.LENGTH_LONG).show();

				} else {
					_Gps = mPreferences.getString("gps", "");
					if (_Gps.equals("Bad Elf GPS")) {
						if (checkBlootoothConnection()) {

							Log.i("SKScanner",
									"SKScanner: Line 258 User is successfully validated on SignaKey.");
							isLoggedIn = true;
							//Token = SignaKey.Token;
							Intent intent = new Intent(getApplicationContext(),
									CaptureActivity.class);

//					logger.addLog("Camera Started"  );

							SessionData.getInstance().setScanhandler(0);
							startActivityForResult(intent, SKScanner.REQUEST_CAPTUREACTIVITY);
							// _saveToken();
						} else {
							alertDialog("Alert");
						}
					} else if (_Gps.equals("Phone GPS")) {

//                        getGps();
//                        getGps2();
						if (getGps2()) {

							Log.i("SKScanner",
									"SKScanner: Line 258 User is successfully validated on SignaKey.");
							isLoggedIn = true;
							Intent intent = new Intent(getApplicationContext(),
									CaptureActivity.class);


							SessionData.getInstance().setScanhandler(0);
							startActivityForResult(intent, SKScanner.REQUEST_CAPTUREACTIVITY);
						}

					} else {

						Log.i("SKScanner",
								"SKScanner: Line 258 User is successfully validated on SignaKey.");
						isLoggedIn = true;
						Intent intent = new Intent(getApplicationContext(),
								CaptureActivity.class);


						SessionData.getInstance().setScanhandler(0);
						startActivityForResult(intent, SKScanner.REQUEST_CAPTUREACTIVITY);
					}


				}
			}
		}.execute(mSkClient);

	}


	public boolean getGps2() {
		boolean x = false;
		gps = new GPSTracker(KeyViewer.this);
		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			x = true;
			GPScordinates = getChainedGpsStringBuilder().toString();
//            GPScordinates = "?*GPScoordinates=" + latitude + "," + longitude + "*LocationID=" +locationId+"*GPSaccuracy="+"*TaskID="+taskId;

			SessionData.getInstance().setGpsCoordinates(GPScordinates);

			System.out.println("Capture --- SKScanner Activity Coordinates lat " + latitude + " long " + longitude);


		} else {
			x = false;
			showSettingsAlert();
		}
		return x;
	}


	public boolean checkBlootoothConnection() {
		BluetoothAdapter bluetoothAdapter;
		boolean connection = true;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			connection = false;
//           throw new UnsupportedOperationException(getString(com.bad_elf.badelfgps.R.string.badElfGpsErrorNoBluetooth));
			Log.e(TAG, "checkBlootoothConnection: " + getString(com.bad_elf.badelfgps.R.string.badElfGpsErrorNoBluetooth));

		}
		if (!bluetoothAdapter.isEnabled()) {
			connection = false;
			Log.e(TAG, "checkBlootoothConnection: " + getString(com.bad_elf.badelfgps.R.string.badElfGpsErrorBluetoothDisabled));
//           throw new IllegalStateException(getString(com.bad_elf.badelfgps.R.string.badElfGpsErrorBluetoothDisabled));
		}
		return connection;
	}

	public void alertDialog(String dialog) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(dialog)
				.setMessage("Bluetooth is not enabled")
				.setPositiveButton("Enable bluetooth", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//set what would happen when positive button is clicked
						setBluetooth(true);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//set what should happen when negative button is clicked
					}
				})
				.show();
	}

	public static boolean setBluetooth(boolean enable) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean isEnabled = bluetoothAdapter.isEnabled();
		if (enable && !isEnabled) {
			return bluetoothAdapter.enable();
		} else if (!enable && isEnabled) {
			return bluetoothAdapter.disable();
		}
		// No need to change bluetooth state
		return true;
	}

	private String getChainedGpsStringBuilder() {
		String result = "";
		StringBuilder stringBuilderGps = new StringBuilder();

//        stringBuilderGps.append("?*GPScoordinates=" + latitude + "," + longitude);
		stringBuilderGps.append("?");


		if (latitude != 0) {
			stringBuilderGps.append("*GPScoordinates=" + latitude + "," + longitude);
		}
		if (!locationId.equals("")) {
			if (latitude != 0) {
				stringBuilderGps.append("&*LocationID=" + locationId);
			} else {
				stringBuilderGps.append("*LocationID=" + locationId);
			}
		}
		if (!gpsAccuracy.equals("")) {
			if (latitude != 0 || !locationId.equals("")) {
				stringBuilderGps.append("&*GPSaccuracy=" + gpsAccuracy);
			} else {
				stringBuilderGps.append("*GPSaccuracy=" + gpsAccuracy);
			}
		}
		if (!taskId.equals("")) {
			if (latitude != 0 || !locationId.equals("") || !gpsAccuracy.equals("")) {
				stringBuilderGps.append("&*TaskID=" + taskId);
			} else {
				stringBuilderGps.append("*TaskID=" + taskId);
			}
		}
		result = stringBuilderGps.toString();
		if (latitude == 0 && locationId.equals("") && gpsAccuracy.equals("") && taskId.equals("")) {
			result = "";
		}

		Log.i("", "getChainedGpsStringBuilder:result " + result);

		return result;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing the Settings button.
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});

		// On pressing the cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

}

