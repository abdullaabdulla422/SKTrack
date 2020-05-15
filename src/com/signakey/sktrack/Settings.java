package com.signakey.sktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.SessionData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Settings extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    public String Company = null;
    public static String Gps = null;
    public static String GPs = null;
    private SignaKeyClient signakey = null;


    CheckBoxPreference switchlog;
    Logger logger;

    ListPreference gps;
    SharedPreferences settingPreferences;

    @SuppressWarnings("deprecation")
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signakey = new SignaKeyClient(this);
        signakey.isLoggedIn();
        settingPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        addPreferencesFromResource(R.xml.settings);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        PreferenceScreen preferences = getPreferenceScreen();
        preferences.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        switchlog = (CheckBoxPreference) findPreference("logger");

        if (switchlog.isChecked()) {
            Log.i("SKScanner", "Enabled True");
            SessionData.getInstance().setLogger(1);
        } else {
            Log.i("SKScanner", "Enabled false");
            SessionData.getInstance().setLogger(0);
        }
        switchlog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                return true;
            }
        });

        final Preference butt = (Preference) findPreference("button");
        butt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference v) {

                if (butt == v) {

//                    if (settingPreferences.getString("locationid", "").equals("")) {
//                        Toast.makeText(Settings.this, "Enter the LocationID", Toast.LENGTH_SHORT).show();
//
//                    } else if (settingPreferences.getString("taskid", "").equals("")) {
//                        Toast.makeText(Settings.this, "Enter the TaskID", Toast.LENGTH_SHORT).show();
//
//                    } else {
                    _login2sk();
//                    }

                }


                return true;
            }


        });
        gps = (ListPreference) findPreference("gps");
        SharedPreferences Preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        GPs = Preferences.getString("gps", "");
        gps.setSummary(GPs);

//		View.OnClickListener listener=new View.OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);
//				StringBuilder builder = new StringBuilder();
//				String listPrefs = prefs.getString("gps", "Default list prefs");
//				builder.append("List preference: " + listPrefs);
//				gps.setSummary(builder.toString());
////				gps.setText(builder.toString());
//			}
//		};

//		if(switchlog.isChecked()){
//			Log.i("SKScanner", "Enabled True");
//			SessionData.getInstance().setLogger(1);
//		}
//
//		else{
//			Log.i("SKScanner", "Enabled false");
//			SessionData.getInstance().setLogger(0);
//		}

