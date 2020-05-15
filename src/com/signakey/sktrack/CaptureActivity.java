package com.signakey.sktrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bad_elf.badelfgps.BadElfDevice;
import com.bad_elf.badelfgps.BadElfGpsConnection;
import com.bad_elf.badelfgps.BadElfGpsConnectionObserver;
import com.bad_elf.badelfgps.BadElfService;
import com.cvisionlab.zxing.ZXingKey;
import com.google.zxing.Result;
import com.signakey.sktrack.skclient.GPSTracker;
import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.SessionData;
import com.signakey.sktrack.skclient.WebMarkAuthenticateResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.signakey.sktrack.CameraManager.camera;

//import github.nisrulz.lantern.Lantern;


class MyDebug {
    static final boolean LOG = false;
}

public final class CaptureActivity extends Activity implements
        SurfaceHolder.Callback, BadElfGpsConnectionObserver {
    ToggleButton tButton;

    double latitude = 0;
    double longitude = 0;
    GPSTracker gps;
    String GPScordinates;

    String tempData = "";
    private static final String TAG = "BadelfGpsTracker";

    private Button connectDisconnectButton;
    private TextView stateView;
    //	private List<Button> requestButtons; // The 10 request buttons
    private TextView receivedDataView;
    private StringBuilder strReceivedDataView;
    private TextView txtLatlong;
    SharedPreferences mPreferences;
    SharedPreferences.Editor mPreferencesEditor;
    String locationId = "";
    String taskId = "";
    String gpsAccuracy = "";

    private BadElfDevice badElfDevice;  // The Bad Elf device we will communicate with
    private BadElfGpsConnection badElfConnection;
    String gpsPref = "";

    private BeepManager mBeepManager = null;
    private ProgressDialog mProgressDialog;
    private SignaKeyClient mSkClient = null;
    private boolean decodesuccess = false;
    //private boolean gendb = false;
    public String meta;
    public String decode;

    SurfaceHolder surfaceHolder;
    private boolean meteringAreaSupported;

    private Camera mCamera;

    Logger logger;
    //	public WebGenDbResult webgenresult = null;
//	Switch switchcontrols;
//	public String atr;
//	public String finalstr;
    int _mediafiles = 0;
//	public String str;
//	public String genResult;
//	static Camera camera = null;
//	Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    SeekBar msliderzoom;
    float discrete = 0;
    float start = 0;
    float end = 100;
    float start_pos = 0;
    int start_position = 0;

    private static final int FOCUS_AREA_SIZE = 300;
//	public String sucess;
//	public String keyword;
//	int MAX_ZOOM = 200;

    public static final String EXTRA_PATH_TO_INFO = "extra_path_to_info";
    // public static TextView mZoom;

    // ViewFinderView
    //private final static String TAG = "SimpleTorch";

    private boolean hasSurface;

    public static String DataMatrixResult = " ";
    //	int currentZoomLevel = 0, maxZoomLevel = 0;
//	public String outText;
    // Decoder
    private CaptureActivityHandler handler;
    ZXingKey decoder;
    public static WebMarkAuthenticateResult globalresult = null;
    //	public static DemoItem demoitem = null;
    //public WebMarkDecodeSymbolsResult decoderesult = null;
    TextView zoomtext, zom;
    ImageView right, left;
    Context context = this;

    SurfaceView surfaceView;
    private boolean firstClick = true;
    private boolean connected = false;
//	int version, client, item, sequence;
//	public String keywordname, serial, stringpattern, keywordgendb;

    /*
     * private void SetZoomLevel() { int myZoomLevel = msliderzoom.getProgress()
     * + 1;
     *
     * Toast.makeText(this, "Zoom Level : " + String.valueOf(myZoomLevel),
     * Toast.LENGTH_SHORT).show(); };
     */
    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	@SuppressLint("ClickableViewAccessibility")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_camera);

//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//					.permitAll().build();
//			StrictMode.setThreadPolicy(policy);

        Toast toast = Toast.makeText(this, "Tap the area you wish to that you wish to focus", Toast.LENGTH_LONG);
        toast.show();
        cd = new ConnectionDetector(getApplicationContext());
        start = 0; // you need to give starting value of SeekBar
        end = 5; // you need to give end value of SeekBar
        start_pos = 0;
        zom = (TextView) findViewById(R.id.zoomtext);
        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        PackageManager packageManager = context.getPackageManager();


        zoomtext = (TextView) findViewById(R.id.zoom);
        // msliderzoom = (SeekBar)findViewById(R.id.level_seekbar);
        tButton = (ToggleButton) findViewById(R.id.toggleButton1);

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            //yes
            Log.i("camera", "This device has flash supported!");
            tButton.setVisibility(View.VISIBLE);
        } else {
            //no
            Log.i("camera", "This device has no flash support!");
            tButton.setVisibility(View.GONE);

        }


        right = (ImageView) findViewById(R.id.right_background);
        left = (ImageView) findViewById(R.id.left_background);
        msliderzoom = (SeekBar) findViewById(R.id.seekBar1);

        /* switchcontrols = (Switch) findViewById(R.id.switch1); */

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int mode = Integer.parseInt(preferences.getString("mode", "3"), 10);
        Log.i("SKScanner", "CapAct: Line 77 onCreate .");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView modeTitle = (TextView) findViewById(R.id.mode);

        Log.i("SKScanner", "CapAct: Line 83 setContentView layout capture.");
        CameraManager.init(getApplication());

        // onResume();

        if (mode == 2) {
            modeTitle.setText("Standard");
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
        } else if (mode == 1 || mode == 3) {
            modeTitle.setText("SignaKey");
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);

        }

        /**
         * SKScannerSDK
         */
        mBeepManager = new BeepManager(this);
        decoder = new ZXingKey(this);

        //	initCamera(surfaceHolder);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
