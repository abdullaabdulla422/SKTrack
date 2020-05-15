package com.signakey.sktrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bad_elf.badelfgps.BadElfService;
import com.signakey.skscanner.SignaKeyRecognizer;
import com.signakey.sktrack.skclient.GPSTracker;
import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.SessionData;
import com.signakey.sktrack.skclient.WebMarkAuthenticateResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.bad_elf.badelfgps.BadElfDevice.TAG;

public class SKScanner extends Activity {
    public final static int REQUEST_PICK_IMAGE = 1001;
    public final static int REQUEST_SCAN_CAMERA = 1002;
    public final static int REQUEST_CAPTUREACTIVITY = 1003;
    public static final String EXTRA_PATH_TO_INFO = "extra_path_to_info";
    BeepManager mBeepManager = null;
    private SharedPreferences mPreferences = null;
    private SignaKeyClient mSkClient = null;
    // private Context mContext1 = null;
    private boolean isLoggedIn = false;
    private boolean decodesuccess = false;
    public static String DataMatrixResult = " ";
    public static WebMarkAuthenticateResult globalresult = null;
    public static String Token = null;
    private ProgressDialog mProgressDialog;
    public int _mediafiles = 0;
    public static int _pubmediafiles = 0;
    public static int _conmediafiles = 0;
    public static int _resmediafiles = 0;
    public static int _primediafiles = 0;
    public static String _company = null;
    public static String _Gps = null;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    Boolean isBadelfPresent = false;
    double latitude, longitude;
    GPSTracker gps;
    BadElfService badElfService;

    Logger logger;

    // Connection detector class
    ConnectionDetector cd;

    String GPScordinates;

    String locationId = "";
    String taskId = "";
    String gpsAccuracy = "";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        if (SessionData.getInstance().getClear_errorMsf()) {
            SessionData.getInstance().setClear_errorMsf(false);
            startActivity(new Intent(getApplicationContext(), SKScanner.class));
        }

//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//			String[] permission = {"android.permission.CAMERA","android.permission.ACCESS_FINE_LOCATION"};
//			int permissionrequestcode = 200;
//			requestPermissions(permission, permissionrequestcode);
//		}


        Log.i("SKScanner", "SKScanner: Line 45 onCreate. isLoggedIn = "
                + isLoggedIn);
        _initParam(); // Check the decode choices SignaKey or Other
        _initInfoButton(); // Version Info
        _initSettingsButton(); // Configure
        _initScanImageButton(); // STored Image
        //	splash();

        _initMediaButton(); // Media
        cd = new ConnectionDetector(getApplicationContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _company = mPreferences.getString("company", "");
        locationId = mPreferences.getString("locationid", "");
        taskId = mPreferences.getString("taskid", "");

        // _initGetLogo(); // Get Logo from web site if needed
        _mediafiles = 0;
        new SignaKeyRecognizer();

        Log.i("SKScanner", "SKScanner: Line 57 new SignaKeyRecognizer.");
        // Start SignaKeyCLient
        mSkClient = new SignaKeyClient(this);
        // Check credentials
        _login2sk();// Login to SignaKey's service.
        // Beep manager.
        mBeepManager = new BeepManager(this);
        StorageManager storage = new StorageManager();
        storage.init(getApplicationContext());
        String file = storage.getBaseDir();
        if (file != null) {
            File filex = new File(file);
            // Delete folder
            if (filex.exists()) {
                String deleteCmd = "rm -r " + file;
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec(deleteCmd);
                } catch (IOException e) {
                }
            }
        }

        SKScanner skScanner = (SKScanner) this;
        SessionData.getInstance().setSkScanner(skScanner);
        _initScanCamButton(); // Camera

//


    }

    @Override
    protected void onResume() {
        super.onResume();
        GPScordinates = getChainedGpsStringBuilder().toString();

    }

    /**
     * Initialize "Scan camera" button.
     */
    public void _initScanCamButton() {
        Log.i("SKScanner", "SKScanner: Line 114 _initScanCamButton ");
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int mode = Integer.parseInt(preferences.getString("mode", "3"), 10);
        Button button = (Button) findViewById(R.id.btn_scan_camera);
        // Evaluate the state of decoding barcode or signakey
        if (mode == 1 || mode == 3) {
            button.setText("SignaKey");

        } /*else if (mode == 2) {
			button.setText("Standard");

		}*/ else {
            button.setText("Standard");

        }


        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {

//                    getGps();

                    // Internet Connection is Present
                    // make HTTP requests
                    _loginsignakey2sk();

//	    				Intent intent = new Intent(getApplicationContext(),
//	    						CaptureActivity.class);
//	    				startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);

                } else {

                    // Internet connection is not present
                    // Ask user to connect to Internet
                    Toast.makeText(getApplicationContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
//	                    showAlertDialog(SKScanner.this, "No Internet Connection",
//	                            "You don't have internet connection.", false);
                }


            }
        });
    }
    //}

    public void getGps() {
        gps = new GPSTracker(SKScanner.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


            GPScordinates = "?*GPScoordinates=" + latitude + "," + longitude;

            SessionData.getInstance().setGpsCoordinates(GPScordinates);

            System.out.println("Capture --- SKScanner Activity Coordinates lat " + latitude + " long " + longitude);


        } else {
            gps.showSettingsAlert();
        }
    }