//		gps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//
//				if(gps.getEntry().toString().length()==0){
////					gps.setEntryValues(R.array.gpsvalues);
//					gps.setSummary("No GPS");
//				}else if(gps.getEntry().toString().length()==1){
////					gps.setEntryValues(R.array.gpsvalues);
//					gps.setSummary("Phone GPS");
//				}else if(gps.getEntry().toString().length()==2){
////					gps.setEntryValues(R.array.gpsvalues);
//					gps.setSummary("Bad Elf GPS");
//
//				}
//
//
//				return true;
//
//			}
//		});
//
    }


    // On preference changes.
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Log.i("SKScanner", "Settings for camera PrefAct Sharedprefchg line 30.");
        SharedPreferences mPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        int mode = Integer.parseInt(mPreferences.getString("mode", "3"), 10);

        switch (mode) {
            case 1:
            default:
                ScanCamera.mRecognizeSignaKey = true;
                ScanCamera.mRecognizeStandard = false;
                Log.i("SKScanner",
                        "Settings: Line 45 Try to recognize SignaKey code using SignaKey library. mRecognizeSignaKey = "
                                + ScanCamera.mRecognizeSignaKey);
                if (switchlog.isChecked()) {
                    Log.i("SKScanner", "Enabled True");
                    SessionData.getInstance().setLogger(1);
                } else {
                    Log.i("SKScanner", "Enabled false");
                    SessionData.getInstance().setLogger(0);
                }

                break;
            case 2:

                ScanCamera.mRecognizeSignaKey = false;
                ScanCamera.mRecognizeStandard = true;

                Log.i("SKScanner",
                        "Settings: Line 52 Try to recognize standard barcode using ZXing library. mRecognizeSignaKey = "
                                + ScanCamera.mRecognizeSignaKey);
                if (switchlog.isChecked()) {
                    Log.i("SKScanner", "Enabled True");
                    SessionData.getInstance().setLogger(1);
                } else {
                    Log.i("SKScanner", "Enabled false");
                    SessionData.getInstance().setLogger(0);
                }
                break;
            case 3:
                ScanCamera.mRecognizeSignaKey = true;
                ScanCamera.mRecognizeStandard = true;
                break;

        }

        ScanCamera.mSignakeySymbol = Integer.parseInt(
                mPreferences.getString("signakey_symbol", "1"), 10);
        BeepManager mBeepManager = new BeepManager(this);
        mBeepManager.updatePrefs();

        ScanCamera.mBgMode = Integer.parseInt(
                mPreferences.getString("signakey_bg_mode", "1"), 10);
        if (ScanCamera.mBgMode == 2) {
            ScanCamera.mWhiteOnBlack = true;
        } else {
            ScanCamera.mWhiteOnBlack = false;
        }
        // Check to see if COmpany has changed if so get new logo
        Company = mPreferences.getString("company", "");
        Log.d("Company", "" + Company);
        // Username = mPreferences.getString("username", "");
        if (Company != SKScanner._company) {

            Toast.makeText(getApplicationContext(),
                    "Settings getting new logo from website.",
                    Toast.LENGTH_LONG).show();
            _initGetLogo();

        }


        if (key.equals("gps")) {
            Gps = sharedPreferences.getString("gps", "GPS");

            if (Gps.equals("GPS")) {
                Gps = "GPS";

            } else if (Gps.equals("NO GPS")) {
                Gps = "No GPS";
            } else if (Gps.equals("Phone GPS")) {

                Gps = "Phone GPS";
            } else if (Gps.equals("Bad Elf GPS")) {

                Gps = "Bad Elf GPS";
            } else {
                Gps = "No GPS";
            }
//			String Gps = mPreferences.getString("Gps", "");
            Log.d("Gps", "" + Gps);
//			SessionData.getInstance().setGpsSelection(Gps);
            gps.setSummary(Gps);

        }

    }


    private void _login2sk() {

        Log.i("SKScanner",
                "SKScanner: Line 245 Try to validate user on SignaKey.");
        new AsyncTask<SignaKeyClient, Void, Boolean>() {
            protected Boolean doInBackground(SignaKeyClient... params) {
                SignaKeyClient skclient = params[0];
                return skclient.login();
            }

            protected void onPostExecute(Boolean isLoggedIn) {
                if (isLoggedIn == false) {


                    Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();

                    Log.i("SKScanner",
                            "SKScanner: Line 254 User is not validated on SignaKey.");


                } else {
                    Log.i("SKScanner",
                            "SKScanner: Line 258 User is successfully validated on SignaKey.");


                    Intent in = new Intent(Settings.this, SKScanner.class);
                    startActivity(in);
                    finish();

                }
            }
        }.execute(signakey);

    }

    @SuppressWarnings("deprecation")

    private void _initGetLogo() {


        SharedPreferences mPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        //String Company = null;
        Company = mPreferences.getString("company", "");
        Log.i("SKScanner", "SKScanner: Line 224 Company = " + Company);


        Log.i("SKScanner", "SKScanner: Line 227 Company = " + Company);

        byte[] image = null; // byte array used to hold data from downloaded
        // file.
        DataInputStream dis = null; // input stream that will read data from
        // the file.


        URI myURI = null;
        try {

            myURI = new URI("http://www.signakey.com/public/demologo/"
                    + Company + ".bmp");

        } catch (URISyntaxException e) {
            Log.i("SKScanner", "SKScanner: Line 239 Syntax Exception." + e);
            Toast.makeText(getApplicationContext(),
                    "SKScanner: Error in format of Company.",
                    Toast.LENGTH_LONG).show();
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(myURI);
        HttpResponse webServerResponse = null;

        try {
            webServerResponse = httpClient.execute(getMethod);

            Log.i("SKScanner",
                    "Settings: Line 128 Settings webServerResponse"
                            + webServerResponse);


            HttpEntity httpEntity = webServerResponse.getEntity();

            if (httpEntity != null) {
                try {
                    image = new byte[(int) httpEntity.getContentLength()];
                    Log.i("SKScanner", "Settings: httpEntity Length"
                            + httpEntity.getContentLength());
                    dis = new DataInputStream((httpEntity).getContent());
                    for (int x = 0; x < image.length; x++) {
                        image[x] = (byte) dis.read();
                    }
                    dis.close();
                    FileOutputStream fos = openFileOutput("Logo.bmp",
                            Context.MODE_PRIVATE);

                    fos.write(image);
                    fos.close();
                    Log.i("SKScanner", "Settings: Line 271 Image saved.");

                    logger.addLog("SKScanner, Settings: Image saved.");


                } catch (IllegalStateException e) {
                    Log.i("SKScanner", "Settings: Save image IllegalStateException." + e);
                    Toast.makeText(getApplicationContext(),
                            "SKScanner: Error response.", Toast.LENGTH_LONG)
                            .show();

                    logger.addLog("SKScanner, Settings: IllegalStateException" + e);
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("SKScanner", "Settings: Line 159 I/O Exception." + e);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Settings: I/O Exception saving new Logo.",
                            Toast.LENGTH_LONG).show();
                    logger.addLog("SKScanner, Settings: Save image IOException" + e);
                }

            }


        } catch (ClientProtocolException e) {
            Log.i("SKScanner", "Settings: Protocol Exception.");
            Toast.makeText(getApplicationContext(),
                    "SKScanner: Error in Web Protocol.", Toast.LENGTH_LONG)
                    .show();
        } catch (IOException e) {
            Log.i("SKScanner", "SKScanner: I/O Exception.");
            Toast.makeText(getApplicationContext(),
                    "SKScanner: I/O Exception Error in Web Response.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.i("SKScanner", "SKScanner: I/O Exception.");
            Toast.makeText(getApplicationContext(),
                    "SKScanner: I/O Exception Error in Web Response.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }


    @Override
    public void onBackPressed() {

//        if (settingPreferences.getString("locationid", "").equals("")) {
//            Toast.makeText(Settings.this, "Enter the LocationID", Toast.LENGTH_SHORT).show();
//
//        } else if (settingPreferences.getString("taskid", "").equals("")) {
//            Toast.makeText(Settings.this, "Enter the TaskID", Toast.LENGTH_SHORT).show();
//
//        } else {

        _login2sk();

//        }
    }
}