//		surfaceView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event){
//				Parameters parameters = CameraManager.camera.getParameters();
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//
//
//					Camera.Parameters p = CameraManager.camera.getParameters();
//
//					if (p.getFlashMode().contains("auto")){
//
//						focusOnTouch(event);
////						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
////						CameraManager.camera.setParameters(parameters);
////						CameraManager.camera.startPreview();
//						SessionData.getInstance().setScanhandler(1);
//					//	camera.cancelAutoFocus();
//					}else {
//
//						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
//
//						CameraManager.camera.setParameters(parameters);
//						CameraManager.camera.startPreview();
//						SessionData.getInstance().setScanhandler(1);
//					}
//
//					//Toast.makeText(CaptureActivity.this, "Flash Mode "+ parameters.getFlashMode() ,Toast.LENGTH_SHORT).show();
//
////					Camera.Parameters p = CameraManager.camera.getParameters();
////
////					Toast toast = Toast.makeText(getBaseContext(), "On_Touch " + p.getFlashMode(), Toast.LENGTH_LONG);
////					toast.show();
//
////					if (p.getFlashMode().contains("auto")){
////
////						camera.cancelAutoFocus();
////
////						Toast toasts = Toast.makeText(getBaseContext(), "On_Touch Auto_focus cancelled " + p.getFlashMode(), Toast.LENGTH_LONG);
////						toasts.show();
////					}
//
//				}
//				return true;
//			}
//		});


        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //	Camera.Parameters p = CameraManager.camera.getParameters();
                    //if (p.getFlashMode().contains("auto")){

                    //SessionData.getInstance().setAutoFocus(1);

                    CameraManager.focusOnTouch(event);
                    //	initCamera(surfaceHolder);

                    SessionData.getInstance().setScanhandler(1);
                    //   onResume();


                }
                return false;
            }

        });

//		surfaceView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					//SessionData.getInstance().setAutoFocus(1);
//					CameraManager.focusOnTouch(event);
//					hasSurface = true;
//					handler.restartPreviewAndDecode();
////					final Parameters params = CameraManager.camera.getParameters();
////
////					params.setFocusMode(Parameters.FOCUS_MODE_FIXED);
////
////					CameraManager.camera.setParameters(params);
//					SessionData.getInstance().setScanhandler(1);
//
//					Camera.Parameters p = CameraManager.camera.getParameters();
////
//					Toast toast = Toast.makeText(getBaseContext(), "On_Touch " + p.getFlashMode(), Toast.LENGTH_LONG);
//					toast.show();
//
//				}
//				return false;
//			}
//		});

//        if(SessionData.getInstance().getGpsSelection().equals("Bad Elf GPS")){
//            connectDisconnectButton.setVisibility(View.VISIBLE);
//            stateView.setVisibility(View.VISIBLE);
//            txtLatlong.setVisibility(View.VISIBLE);
//        }else{
//            connectDisconnectButton.setVisibility(View.GONE);
//            stateView.setVisibility(View.GONE);
//            txtLatlong.setVisibility(View.GONE);
//
//        }

        //badelfgps

        mPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        mPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();

        mPreferencesEditor.apply();
        initBadelfgps();
        Log.d(TAG, "initBadelfgps lat:1101.5028 => " + decimalToDMS(1101.5028));
        Log.d(TAG, "initBadelfgps long:07657.5192 => " + decimalToDMS(07657.5192));
    }


    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

        final Parameters params = camera.getParameters();
        logger.addLog("Camera Started");
        camera.setParameters(params);
        camera.startPreview();
        List<String> flashModes = params.getSupportedFlashModes();
//		Toast.makeText(CaptureActivity.this, "Flash Mode "+ params.getFlashMode() + flashModes.size() ,Toast.LENGTH_SHORT).show();