//	public void getBadelfgps(){
//		badElfService=new BadElfService(SKScanner.this);
//		if (badElfService.canGetLocation()) {
//			latitude = badElfService.getLatitude();
//			longitude = badElfService.getLongitude();
//
//
//			GPScordinates = "?*GPScoordinates="+latitude+","+longitude;
//
//			SessionData.getInstance().setGpsCoordinates(GPScordinates);
//
//			System.out.println("Capture --- SKScanner Activity Coordinates lat "+latitude +" long "+ longitude);
//
//
//		} else {
//			badElfService.showSettingsAlert();
//		}
//
//	}

    /**
     * Initialize "Info" button.
     */
    private void _initInfoButton() {
        Log.i("SKScanner", "SKScanner: Line 120 _initInfoButton ");
        Button button = (Button) findViewById(R.id.btn_info);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Info.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initialize "Settings" button.
     */
    private void _initSettingsButton() {

        Log.i("SKScanner", "SKScanner: Line 120 _initSettingsButton ");
        Button button = (Button) findViewById(R.id.btn_preferences);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // get Internet status

                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {

                    // Internet Connection is Present
                    // make HTTP requests

                    Intent intent = new Intent(getApplicationContext(),
                            Settings.class);
                    startActivity(intent);
                } else if (isBadelfPresent) {

//                    getBadelfgps();
                    _loginsignakey2sk();

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    Toast.makeText(getApplicationContext(),
                            "No Internet Connection",
                            Toast.LENGTH_LONG).show();
//                    showAlertDialog(SKScanner.this, "No Internet Connection",
//                            "You don't have internet connection.", false);
                }
//
            }
        });
    }