//		android.hardware.camera2.CameraManager manager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
//		try {
//			for(String camera_id : manager.getCameraIdList()){
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				Log.d("Cam", "Available Cameras: id: " + camera_id + " and rear facing = " + (manager.getCameraCharacteristics(camera_id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK));
//
//				if ((manager.getCameraCharacteristics(camera_id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK)){
//					Toast.makeText(context, "Available Cameras: id:"+ camera_id, Toast.LENGTH_SHORT).show();
//
//				}else {
//					Toast.makeText(context, "Front Cameras: id:"+ camera_id, Toast.LENGTH_SHORT).show();
//				}
//			}
//		}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


//		String backCameraId = null;
//		android.hardware.camera2.CameraManager manager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
//		try {
//
//			if (manager != null) {
//
//				int length = manager.getCameraIdList().length;
//				System.out.println("Camera List " + length);
////				Toast.makeText(context, "Camera List Count : "+ length, Toast.LENGTH_SHORT).show();
//
//				for(String cameraId  :  manager.getCameraIdList()){
//					CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
//					Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
//					if(facing == CameraMetadata.LENS_FACING_BACK) {
//						backCameraId = cameraId;
//						break;
//					}
//				}
//			}
//		} catch (CameraAccessException e) {
////			String s = e.toString();
////			Toast.makeText(context, "Exception" + s, Toast.LENGTH_SHORT).show();
//			e.printStackTrace();
//		}
//		Toast.makeText(context, "back camera exists ? "+ (backCameraId!=null) , Toast.LENGTH_SHORT).show();

//		Log.d("back", "back camera exists ? "+ (backCameraId!=null)) ;
//
////		Toast.makeText(context, "back camera id "+ backCameraId , Toast.LENGTH_SHORT).show();
//
//		Log.d("back camera id", "back camera id  : " + backCameraId);


//		for(String camera_id : android.hardware.camera2.CameraManager.getCameraIdList()){
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				Log.d(TAG, "Available Cameras: id: " + camera_id + " and rear facing = " + (android.hardware.camera2.CameraManager.getCameraCharacteristics(camera_id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK));
//			}
//		}

        final List<Integer> ratios = params.getZoomRatios();


        Camera.Parameters p = CameraManager.camera.getParameters();

//					if (p.getFlashMode().contains("auto")){
//
//
//						tButton.setVisibility(View.GONE);
//					}else {
//
//						tButton.setVisibility(View.VISIBLE);
//					}

        tButton.setChecked(false);
        //params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        tButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                List<String> flashModes = params.getSupportedFlashModes();


                if (isChecked) {


                    if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
                        params.setFlashMode(Parameters.FLASH_MODE_TORCH);

//							Toast toast = Toast.makeText(getBaseContext(), "Inside FLASH_MODE_TORCH", Toast.LENGTH_LONG);
//							toast.show();


                    }
                } else {


                    params.setFlashMode(Parameters.FLASH_MODE_OFF);

//					Toast toast = Toast.makeText(getBaseContext(), "Inside FLASH_MODE_OFF", Toast.LENGTH_LONG);
//					toast.show();

                }
                //Toast.makeText(CaptureActivity.this, "Flash Mode "+ params.getFlashMode() ,Toast.LENGTH_SHORT).show();
//            if ( params.getFlashMode().contains("torch")){
//
//	              context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//	              CameraManager.camera.setParameters(params);
//	              CameraManager.camera.startPreview();
//             }
                camera.setParameters(params);

                camera.startPreview();

//				Toast toast = Toast.makeText(getBaseContext(), "Outside " + params.getFlashMode(), Toast.LENGTH_LONG);
//				toast.show();

                Parameters p = camera.getParameters();

//				Toast toast = Toast.makeText(getBaseContext(), "Outside " + p.getFlashMode(), Toast.LENGTH_LONG);
//				toast.show();

            }

        });
        msliderzoom.setProgress(start_position);

        start = 0; // you need to give starting value of SeekBar
        end = 5; // you need to give end value of SeekBar
        start_pos = 0;
        start_position = (int) (((start_pos - start) / (end - start)) * 100);
        discrete = start_position;
        msliderzoom
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                        // Toast.makeText(getBaseContext(), "Zoom Level= " +
                        // String.valueOf(discrete), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {

                        // zoomtext.setText(String.valueOf(progress));

                        // TODO Auto-generated method stub
                        // SetZoomLevel();
                        /*
                         * float temp = progress; float dis = end - start;
                         * discrete = (start + ((temp / 100) * dis));
                         */
                        if (camera.getParameters()
                                .isZoomSupported()) {

//							Parameters params = CameraManager.camera
//									.getParameters();
                            params.setZoom(progress);
                            params.getSupportedPreviewSizes();
                            camera.setParameters(params);

                        }

                    }

                });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {


            Log.d("Rotate", "" + getResources().getConfiguration().orientation);


        }

        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                System.out.println("SCREEN_ORIENTATION_PORTRAIT");
                camera.setDisplayOrientation(90);
                break;

            case Surface.ROTATION_90:
                System.out.println("SCREEN_ORIENTATION_LANDSCAPE");
                camera.setDisplayOrientation(0);
                break;

            case Surface.ROTATION_180:
                System.out.println("SCREEN_ORIENTATION_REVERSE_PORTRAIT");
                break;

            case Surface.ROTATION_270:
                System.out.println("SCREEN_ORIENTATION_REVERSE_LANDSCAPE");
                camera.setDisplayOrientation(180);
                break;
        }
        refreshCamera();
        //	Toast.makeText(context, "After Refresh", Toast.LENGTH_SHORT).show();

    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
//		try {
//			CameraManager.camera.stopPreview();
//		} catch (Exception e) {
//			// ignore: tried to stop a non-existent preview
//		}

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            CameraManager.camera.setPreviewDisplay(surfaceHolder);
            CameraManager.camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        if (!hasSurface) {
            Log.i("SKScanner", "CapAct: Line 174 surfaceCreated.");
            hasSurface = true;

            initCamera(arg0);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        hasSurface = false;
        Log.i("SKScanner", "CapAct: Line 183 surfaceDestroyed.");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("SKScanner", "CapAct: Line 189 onResume.");
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_camera);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        CameraManager.init(getApplication());

        decoder = new ZXingKey(this);
        if (hasSurface) {

            Log.i("SKScanner", "CapAct: Line 195 onResume hasSurface."
                    + hasSurface);
//			hasSurface = true;
//			handler.restartPreviewAndDecode();
            initCamera(surfaceHolder);
        } else {

            Log.i("SKScanner", "CapAct: Line 199 onResume hasSurface."
                    + hasSurface);
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("SKScanner", "CapAct: Line 208 onPause.");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }


    public Handler getHandler() {
        return handler;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        Log.i("SKScanner", "CapAct: Line 230 onConfigurationChanged.");
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {

            CameraManager.get().openDriver(surfaceHolder);
            Log.i("SKScanner",
                    "CapAct Line 236 openDriver & initializating camera");
        } catch (IOException ioe) {

            return;
        } catch (RuntimeException e) {
            Log.i("SKScanner",
                    "CapAct Line 241 Unexpected error initializating camera", e);
            return;
        }

        if (!gpsPref.equals("Bad Elf GPS")) {
            initCameraScanHandler();
        } else if (connected) {
            initCameraScanHandler();
        }

        Log.i("SKScanner", "CapAct: Line 247 initCamera.");
    }

    public void drawViewfinder() {

        Log.i("SKScanner", "CapAct: Line 252  not this time drawViewFinder.");
    }

    public void handleDecode(Result rawResult) {

        Log.i("SKScanner", "CapAct: Line 257 handleDecode rawResult.text = ."
                + rawResult.getText());


        Log.i("SKScanner", "CapAct: Line 280 Barcode decoded show message."
                + rawResult.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));

        if (rawResult.getText() == "SignaKey Decode") {
            mBeepManager.playBeepSoundAndVibrate();
            _processRecognizedKey(rawResult.getRawBytes());

        } else {
            mBeepManager.playBeepSoundAndVibrate();
            _savebardcode(rawResult);
            DataMatrixResult = rawResult.getText();
            builder.setMessage("Barcode Decoded. Format "
                    + rawResult.getBarcodeFormat().name() + "\n"
                    + rawResult.getText());
            logger.addLog("SKScanner: " + "Barcode Decoded. Format : " + rawResult.getBarcodeFormat().name() + "\n"
                    + rawResult.getText());

            builder.setNegativeButton("ok", okListener);
            builder.show();

        }


    }

    private final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {


            Intent intent = new Intent();

            CaptureActivity.this.setResult(Activity.RESULT_OK, intent);
            CaptureActivity.this.finish();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.i("SKScanner", "CapAct: Line 326 onCreateOptionsMenu.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("SKScanner", "CapAct: Line 332 onOptionsItemSelected.");
        return super.onOptionsItemSelected(item);
    }

    public void getGps() {
        gps = new GPSTracker(CaptureActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            GPScordinates = getChainedGpsStringBuilder().toString();

//            GPScordinates = "?*GPScoordinates=" + latitude + "," + longitude + "&*LocationID=" +locationId+"&*GPSaccuracy="+gpsAccuracy+"&*TaskID="+taskId;

            SessionData.getInstance().setGpsCoordinates(GPScordinates);

            System.out.println("Capture Activity Coordinates lat " + latitude + " long " + longitude);
            System.out.println("Capture Activity GPScordinates " + GPScordinates);

//            Toast.makeText(CaptureActivity.this, GPScordinates, Toast.LENGTH_SHORT).show();


        } else {
            gps.showSettingsAlert();
        }


    }
//	public void getbadgps(){
//		badelfgps=new BadElfService(CaptureActivity.this);
//		if (badelfgps.canGetLocation()) {
//			latitude = badelfgps.getLatitude();
//			longitude = badelfgps.getLongitude();
//
//
//			GPScordinates = "?*GPScoordinates="+latitude+","+longitude;
//
//			SessionData.getInstance().setGpsCoordinates(GPScordinates);
//
//			System.out.println("Capture Activity Coordinates lat "+latitude +" long "+ longitude);
//
//			Toast.makeText(CaptureActivity.this,GPScordinates,Toast.LENGTH_SHORT).show();
//
//
//		} else {
//			badelfgps.showSettingsAlert();
//		}
//	}


    @SuppressLint("StaticFieldLeak")
    public boolean _processRecognizedKey(final byte[] keyBuffer) {

        mSkClient = new SignaKeyClient(this);
        if (!gpsPref.equals("Bad Elf GPS")) {

            getGps();
        }
//		getbadgps();

        Log.i("SKScanner", "CapAct: Line 347 _processRecognizedKey.");

        new AsyncTask<Void, Void, WebMarkAuthenticateResult>() {
            protected void onPreExecute() {

                mProgressDialog = new ProgressDialog(CaptureActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Processing live camera image ...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                try {
                    mProgressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("SKScanner",
                        "CapAct: Line 358 _processRecognizedKey Async Task Setup.");
            }

            protected WebMarkAuthenticateResult doInBackground(Void... params) {

                Log.i("SKScanner",
                        "CapAct: Line 362 _processRecognizedKey. Background task");


                WebMarkAuthenticateResult result = null;
                Log.i("SKScanner", "CapAct: Line 367 _processRecognizedKey. ");
                if (true) {
                    int tryCount = 0;
                    while ((result == null) && (tryCount < 2)) {
                        Log.i("SKScanner",
                                "CapAct: Try to authenticate symbol (attempt "
                                        + (tryCount + 1) + " of 2)");
//                        Log.i("SKScanner", "CapAct: result =" + result);
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

                globalresult = result;
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
                    Toast.makeText(CaptureActivity.this,
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

                            logger.addLog("SKScanner: " + "Result key Text" + result.ItemList.get(result.TargetIndex).KeyText);
                        }
                        if (result.ItemList.get(result.TargetIndex).IsContained) {
                            int icount = result.ItemList.size() - 1;
                            meta = meta + "\nIn Container ="
                                    + result.ItemList.get(0).KeyText + " with "
                                    + icount + " items.\n";

                            logger.addLog("SKScanner: " + "Capture Activity, Result count" + result.ItemList.get(0).KeyText + " with "
                                    + icount + " items.");
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
                                    + " Public Media files attached";

                            Log.d("Public Media files", "" + result.ItemList.get(result.TargetIndex).PublicFileList
                                    .size());
                            // PublicFileList
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


                        Log.d("Size for media", "" + _mediafiles);
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
                    logger.addLog("SKScanner: " + "Capture Activity, Error" + e);

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
                    if (result.RecoverableFailure != null) {
                        if (result.RecoverableFailure) {
                            Intent intent = new Intent(getApplicationContext(),
                                    CaptureActivity.class);
                            intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO,
                                    k.getAssociatedPath());
                            startActivity(intent);
//                            CaptureActivity.this.finish();


                        } else {

                            Intent intent = new Intent(getApplicationContext(),
                                    KeyViewer.class);
                            intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO,
                                    k.getAssociatedPath());
                            startActivity(intent);

                            CaptureActivity.this.finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(),
                                CaptureActivity.class);
                        intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO,
                                k.getAssociatedPath());
                        startActivity(intent);
//                        CaptureActivity.this.finish();

                    }

                }
                if (result.RecoverableFailure != null)

                // Prepare result.
                {
                    if (result.RecoverableFailure) {
                        Intent intent = new Intent(getApplicationContext(),
                                CaptureActivity.class);
                        intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO,
                                k.getAssociatedPath());
                        startActivity(intent);
//                        CaptureActivity.this.finish();

                    } else {
                        Intent intent = new Intent(getApplicationContext(),
                                KeyViewer.class);
                        intent.putExtra(EXTRA_PATH_TO_INFO, k.getAssociatedPath());
                        // if(result.Common.ServiceResultSuccess){
                        CaptureActivity.this.setResult(Activity.RESULT_OK, intent);
                        if (result.Common.WebResultCode == -800) {

                            // RESULT_CANCELED= result.Common.WebResultCode; No Access
                            CaptureActivity.this.setResult(Activity.RESULT_CANCELED,
                                    intent);
                        }
                        CaptureActivity.this.finish();

                        logger.addLog("Camera Stopped ");
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(),
                            CaptureActivity.class);
                    intent.putExtra(KeyViewer.EXTRA_PATH_TO_INFO,
                            k.getAssociatedPath());
                    startActivity(intent);
//                    CaptureActivity.this.finish();

                }
                // }


            }
        }.execute();

        Log.i("SKScanner",
                "CapAct: Line 511 Return from Web Authentication *********************** ");
        return decodesuccess;
    }

    private void _savebardcode(Result rawResult) {

        FileOutputStream mOutput;
        Log.i("SKScanner",
                "CapAct: Line 507 Save Barcode data." + rawResult.getText());
        try {
            int FileLength = 0;
            mOutput = openFileOutput("DecodeResultfile.txt", FileLength);
            mOutput.write(rawResult.toString().getBytes());
            Log.i("SKScanner",
                    "CapAct: Line 512 res =  " + rawResult.toString());
            mOutput.close();
        } catch (FileNotFoundException e) {
            Log.i("SKScanner", "CapAct: Line 515 file not found =  " + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("SKScanner", "CapAct: Line 518 IOException =  " + e);
            e.printStackTrace();
        }
        Log.i("SKScanner", "CapAct: Line 521 Save Barcode data.");
        return;
    }

//    @Override
//    protected void onStop() {
//        if (gpsPref.equals("Bad Elf GPS")) {
//            if (badElfConnection != null) {
//                badElfConnection.onDestroy();  // Destroy our link to the connection service.
//            }
//        }
//        super.onStop();
//
//    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (gpsPref.equals("Bad Elf GPS")) {
            badElfConnection.onDestroy();  // Destroy our link to the connection service.
        }
        // NOTE: If there is an active connection to the Bad Elf
        // Device this does not cause that to disconnect. To
        // disconnect from the Bad Elf Device call
        // badElfConnection.disconnect(). This way the connection
        // to the Bad Elf Device is not tied to the Activity Life
        // Cycle and will not be dropped when the android device
        // orientation changes.
    }

    // This is the index in requestButtons of the last request button pressed
    private int lastRequestIndex = -1;
    private static final String LAST_REQUEST_INDEX = "LAST_REQUEST_INDEX";

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(LAST_REQUEST_INDEX, lastRequestIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        lastRequestIndex = savedInstanceState.getInt(LAST_REQUEST_INDEX);
        if (lastRequestIndex != -1) {
//			highlightButton(requestButtons.get(lastRequestIndex));
        }
    }

    /**
     * Initialize request buttons and add them to the requestButtons array
     *
     * Find the buttons that are children of the given view id. For each button found add it to the
     * array, link the corresponding Request Enum, set the text, and set the listener.
     *
     * @param id The view id of the parent view of some request buttons
     * @param includeSatellites the request type matching the buttons in the parent view
     */