//


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }


    /**
     * Initialize "Scan image" button.
     */
    private void _initScanImageButton() {


        Log.i("SKScanner", "SKScanner: Line 140 _initScanImageButton ");
        Button button = (Button) findViewById(R.id.btn_scan_image);
        button.setEnabled(true);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        REQUEST_PICK_IMAGE);
                //startActivity(new Intent(SKScanner.this, DecodeImageActivity.class));
            }
        });
    }

    /**
     * Initialize "Media" button.
     */
    private void _initMediaButton() {
        Log.i("SKScanner", "SKScanner: Line 156 _initMediaButton ");
        // Check to see if media files are attached then make "Media" visible
        if (_mediafiles == 0) {
            Button _media = (Button) findViewById(R.id.btn_media);
            _media.setEnabled(false);

            // _media.setBackgroundColor(Color.GRAY);
        }

        Button button = (Button) findViewById(R.id.btn_media);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        MediaFiles.class);
                startActivity(intent);
            }
        });
    }

    // Init camera parameters
    public void _initParam() {
        Log.i("SKScanner", "SKScanner: Line 200 _initParam ");
        SharedPreferences mPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int mode = Integer.parseInt(mPreferences.getString("mode", "3"), 10);
        switch (mode) {
            case 1:
            default:
                ScanCamera.mRecognizeSignaKey = true;
                ScanCamera.mRecognizeStandard = false;

                Log.i("SKScanner",
                        "SKScanner: Line 209 Try to recognize SignaKey code using SignaKey library. mRecognizeSignaKey = "
                                + ScanCamera.mRecognizeSignaKey);
                break;
            case 2:

                ScanCamera.mRecognizeSignaKey = false;
                ScanCamera.mRecognizeStandard = true;

                Log.i("SKScanner",
                        "SKScanner: Line 216 Try to recognize standard barcode using ZXing library. mRecognizeStandard = "
                                + ScanCamera.mRecognizeStandard);
                break;
            case 3:
                ScanCamera.mRecognizeSignaKey = true;
                ScanCamera.mRecognizeStandard = true;
                break;
        }
        ScanCamera.mSignakeySymbol = Integer.parseInt(
                mPreferences.getString("signakey_symbol", "1"), 10);
        // TextView modeTitle = (TextView) findViewById(R.id.mode);
        // modeTitle.setText(getResources().getStringArray(R.array.code_type_entries)[mode-1]);
        BeepManager mBeepManager = new BeepManager(this);
        mBeepManager.updatePrefs();

        ScanCamera.mBgMode = Integer.parseInt(
                mPreferences.getString("signakey_bg_mode", "1"), 10);
        if (ScanCamera.mBgMode == 2) {
            ScanCamera.mWhiteOnBlack = true;
        } else {
            ScanCamera.mWhiteOnBlack = false;
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
                            startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);
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
                            startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);
                        }

                    } else {

                        Log.i("SKScanner",
                                "SKScanner: Line 258 User is successfully validated on SignaKey.");
                        isLoggedIn = true;
                        Intent intent = new Intent(getApplicationContext(),
                                CaptureActivity.class);


                        SessionData.getInstance().setScanhandler(0);
                        startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);
                    }


                }
            }
        }.execute(mSkClient);

    }

    @SuppressLint("StaticFieldLeak")
    private void _loginsignakey2sk2(final Intent data, final SharedPreferences preferences, final int mode) {


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
//                            Intent intent = new Intent(getApplicationContext(),
//                                    CaptureActivity.class);
//
////					logger.addLog("Camera Started"  );
//
//                            SessionData.getInstance().setScanhandler(0);
//                            startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);

                            scanImage(data, preferences, mode);
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
                            scanImage(data, preferences, mode);
//                            Intent intent = new Intent(getApplicationContext(),
//                                    CaptureActivity.class);
//
//
//                            SessionData.getInstance().setScanhandler(0);
//                            startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);
                        }

                    } else {
                        latitude = 0;
                        longitude = 0;
                        GPScordinates = getChainedGpsStringBuilder();

                        SessionData.getInstance().setGpsCoordinates(GPScordinates);
                        Log.i("SKScanner",
                                "SKScanner: Line 258 User is successfully validated on SignaKey.");
                        isLoggedIn = true;
                        scanImage(data, preferences, mode);
//                        Intent intent = new Intent(getApplicationContext(),
//                                CaptureActivity.class);
//
//
//                        SessionData.getInstance().setScanhandler(0);
//                        startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);
                    }


                }
            }
        }.execute(mSkClient);

    }


    @SuppressLint("StaticFieldLeak")
    private void _login2sk() {
        // isLoggedIn = SignaKeyClient.LoginStatus;
        if (SignaKey.Token != null) {
            Log.i("SKScanner",
                    "SKScanner: Line 241 User logged in on SignaKey.");
            return;
        }

        Log.i("SKScanner",
                "SKScanner: Line 245 Try to validate user on SignaKey.");
        new AsyncTask<SignaKeyClient, Void, Boolean>() {
            protected Boolean doInBackground(SignaKeyClient... params) {
                SignaKeyClient skclient = params[0];
                return skclient.login();
            }

            protected void onPostExecute(Boolean isLoggedIn) {
                if (isLoggedIn == false) {

                    Log.i("SKScanner",
                            "SKScanner: Line 254 User is not validated on SignaKey.");

//					 Toast.makeText(getApplicationContext(),
//					 "ScanCam: User is not validated on SignaKey.\nCheck your account setting or internet connection."
//					 , Toast.LENGTH_LONG).show();

                } else {
                    Log.i("SKScanner",
                            "SKScanner: Line 258 User is successfully validated on SignaKey.");
                    isLoggedIn = true;
                    Token = SignaKey.Token;
                    // _saveToken();

                }
            }
        }.execute(mSkClient);

    }

    /**
     * Activity result handler.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int mode = Integer.parseInt(preferences.getString("mode", "3"), 10);
        Button button = (Button) findViewById(R.id.btn_scan_camera);

        // Toast.makeText(SKScanner.this, ("Result code = " + resultCode),
        // Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_CANCELED) {
            SignaKey.Token = null;

            // Get new token
//			Toast.makeText(SKScanner.this, "Login expired, ReTry login.",
//					Toast.LENGTH_LONG).show();
            _login2sk();

        }

        if (resultCode == RESULT_OK) {
            // Evaluate the state of decoding barcode or signakey
            if (mode == 1 || mode == 3) {
                button.setText("SignaKey");

            } else {
                button.setText("Standard");

            }
			 
			/*else {
				button.setText("Standard");
  
			}*/
            // Check to see if media files are attached then make "Media"
            // enabled
            _mediafiles = _pubmediafiles + _conmediafiles + _resmediafiles
                    + _primediafiles;
            if (_mediafiles > 0) {
                Button _media = (Button) findViewById(R.id.btn_media);
                _media.setEnabled(true);

            }
            if (_mediafiles == 0) {
                Button _media = (Button) findViewById(R.id.btn_media);
                _media.setEnabled(false);
            }

            if (requestCode == REQUEST_PICK_IMAGE) {

                _loginsignakey2sk2(data, preferences, mode);

            }
        }


        // new AsyncTask<Void, Void, WebMarkAuthenticateResult>() {
        // protected void onPreExecute() {
        // mProgressDialog = new ProgressDialog(SKScanner.this);
        // mProgressDialog.setCancelable(false);
        // mProgressDialog.setMessage("Processing stored image ...");
        // mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // mProgressDialog.show();
        // };
        //
        // protected WebMarkAuthenticateResult doInBackground(Void... params) {
        // SignaKeyClient skclient = new SignaKeyClient(SKScanner.this);
        // WebMarkAuthenticateResult result = null;
        // Log.i("SKScanner", "SKScanner: Try to validate user on SignaKey.");
        // if (true) {
        // Log.i("SKScanner",
        // "SKScanner: User is successfully validated on SignaKey.");
        //
        // // Auth key.
        // int tryCount = 0;
        // while ((result == null) && (tryCount < 2)) {
        // Log.i("SKScanner", "SKScanner: Try to authenticate symbol (attempt "
        // + (tryCount + 1) + " of 2)");
        // result = skclient.authenticateSymbol(keyBuffer);
        // if (result == null) {
        // try {
        // Thread.sleep(1000);
        // }
        // catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // tryCount++;
        // }
        // }
        // else {
        // Log.i("SKScanner", "SKScanner: User is not validated on SignaKey.");
        // }
        // return result;
        // };
        //
        // protected void onPostExecute(WebMarkAuthenticateResult result) {
        // mProgressDialog.dismiss();
        //
        // // Prepare bitmap and meta data.
        // // Bitmap bitmap = BitmapFactory.decodeFile(path);
        // String InLogo = "Logo.bmp";
        // FileInputStream fis = null;
        // try {
        // fis = openFileInput(InLogo);
        // Log.i("SKScanner", "SKScanner: File Open Result fis= " + fis);
        // } catch (FileNotFoundException e) {
        // Log.i("SKScanner", "SKScanner: FileNotFoundException e " + e);
        // e.printStackTrace();
        // }
        //
        //
        // Log.i("SKScanner", "SKScanner: Image saved.");
        //
        // Bitmap bitmap = BitmapFactory.decodeStream( fis);
        // String meta = SignaKey.keyBufferToMeta(keyBuffer);
        //
        // if ((result == null) || (result.Common.WebResultCode != 0)) {
        // Log.i("SKScanner",
        // "SKScanner: Error during authentication key in SKScanner.");
        // meta = meta + mPreferences.getString("company", "") +
        // "\nWeb result message = " + result.Common.ServiceResultSuccess + "."
        // ;
        // Toast.makeText(SKScanner.this,
        // "Error during authentication of key. In SKScanner code.",
        // Toast.LENGTH_LONG).show();
        //
        // if (result != null) {
        // Log.i("SKScanner", "SKScanner: Web result code = " +
        // result.Common.WebResultCode + ".");
        // }
        // }
        // else {
        // Log.i("SKScanner", "SKScanner: Authenticate status = " +
        // result.Common.ServiceResultMessage + " (" +
        // result.Common.ServiceResultSuccess + ").");
        // meta = meta + mPreferences.getString("company", "") + "\n" ;
        // if (!result.Common.ServiceResultSuccess){
        // meta = meta + "\nStatus = " + (result.Common.ServiceResultMessage +
        // " (" + result.Common.ServiceResultSuccess + ").");
        // } else {
        //
        // if (result.PublicText != null){
        // meta = meta + "\n\nPublic Text = " + result.PublicText;}
        // if(result.ConsumerText != null ){
        // / meta = meta + "\n\nConsumer Test = " + result.ConsumerText;}
        // if (result.RestrictedText != null){
        // meta = meta + "\n\nRestrictedText = " + result.RestrictedText;}
        // if (result.PrivateText != null){
        // meta = meta + "\n\nPrivate Text = " + result.PrivateText;}
        // }
        // }
        //
        // Store SignaKey.
        // SignaKey k = new SignaKey(bitmap, SignaKey.TYPE_SIGNAKEY, meta,
        // keyBuffer);
        // SignaKey k = new SignaKey(bitmap, SignaKey.TYPE_SIGNAKEY, meta,
        // keyBuffer);
        // StorageManager storage = new StorageManager();
        // storage.init(getApplicationContext());
        // if (k.storeTo(storage.getBaseDir()) == false) {
        // Toast.makeText(getApplicationContext(),
        // "SKScanner: Can't save key to history.", Toast.LENGTH_LONG).show();
        // }
        // else {
        // Intent intent = new Intent(getApplicationContext(), KeyViewer.class);
        // intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO, k.getAssociatedPath());
        // startActivity(intent);
        // }
        // }
        // }.execute();
        // }
        // }

        // "Standard" or "SignaKey and Standard".
        // if (!isRecognized && ((mode == 2) || (mode == 3))) {
        // Log.i("SKScanner",
        // "SKScanner: Line 454 Try to recognize code using ZXing library.");
        // Result res = ReaderInterface.tryToRead(path, true);
        // if(res != null) {
        // // Store ZXingKey.
        // ZXingKey k = new ZXingKey(path, res);
        // StorageManager storage = new StorageManager();
        // storage.init(getApplicationContext());
        // if (k.storeTo(storage.getBaseDir()) == false) {
        // Toast.makeText(getApplicationContext(),
        // "SKScanner: Line 462 Can't save key to history.",
        // Toast.LENGTH_LONG).show();
        // }
        // else {
        // Intent intent = new Intent(getApplicationContext(), KeyViewer.class);
        // intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO, k.getAssociatedPath());
        // startActivity(intent);
        // }
        // Toast.makeText(getApplicationContext(),
        // "SKScanner: Line 469 Barcode is not recoginzed result = " + res ,
        // Toast.LENGTH_LONG).show();
        // isRecognized = true;
        // }
        // }
        //
        // if (!isRecognized) {
        // Toast.makeText(getApplicationContext(),
        // "SKScanner: Line 475 Barcode is not recoginzed .",
        // Toast.LENGTH_LONG).show();
        // }
        // }
        // else if (requestCode == REQUEST_SCAN_CAMERA) {
        // // Get path to saved info in history.
        // String pathToInfo =
        // data.getExtras().getString(ScanCamera.EXTRA_PATH_TO_INFO);
        //
        // // Show key.
        // Intent intent = new Intent(getApplicationContext(), KeyViewer.class);
        // intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO, pathToInfo);
        // startActivity(intent);
        // }
        // }
    }


    @SuppressLint("StaticFieldLeak")
    public boolean _processRecognizedKey(final byte[] keyBuffer) {
        // Make storage manager.
        mSkClient = new SignaKeyClient(this);
        // final StorageManager storage = new StorageManager();
        // int initresult = storage.init(getApplicationContext());
        Log.i("SKScanner", "CapAct: Line 347 _processRecognizedKey.");
        // if ((initresult == StorageManager.NO_ERRORS) || (initresult ==
        // StorageManager.NOT_MOUNTED)) {
        // Authenticate key on SignaKey service.
        new AsyncTask<Void, Void, WebMarkAuthenticateResult>() {
            protected void onPreExecute() {

                mProgressDialog = new ProgressDialog(SKScanner.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Processing...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
                Log.i("SKScanner",
                        "CapAct: Line 358 _processRecognizedKey Async Task Setup.");
            }

            protected WebMarkAuthenticateResult doInBackground(Void... params) {
                // Wait end of login.
                Log.i("SKScanner",
                        "CapAct: Line 362 _processRecognizedKey. Background task");

                // Auth key.
                WebMarkAuthenticateResult result = null;
                Log.i("SKScanner", "CapAct: Line 367 _processRecognizedKey. ");
                if (true) {
                    int tryCount = 0;
                    while ((result == null) && (tryCount < 2)) {
                        Log.i("SKScanner",
                                "CapAct: Try to authenticate symbol (attempt "
                                        + (tryCount + 1) + " of 2)");
                        Log.i("SKScanner", "CapAct: result =" + result);
                        result = mSkClient.authenticateSymbol(keyBuffer);
                        Log.i("SKScanner",
                                "CapAct: Line 374 _processRecognizedKey. result = "
                                        + result);
                        if (result == null) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        tryCount++;
                    }
                }

                CaptureActivity.globalresult = result;
                return result;
            }

            ;

            protected void onPostExecute(WebMarkAuthenticateResult result) {
                mProgressDialog.dismiss();
                String InData = "DecodeResultfile.txt";
                FileInputStream mInput = null;
                File file = getBaseContext().getFileStreamPath(InData);
                if (file.exists()) {
                    try {
                        mInput = openFileInput(InData);
                        Log.i("SKScanner",
                                "CapAct: Line 409 input file path =  "
                                        + mInput.toString());
                        byte[] data = new byte[128];
                        for (int x = 0; x < data.length; x++) {
                            data[x] = (byte) mInput.read();
                            if (data[x] == -1) { // -1 means end of data
                                break;
                            }

                        }

                        mInput.close();

                    } catch (FileNotFoundException e) {
                        Log.i("SKScanner",
                                "CapAct: Line 412 FileNotFoundException =  "
                                        + e);
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.i("SKScanner", "CapAct: Line 415 IOException =  "
                                + e);
                        e.printStackTrace();
                    }

                    File barcode = new File(InData);
                    barcode.delete();
                }
                Log.i("SKScanner",
                        "CapAct: Line 422 _processRecognizedKey. onPostExecute result = "
                                + result);
                // Make meta string from key.
                String meta = SignaKey.keyBufferToMeta(keyBuffer);

                if ((result == null) || (result.Common.WebResultCode != 0)) {
                    Log.i("SKScanner",
                            "CapAct: Error during authentication of key.");
                    Toast.makeText(SKScanner.this,
                            "Error during authentication of key.",
                            Toast.LENGTH_LONG).show();
                    meta = meta
                            + SignaKeyClient.Company
                            + "\nWeb Result code failed in ScanCamera. Result = "
                            + result;
                    if (result != null) {
                        Log.i("SKScanner", "CapAct: Web result code = "
                                + result.Common.WebResultCode + ".");
                        meta = meta + "\nWeb result code = "
                                + result.Common.WebResultCode + ".\n";
                        decodesuccess = false;
                    }
                } else {
                    Log.i("SKScanner", "CapAct: Authenticate status = "
                            + result.Common.ServiceResultMessage + " ("
                            + result.Common.ServiceResultSuccess + ").");
                    meta = meta + SignaKeyClient.Company + "\n" + "STATUS: "
                            + result.Common.ServiceResultSuccess
                            + "\n 2D Decode = " + DataMatrixResult;
                    if (!result.Common.ServiceResultSuccess) {
                        meta = meta
                                + "\nStatus = "
                                + (result.Common.ServiceResultMessage + " ("
                                + result.Common.ServiceResultSuccess + ").");
                    } else {
                        if (result.ItemList.get(result.TargetIndex).KeyText != "") {
                            meta = meta
                                    + "\nKey Text = "
                                    + result.ItemList.get(result.TargetIndex).KeyText
                                    + "\n";
                        }
                        if (result.ItemList.get(result.TargetIndex).IsContained) {
                            int icount = result.ItemList.size() - 1;
                            meta = meta + "\nIn Container ="
                                    + result.ItemList.get(0).KeyText + " with "
                                    + icount + " items.\n";
                        }
                        if (result.ItemList.get(result.TargetIndex).IsContainer) {
                            int icount = result.ItemList.size() - 1;
                            meta = meta + "\nIs Container= "
                                    + result.ItemList.get(0).KeyText + " with "
                                    + icount + " items.\n";
                        }
                        if (result.ItemList.get(result.TargetIndex).PublicText != "") {
                            meta = meta
                                    + "\nPublic Text = "
                                    + result.ItemList.get(result.TargetIndex).PublicText;
                        }
                        if (result.ItemList.get(result.TargetIndex).ConsumerText != "") {
                            meta = meta
                                    + "\nConsumer Test = "
                                    + result.ItemList.get(result.TargetIndex).ConsumerText;
                        } // ConsumerText
                        if (result.ItemList.get(result.TargetIndex).RestrictedText != "") {
                            meta = meta
                                    + "\nRestrictedText = "
                                    + result.ItemList.get(result.TargetIndex).RestrictedText;
                        } // RestrictedText
                        if (result.ItemList.get(result.TargetIndex).PrivateText != "") {
                            meta = meta
                                    + "\nPrivate Text = "
                                    + result.ItemList.get(result.TargetIndex).PrivateText
                                    + "\n";
                        } // PrivateText
                        SKScanner._pubmediafiles = 0;
                        if (result.ItemList.get(result.TargetIndex).PublicFileList != null) {
                            meta = meta
                                    + "\n "
                                    + result.ItemList.get(result.TargetIndex).PublicFileList
                                    .size()
                                    + " Public Media files attached"; // PublicFileList
                            SKScanner._pubmediafiles = result.ItemList
                                    .get(result.TargetIndex).PublicFileList
                                    .size();


                        }
                        SKScanner._conmediafiles = 0;
                        if (result.ItemList.get(result.TargetIndex).ConsumerFileList != null) {
                            meta = meta
                                    + "\n "
                                    + (result.ItemList.get(result.TargetIndex).ConsumerFileList
                                    .size())
                                    + " Consumer Media files attached"; // ConsumerFileList
                            SKScanner._conmediafiles = result.ItemList
                                    .get(result.TargetIndex).ConsumerFileList
                                    .size();
                        }
                        SKScanner._resmediafiles = 0;
                        if (result.ItemList.get(result.TargetIndex).RestrictedFileList != null) {
                            meta = meta
                                    + "\n "
                                    + (result.ItemList.get(result.TargetIndex).RestrictedFileList
                                    .size())
                                    + " Restricted Media files attached";
                            SKScanner._resmediafiles = result.ItemList
                                    .get(result.TargetIndex).RestrictedFileList
                                    .size();
                        } // RestrictedFileList
                        SKScanner._primediafiles = 0;
                        if (result.ItemList.get(result.TargetIndex).PrivateFileList != null) {
                            meta = meta
                                    + "\n "
                                    + (result.ItemList.get(result.TargetIndex).PrivateFileList
                                    .size())
                                    + " Private Media files attached";
                            SKScanner._primediafiles = result.ItemList
                                    .get(result.TargetIndex).PrivateFileList
                                    .size();
                        }
                        decodesuccess = true;
                    }
                }

                // Prepare bitmap and meta data.
                String InLogo = "Logo.bmp";
                FileInputStream fis = null;
                try {
                    fis = openFileInput(InLogo);
                    Log.i("SKScanner", "CapAct: File Open Result fis= " + fis);
                } catch (FileNotFoundException e) {
                    Log.i("SKScanner", "CapAct: FileNotFoundException e " + e);
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(fis);

                SignaKey k = new SignaKey(bitmap, SignaKey.TYPE_SIGNAKEY, meta,
                        keyBuffer);
                StorageManager storage = new StorageManager();
                storage.init(getApplicationContext());
                if (k.storeTo(storage.getBaseDir()) == false) {
                    Toast.makeText(getApplicationContext(),
                            "CaptureActivity: Can't save key to history.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(),
                            KeyViewer.class);
                    intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO,
                            k.getAssociatedPath());
                    startActivity(intent);
                }

                // Prepare result.
                Intent intent = new Intent(getApplicationContext(),
                        KeyViewer.class);
                intent.putExtra(EXTRA_PATH_TO_INFO, k.getAssociatedPath());
                // if(result.Common.ServiceResultSuccess){
                SKScanner.this.setResult(Activity.RESULT_OK, intent);
                _mediafiles = _pubmediafiles + _conmediafiles + _resmediafiles
                        + _primediafiles;
                if (_mediafiles != 0) {
                    Button _media = (Button) findViewById(R.id.btn_media);
                    _media.setEnabled(true);

                    // _media.setBackgroundColor(Color.GRAY);
                }
                Log.d("Size", "" + _mediafiles);
                SessionData.getInstance().setMediafiles(_mediafiles);
                // }
                if (result.Common.WebResultCode == -800) {
                    // RESULT_CANCELED= result.Common.WebResultCode; No Access
                    SKScanner.this.setResult(Activity.RESULT_CANCELED,
                            intent);
                }
                //	SKScanner.this.finish();
            }
        }.execute();

        Log.i("SKScanner",
                "CapAct: Line 511 Return from Web Authentication *********************** ");
        return decodesuccess;
    }

    /**
     * Get path for image picked media gallery.
     *
     * @param uri is an URI to data from media gallery.
     * @return Path to image as string.
     */
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        String result = null;
        if (cursor != null) { // cursor can be null, if OI file manager is used
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public void UpdateButtons() {
        // Button button = (Button) findViewById(R.id.btn_scan_camera);
        _initScanCamButton();
        return;

    }

    public void onBackPressed() {
        super.onBackPressed();
        // finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }

    ;

    // Back button pressed
    // public boolean onKeyDown(int keyCode, KeyEvent event){
    // return super.onKeyDown(keyCode, event);
    // }
    @Override
    protected void onPause() {
        super.onPause();

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


//    private void function() {
//
//        String user="getter";
//        String user2="getter";
//
//        SharedPreferences Preferences = PreferenceManager
//                .getDefaultSharedPreferences(getBaseContext());
//         Preferences.getString("",user);
//         Preferences.getString("",user2);
//        Log.d(TAG, "function: "+user+""+user2+"  ");
//    }
//    SharedPreferences

    public boolean getGps2() {
        boolean x = false;
        gps = new GPSTracker(SKScanner.this);
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
    public boolean getGpsScan() {
        boolean x = false;
        gps = new GPSTracker(SKScanner.this);
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
            showSettingsAlert2();
        }
        return x;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SKScanner.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i("SKScanner",
                        "SKScanner: Line 258 User is successfully validated on SignaKey.");
                isLoggedIn = true;
                Intent intent = new Intent(getApplicationContext(),
                        CaptureActivity.class);


                SessionData.getInstance().setScanhandler(0);
                startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public void showSettingsAlert2() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SKScanner.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i("SKScanner",
                        "SKScanner: Line 258 User is successfully validated on SignaKey.");
                isLoggedIn = true;
                Intent intent = new Intent(getApplicationContext(),
                        CaptureActivity.class);


                SessionData.getInstance().setScanhandler(0);
                startActivityForResult(intent, REQUEST_CAPTUREACTIVITY);

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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


    public void scanImage(Intent data, SharedPreferences preferences, int mode) {
        {

//            _loginsignakey2sk2();
//                getGps2();
            Uri selectedImageUri = data.getData();
            boolean isRecognized = false;

            String pathTmp = getPath(selectedImageUri); // MEDIA GALLERY
            if (pathTmp == null)
                pathTmp = selectedImageUri.getPath(); // OI FILE Manager
            final String path = pathTmp;

            int signakey_symbol = Integer.parseInt(
                    preferences.getString("signakey_symbol", "1"), 10);
            // "SignaKey" or "SignaKey and Standard".
            if ((mode == 1) || (mode == 3)) {
                Log.i("SKScanner",
                        "SKScanner: Line 317 Try to recognize code using SignaKey library.");
                int bg_mode = Integer.parseInt(
                        preferences.getString("signakey_bg_mode", "1"), 10);
                SignaKeyRecognizer recognizer = new SignaKeyRecognizer();
                byte[] keyBufferBgBlack = new byte[256];
                byte[] keyBufferBgWhite = new byte[256];
                int confidenceBgBlack = -1;
                int confidenceBgWhite = -1;
                switch (bg_mode) {
                    case 3:
                        confidenceBgBlack = recognizer.recognizeFromPath(path,
                                keyBufferBgBlack, false, true, signakey_symbol);
                        confidenceBgWhite = recognizer
                                .recognizeFromPath(path, keyBufferBgWhite,
                                        false, false, signakey_symbol);
                        break;
                    case 2:
                        confidenceBgBlack = recognizer.recognizeFromPath(path,
                                keyBufferBgBlack, false, true, signakey_symbol);
                        break;
                    case 1:
                    default:
                        confidenceBgWhite = recognizer
                                .recognizeFromPath(path, keyBufferBgWhite,
                                        false, false, signakey_symbol);
                        break;
                }

                final int confidence = (confidenceBgBlack > confidenceBgWhite) ? confidenceBgBlack
                        : confidenceBgWhite;
                final byte[] keyBuffer = (confidenceBgBlack > confidenceBgWhite) ? keyBufferBgBlack
                        : keyBufferBgWhite;
                Log.i("SKScanner", "SKScanner Line 340 confidence = "
                        + confidence);

                if (confidence == -1) {
                    Toast.makeText(getApplicationContext(),
                            "Decode Image failed",
                            Toast.LENGTH_LONG).show();
                }

                if (confidence > SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD) {
                    mBeepManager.playBeepSoundAndVibrate();
                    // Show message.
                    Log.i("SKScanner",
                            "SKScanner Line 346 Key is recognized with confidence "
                                    + confidence + ".");
                    _processRecognizedKey(keyBuffer);
                }
            }
        }
    }

}