//	private void initializeRequestButtons(int id, boolean includeSatellites){
//		final ViewGroup buttonGroup = (ViewGroup)findViewById(id);
//		for (int i = 0; i < buttonGroup.getChildCount(); i++){
//			Button b = (Button)buttonGroup.getChildAt(i);
//
//			// Get the next request
//			BadElfGpsConnection.Request request = BadElfGpsConnection.Request.values()[requestButtons.size()];
//
//			// make sure it is the correct type
//			if (request.includeSatellites != includeSatellites) throw new AssertionError();
//
//			requestButtons.add(b);
//
//			b.setTag(request);  // Add the Request item to the button
//			b.setText(getString(R.string.badElfGpsRequestRate, request.rate)); // set the text of the button to the request rate
//			b.setOnClickListener(onRequestButtonClicked);
//		}
//
//	}

    /**
     * Called when the Connect/Disconnect button is pressed
     */
    private View.OnClickListener onConnectDisconnectButtonClicked = new View.OnClickListener() {
        public void onClick(View button) {
            if (badElfConnection.getState() == BadElfService.State.IDLE) {
                if (firstClick)
                // if not connected, Connect to the Bad Elf Device
                {
                    receivedDataView.setText(""); // clear old received data
                    badElfConnection.connect();
                    firstClick = false;
                } else {
//                    showRetryBadElfAlertForConnection();
//                    connectDisconnectButton.setVisibility(View.GONE);
                }

            } else {
                // if connected, disconnect
                badElfConnection.disconnect();
            }
        }
    };

    /**
     * Clear the highlighting on all the request buttons
     */
//	private void resetRequestButtonColor() {
//		for (Button b : requestButtons) {
//			b.getBackground().setColorFilter(null);
//		}
//	}

    /**
     * Highlight request button when pressed
     *
     * @param button The button to highlight
     */
    private void highlightButton(Button button) {
        button.getBackground().setColorFilter(new LightingColorFilter(0xFFC0ffC0, 0xFF002000));
    }

    /**
     * Called when a Request button is pressed.
     */
//	private View.OnClickListener onRequestButtonClicked = new View.OnClickListener() {
//		public void onClick(View view) {
//			Button button = (Button)view;
//			resetRequestButtonColor();
//			highlightButton(button);
//			lastRequestIndex = requestButtons.indexOf(button); // save the index so we can restore highlight after screen rotation
//
//			BadElfGpsConnection.Request request = (BadElfGpsConnection.Request)button.getTag(); // get the request
//
//			badElfConnection.sendData(request.data); // send the request data to the connection
//		}
//	};

    /**
     * This makes the activity exit when back is pressed
     */
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item){
//		int id = item.getItemId();
//
//		if (id==android.R.id.home) {
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

    /**
     * Update the State field, request buttons and connect/disconnect button when the connection
     * state changes.
     *
     * @param newState the new state
     */
    private void updateGui(final BadElfService.State newState) {
        final boolean isConnected = (newState == BadElfService.State.CONNECTED);
        final boolean isIdle = (newState == BadElfService.State.IDLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isIdle) {
                    stateView.setVisibility(View.GONE);
                    stateView.setText(newState.toString(CaptureActivity.this));

                } else {
                    stateView.setVisibility(View.GONE);
                }
//				if (requestButtons.get(0).isEnabled() != isConnected) {
//					// if the request buttons are not in the correct state, change them.
//					for (Button b : requestButtons) {
//						b.setEnabled(isConnected);
//					}
//				}

                if (isConnected) {
                    connectDisconnectButton.setVisibility(View.GONE);
                    SessionData.getInstance().setBadElfConnection(true);

                } else {
                    connectDisconnectButton.setVisibility(View.VISIBLE);
                    SessionData.getInstance().setBadElfConnection(false);
                    if (handler != null) {
                        handler.quitSynchronously();
                        handler = null;
                    }
                }
                if (isIdle) {
                    if (firstClick) {
                        connectDisconnectButton.setVisibility(View.VISIBLE);
                    } else {
                        connectDisconnectButton.setVisibility(View.GONE);
                        showRetryBadElfAlertForConnection();

                    }
                }
                connectDisconnectButton.setText(CaptureActivity.this.getResources().getString(
                        isIdle ? R.string.badElfGpsConnect : R.string.badElfGpsDisconnect
                ));

            }
        });

    }

    /**
     * Called when we are connected to the Service.
     * <p>
     * This does not mean we are connected to the Bad Elf Device. The call to
     * badElfConnection.getState() will tell us if we are connected to a Bad Elf Device
     */
    @Override // this is a method of the BadElfGpsConnectionObserver
    public void onReady() {
        Log.d(TAG, "onReady");
        if (gpsPref.equals("Bad Elf GPS")) {
            if (badElfDevice != null) {
                updateGui(badElfConnection.getState());
                connectDisconnectButton.setEnabled(true);
                badElfConnection.setBadElfDevice(badElfDevice);
            }
        }
    }


    /**
     * Called when the state of the Connection to the Bad Elf Device changes
     *
     * @param newState the state we just changed to.
     */
    @Override // this is a method of the BadElfGpsConnectionObserver
    public void onStateChanged(final BadElfService.State newState) {
        Log.d(TAG, String.format("OnStateChanged %s", newState));
        if (gpsPref.equals("Bad Elf GPS")) {
            updateGui(newState);
        }
    }

    /**
     * Called when ever we receive data from the Bad Elf Device
     *
     * @param data the received data
     */
    @Override // this is a method of the BadElfGpsConnectionObserver
    public void onDataReceived(final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receivedDataView.append(new String(data, Charset.forName("ISO-8859-1")));
                // Just keep the last 100 lines
                int linesToRemove = receivedDataView.getLineCount() - 30;


                if (linesToRemove > 0) {
                    tempData = receivedDataView.getText().toString();
//                    Log.d(TAG, "receivedDataView : \n" + tempData);
                    if (canGetGps(tempData)) {
                        if (SessionData.getInstance().isBadElfConnection()) {
                            ArrayList<String> latlongArray = nmeaToLatLongConverter(tempData);
                            if (setBadElfGpslatLong(latlongArray, false)) {
                                initCameraScanHandler();
//                                Log.d(TAG, "nmea receivedDataView with latlong\n : " + tempData);
                            }

                            SessionData.getInstance().setBadElfConnection(false);

                        }

                        for (int i = 0; i < linesToRemove; i++) {
                            Editable text = receivedDataView.getEditableText();
                            int lineStart = receivedDataView.getLayout().getLineStart(0);
                            int lineEnd = receivedDataView.getLayout().getLineEnd(0);
                            text.delete(lineStart, lineEnd);
                        }
                    }

                }
                final Layout layout = receivedDataView.getLayout();
                if (layout != null) {
                    int scrollDelta = layout.getLineBottom(receivedDataView.getLineCount() - 1)
                            - receivedDataView.getScrollY() - receivedDataView.getHeight();
                    if (scrollDelta > 0)
                        receivedDataView.scrollBy(0, scrollDelta);
                }

            }
        });
    }


    public List<BadElfDevice> badelfDeviceList() {
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(View.GONE);

        List<BadElfDevice> badElfDevices = new ArrayList<>();
        try {
            badElfDevices = BadElfDevice.getPairedBadElfDevices(this);
            final ArrayAdapter<BadElfDevice> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, badElfDevices);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(listener);

        } catch (RuntimeException e) {
            // Errors: Bluetooth not enabled or no paired Bad Elf Devices
//			final TextView errorMessage = (TextView) findViewById(R.id.ErrorMessage);
            listView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "badelfDeviceList error: " + e.getMessage());
//			errorMessage.setText(e.getMessage());
//			errorMessage.setVisibility(View.VISIBLE);

        }
        return badElfDevices;
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BadElfDevice badElfDevice = (BadElfDevice) parent.getItemAtPosition(position);
            Log.d(TAG, "Clicked " + badElfDevice.toString());
            Toast.makeText(CaptureActivity.this, "selected device \n" + badElfDevice.toString(), Toast.LENGTH_SHORT).show();
//			Intent deviceDataIntent = new Intent(CaptureActivity.this, BadElfDeviceDataActivity.class);
//			deviceDataIntent.putExtra(BadElfDevice.TAG, badElfDevice);
//			startActivity(deviceDataIntent);
        }
    };


    public ArrayList<String> nmeaToLatLongConverter(String nmea) {

        boolean second = false;
        boolean secondAcc = false;
        ArrayList<String> latlongArray = new ArrayList<>();

        String lines[] = nmea.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {

            if (lines[i] != null && lines[i].contains("$GPRMC")) {
                if (second) {
                    String[] data = lines[i].split(",");
                    if (data.length > 5) {
                        String latStr = data[3];
                        String longStr = data[5];

                        if (latStr != null && longStr != null && !latStr.equals("") && !longStr.equals("")) {

//                        if (latStr.length()>5&&longStr.length()>5) {
                            latlongArray.clear();

                            latlongArray.add(0, latStr.equals("") ? latStr : decimalToDMS(Double.parseDouble(latStr)));
                            latlongArray.add(1, longStr.equals("") ? longStr : decimalToDMS(Double.parseDouble(longStr)));
                            break;
//                        }
                        }
                    }
                }

                second = true;
            }


        }
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null && lines[i].contains("$GPGSA")) {
                if (secondAcc) {
                    String[] data = lines[i].split(",");
                    if (data.length > 17) {
                        String AccuracyStr = data[16];


                        if (AccuracyStr != null && !AccuracyStr.equals("")) {
                            float gpsAcc = Float.parseFloat(data[16]) * 4;
                            DecimalFormat numberFormat = new DecimalFormat("#0000");

                            int accuracyInt = Integer.parseInt(numberFormat.format(gpsAcc));
                            gpsAccuracy = "" + accuracyInt;

                            Log.d(TAG, "nmeaToLatLongConverter: gpsAccData" + data[16] + " ; gpsAccuracy" + gpsAccuracy);

                            break;
//                        }
                        }
                    }
                }

                secondAcc = true;
            }
        }

        if (latlongArray.size() == 2) {
            if (!latlongArray.get(0).equals("")) {
                txtLatlong.setVisibility(View.GONE);
                txtLatlong.setText("lat : " + latlongArray.get(0) + "\n long : " + latlongArray.get(1));
                Toast.makeText(this, "Connected to Bad ELF", Toast.LENGTH_SHORT).show();
                connected = true;
                Log.d(TAG, "nmeaToLatLongConverter: " + "lat :" + latlongArray.get(0) + "long :" + latlongArray.get(1) + "gpsAccuracy :" + gpsAccuracy);
            } else {
                txtLatlong.setVisibility(View.GONE);
            }
        }
        return latlongArray;
    }


    public void initBadelfgps() {

        locationId = mPreferences.getString("locationid", "");
        taskId = mPreferences.getString("taskid", "");

        gpsPref = mPreferences.getString("gps", "");


        connectDisconnectButton = (Button) findViewById(R.id.connect_disconnect_button);
        stateView = (TextView) findViewById(R.id.StateLabel);
        txtLatlong = (TextView) findViewById(R.id.txt_latlong);
        txtLatlong.setVisibility(View.GONE);
        txtLatlong.setText("");

        if (gpsPref.equals("Bad Elf GPS")) {


            connectDisconnectButton.setVisibility(View.VISIBLE);
            stateView.setVisibility(View.VISIBLE);
//            txtLatlong.setVisibility(View.VISIBLE);

            connectDisconnectButton.setOnClickListener(onConnectDisconnectButtonClicked);


            List<BadElfDevice> badElfDevices = new ArrayList<>();
            badElfDevices = badelfDeviceList();
            if (badElfDevices.size() > 0) {
                badElfDevice = badElfDevices.get(0);
            } else {
                showSConnectToBadElfAlert();
            }

            receivedDataView = (TextView) findViewById(R.id.received_data);
            receivedDataView.setVisibility(View.INVISIBLE);
            receivedDataView.setMovementMethod(new ScrollingMovementMethod());
            badElfConnection = new BadElfGpsConnection(this, this);


            txtLatlong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nmeaToLatLongConverter(receivedDataView.getText().toString());
                }
            });


        } else {
            connectDisconnectButton.setVisibility(View.GONE);
            stateView.setVisibility(View.GONE);
            txtLatlong.setVisibility(View.GONE);
        }
    }


    private String decimalToDMS(double value) {
        String result = "";
        String dd = "" + value;

        dd = dd.replaceAll("[.]", "");
        String[] resDigits = dd.split("");

        String degree = "" + resDigits[0] + resDigits[1] + resDigits[2];

        int DD = Integer.parseInt(degree);

        double ss = value - DD * 100;

        double calcResult = DD + ss / 60;

        DecimalFormat numberFormat = new DecimalFormat("#00.000000");
        result = numberFormat.format(calcResult);

        return result;
    }

    private void initCameraScanHandler() {
        if (handler == null) {
            handler = new CaptureActivityHandler(this);
        }
        Log.d(TAG, "initCameraScanHandler: Camera scan initialized");

    }

    public boolean getGps2() {
        boolean x = false;
        gps = new GPSTracker(CaptureActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


            x = true;


            GPScordinates = getChainedGpsStringBuilder().toString();

//            GPScordinates = "?*GPScoordinates=" + latitude + "," + longitude + "&*LocationID=" +locationId+"&*GPSaccuracy="+gpsAccuracy+"&*TaskID="+taskId;

            SessionData.getInstance().setGpsCoordinates(GPScordinates);

            System.out.println("Capture --- Capture Activity Coordinates lat " + latitude + " long " + longitude);


        } else {
            x = false;
            showSettingsAlert();
        }
        return x;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CaptureActivity.this);

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
                dialog.cancel();
//                getGps2();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                initCameraScanHandler();
            }
        });


        // Showing Alert Message
        alertDialog.show();
    }

    public void showRetryBadElfAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CaptureActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(true);

        // Setting Dialog Message
        alertDialog.setMessage("Can not get location from BadElfGps device!");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (setBadElfGpslatLong(nmeaToLatLongConverter(receivedDataView.getText().toString()), false)) {
                    initCameraScanHandler();

                }

            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Use phone Gps", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mPreferencesEditor.putString("gps", "Phone GPS");
                mPreferencesEditor.apply();

                Toast.makeText(CaptureActivity.this, "Phone GPS is selected", Toast.LENGTH_SHORT).show();

                if (getGps2()) {
                    initCameraScanHandler();
                }

            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void showRetryBadElfAlertForConnection() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CaptureActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(true);

        // Setting Dialog Message
        alertDialog.setMessage("Can not connect to BadElfGps device!");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                if (setBadElfGpslatLong(nmeaToLatLongConverter(receivedDataView.getText().toString()), false)) {
//                    initCameraScanHandler();

//                }

                receivedDataView.setText(""); // clear old received data
                badElfConnection.connect();

            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Use phone Gps", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mPreferencesEditor.putString("gps", "Phone GPS");
                mPreferencesEditor.apply();

                Toast.makeText(CaptureActivity.this, "Phone GPS is selected", Toast.LENGTH_SHORT).show();

                if (getGps2()) {
                    initCameraScanHandler();
                }

            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void showSConnectToBadElfAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CaptureActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage("BadElfgps Device is not paired.\nPlease pair to BadElf on bluetooth settings ");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public boolean setBadElfGpslatLong(ArrayList<String> latlongArray, boolean gpsrecieved) {

        if (latlongArray.size() == 2 && !latlongArray.get(0).equals("") && !latlongArray.get(1).equals("")) {
            latitude = Double.parseDouble(latlongArray.get(0));
            longitude = Double.parseDouble(latlongArray.get(1));

            gpsrecieved = true;

            GPScordinates = getChainedGpsStringBuilder().toString();

//            GPScordinates = "?*GPScoordinates=" + latitude + "," + longitude + "&*LocationID=" +locationId+"&*GPSaccuracy="+gpsAccuracy+"&*TaskID="+taskId;

            SessionData.getInstance().setGpsCoordinates(GPScordinates);

            System.out.println("Capture  Activity setBadElfGpslatLong Coordinates lat " + latitude + " long " + longitude);
            System.out.println("Capture  Activity setBadElfGpslatLong GPScordinates " + GPScordinates);

//            getGps2();
        } else {
            showRetryBadElfAlert();
        }

        return gpsrecieved;
    }


    public boolean canGetGps(String word) {
        boolean x = false;
        int i = 0;
        Pattern p = Pattern.compile("GPRMC");
        Matcher m = p.matcher(word);
        while (m.find()) {
            i++;
            if (i > 2) {
                x = true;
                break;
            }
        }
//        System.out.println(i); // Prints 3

        return x;
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

    private StringBuilder getChainedGpsStringBuilder1() {

        StringBuilder stringBuilderGps = new StringBuilder();

//        stringBuilderGps.append("?*GPScoordinates=" + latitude + "," + longitude);
        stringBuilderGps.append("?");

        if (gps != null) {
            stringBuilderGps.append("*GPScoordinates=" + latitude + "," + longitude);
        }
        if (!locationId.equals("")) {
            stringBuilderGps.append("&*LocationID=" + locationId);
        }
        if (!gpsAccuracy.equals("")) {
            stringBuilderGps.append("&*GPSaccuracy=" + gpsAccuracy);
        }
        if (!taskId.equals("")) {
            stringBuilderGps.append("&*TaskID=" + taskId);
        }
        return stringBuilderGps;
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        Intent SKScanner = new Intent(CaptureActivity.this, com.signakey.sktrack.SKScanner.class);
        startActivity(SKScanner);
    }
}
