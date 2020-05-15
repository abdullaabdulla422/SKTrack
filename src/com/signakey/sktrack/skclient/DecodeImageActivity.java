package com.signakey.sktrack.skclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bad_elf.badelfgps.BadElfDevice;
import com.signakey.sktrack.BeepManager;
import com.signakey.sktrack.ConnectionDetector;
import com.signakey.sktrack.KeyViewer;
import com.signakey.sktrack.R;
import com.signakey.sktrack.SKScanner;
import com.signakey.sktrack.SignaKeyClient;
import com.signakey.skscanner.SignaKeyRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DecodeImageActivity extends Activity implements View.OnClickListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String IMAGE_DIRECTORY_NAME = "Camera";
    private static final int REQUEST_PICK_IMAGE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;
    private static final int PICK_FROM_FILE = 2;
    BeepManager mBeepManager = null;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    Boolean isBadelfPresent = false;
    File mediaFile;
    private SignaKeyClient mSkClient = null;
    private Context context;
    private TextView infoTextID;
    private TextView decode;
    private TextView media;
    private ImageView image_toDecode;
    private ImageView camera;
    private ImageView btn_back;
    private Uri selectedImageUri;
    private Boolean gallery_path_FLAG = false;
    private String image_path = "";
    private Bitmap bitmap;
    private ExifInterface exif;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanimage);
        context = DecodeImageActivity.this;
        cd = new ConnectionDetector(getApplicationContext());
        init();
        setOnClick();


    }


    private void init() {

        mBeepManager = new BeepManager((Activity) context);
        mBeepManager.updatePrefs();
        mSkClient = new SignaKeyClient(context);



        infoTextID = (TextView) findViewById(R.id.infoTextID);
        media = (TextView) findViewById(R.id.media);
        decode = (TextView) findViewById(R.id.decode);
        image_toDecode = (ImageView) findViewById(R.id.image_toDecode);
        camera = (ImageView) findViewById(R.id.camera);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        infoTextID.setVisibility(View.VISIBLE);
        image_toDecode.setVisibility(View.GONE);
        decode.setVisibility(View.GONE);
        isInternetPresent = cd.isConnectingToInternet();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setOnClick() {

        media.setOnClickListener(this);
        decode.setOnClickListener(this);
        camera.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v == media)
            media();

        if (v == decode)
            decode();

        if (v == camera)
            camera();

        if(v == btn_back)
            back();

    }

    private void back() {

        Intent backintent = new Intent(DecodeImageActivity.this, SKScanner.class);
        startActivity(backintent);
    }

    private void camera() {
        gallery_path_FLAG = false;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent inten = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        selectedImageUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
        inten.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(inten, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);


    }

    private File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void orientation(View view) {
        try {

            ImageView imageView = (ImageView) view;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // already 8 was here

            if (gallery_path_FLAG)
            {
                bitmap = BitmapFactory.decodeFile(getFilePathForN(selectedImageUri, context), options);
                exif = new ExifInterface(getFilePathForN(selectedImageUri, context));
            }
            else
            {
                bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                exif = new ExifInterface(selectedImageUri.getPath());
            }


            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(0.0));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(0.0));

            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            exif.saveAttributes();

            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bitmap.getWidth(), (float) bitmap.getHeight() / 2);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            imageView.setImageBitmap(bitmap);


        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Toast.makeText(this, ioe.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "General ex : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void decode() {

        if (selectedImageUri != null) {

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            int mode = Integer.parseInt(preferences.getString("mode", "3"), 10);

            String pathTmp = "";
            if (gallery_path_FLAG)
            {
                pathTmp = getFilePathForN(selectedImageUri, context); // MEDIA GALLERY
            }
            else
            {
                pathTmp = getPath(selectedImageUri); // MEDIA GALLERY
            }

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
                        confidenceBgWhite = recognizer.recognizeFromPath(path, keyBufferBgWhite,
                                false, false, signakey_symbol);
                        break;
                }

                final int confidence = (confidenceBgBlack > confidenceBgWhite) ? confidenceBgBlack
                        : confidenceBgWhite;
                Log.d("SKScanner", "SKScanner Line 340 confidence = "
                        + confidence);

                if (confidence > SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD) {
                    mBeepManager.playBeepSoundAndVibrate();
                    // Show message.
                    Log.i("SKScanner",
                            "SKScanner Line 346 Key is recognized with confidence "
                                    + confidence + ".");

                    new Decode(keyBufferBgWhite).execute();
                } else {
//                    selectedImageUri = null;
                    Toast.makeText(context, "No Data available", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private void media() {
        gallery_path_FLAG = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("OnResult", "OnResult");

        if (requestCode == REQUEST_PICK_IMAGE) {
        if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData();

                if (selectedImageUri != null)
                orientation(image_toDecode);
                infoTextID.setVisibility(View.GONE);
                image_toDecode.setVisibility(View.VISIBLE);
                decode.setVisibility(View.VISIBLE);

            }
        }

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
        if (resultCode == RESULT_OK)
        {
//                image_toDecode.setImageURI(selectedImageUri);
            if (selectedImageUri != null)
                orientation(image_toDecode);
                image_toDecode.setVisibility(View.VISIBLE);
                infoTextID.setVisibility(View.GONE);
                decode.setVisibility(View.VISIBLE);
        }
        } else if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        String result = null;
        if (cursor != null) { // cursor can be null, if OI file manager is used
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private String getFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }



    public class Decode extends AsyncTask<Void, Void, WebMarkDecodeSymbolsResult> {

        private ProgressDialog mProgressDialog;
        private byte[] keyBuffer;
        private WebMarkDecodeSymbolsResult decoderesult = null;
        private String decode;
        private Boolean decodesuccess = false;
        private int version;
        private int client;
        private int item;
        private int sequence;

        public Decode(byte[] keyBuffer) {
            this.keyBuffer = keyBuffer;
        }


        @Override
        protected void onPreExecute() {

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage("Processing live camera image ...");
          //  mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            Log.i("SKScanner",
                    "CapAct: Line 358 _processRecognizedKey Async Task Setup.");
        }

        @Override
        protected WebMarkDecodeSymbolsResult doInBackground(Void... voids) {
            // TODO Auto-generated method stub
            Log.i("SKScanner",
                    "CapAct: Line 362 _processRecognizedKey. Background task");
            WebMarkDecodeSymbolsResult result = null;
            if (true) {
                int tryCount = 0;
                while ((result == null) && (tryCount < 2)) {
                    Log.i("SKScanner",
                            "CapAct: Try to authenticate symbol (attempt "
                                    + (tryCount + 1) + " of 2)");
                    result = mSkClient.decodesymbol(keyBuffer);
                    Log.i("SKScanner",
                            "DecodeSymbol: Line 374 _processRecognizedKey. result = "
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
                decoderesult = result;

            }
            return result;

        }

        @Override
        protected void onPostExecute(WebMarkDecodeSymbolsResult result) {

            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if ((result == null) || (result.Common.WebResultCode != 0)) {
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    Toast.makeText(context,
                            "No Internet Connection", Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(getApplicationContext(),
                            SKScanner.class);
                    startActivity(intent);
                    // showAlertDialog(SKScanner.this,
                    // "No Internet Connection",
                    // "You don't have internet connection.", false);
                }
                Log.i("SKScanner",
                        "CapAct: Error during authentication of key.");
                /*
                 * Toast.makeText(CaptureActivity.this,
                 * "Error during authentication of key.",
                 * Toast.LENGTH_LONG).show();
                 */
                // CaptureActivity.this.finish();
                decode = decode
                        + SignaKeyClient.Company
                        + "\nWeb Result code failed in ScanCamera. Result = "
                        + result;
                if (result != null) {
                    Log.i("SKScanner", "CapAct: Web result code = "
                            + result.Common.WebResultCode + ".");
                    decode = decode + "\nWeb result code = "
                            + result.Common.WebResultCode + ".\n";
                    decodesuccess = false;

                }

            } else {
                Log.i("SKScanner", "CapAct: Authenticate status = "
                        + result.Common.ServiceResultMessage + " ("
                        + result.Common.ServiceResultSuccess + ").");
                decode = SignaKeyClient.Company + "\n" + "DECODE STATUS: "
                        + result.Common.ServiceResultSuccess;

                if (!result.Common.ServiceResultSuccess) {
                    decode = decode
                            + "\nStatus = "
                            + (result.Common.ServiceResultMessage + " ("
                            + result.Common.ServiceResultSuccess + ").");

                    Intent gentt = new Intent(getApplicationContext(),
                            KeyViewer.class);
                    // String keyIdentifer = null;
                    gentt.putExtra("DecodeNEED", decode);
                    startActivity(gentt);
                    setResult(Activity.RESULT_OK,
                            gentt);
                    finish();
                    // CaptureActivity.this.setResult(Activity.RESULT_OK,
                    // gentt);
                    // startActivity(gentt);
                    if (result.Common.WebResultCode == -800) {
                        // RESULT_CANCELED= result.Common.WebResultCode; No
                        // Access
                        setResult(
                                Activity.RESULT_CANCELED, gentt);
                    }
                    //

                } else {

                    if (result.OutText != "") {
                        decode = decode + "\nOut text = " + result.OutText
                                + "\n";
                        Log.d("testing outtext", "" + decode);
                    }
                    decode = result.OutText;
                    SessionData.getInstance().setSessionId(result.OutText);
                    if (decode.length() > 0) {
                        version = Integer.parseInt(decode.substring(0, 2),
                                16);
                        client = Integer.parseInt(decode.substring(2, 8),
                                16);
                        item = Integer
                                .parseInt(decode.substring(8, 16), 16);
                        sequence = Integer.parseInt(
                                decode.substring(16, 28), 16);
                    }

                    SessionData.getInstance().setVersion(version);

                    SessionData.getInstance().setClient(client);
                    SessionData.getInstance().setItem(item);
                    SessionData.getInstance().setSequence(sequence);

                    new AsyncGendb().execute();

                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }

                    decodesuccess = true;
                }

            }


        }


    }

    class AsyncGendb extends AsyncTask<Void, Void, WebGenDbResult> {

        private ProgressDialog mProgressDialog;
        private WebGenDbResult webgenresult;
        private String sucess;
        private boolean gendb;

        protected void onPreExecute() {

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage("Processing webGenDb response ...");
         //   mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            Log.i("SKScanner",
                    "CapAct: Line 358 _processRecognizedKey Async Task Setup.");
        }

        protected WebGenDbResult doInBackground(Void... params) {
            // TODO Auto-generated method stub


            Log.i("SKScanner", "GenDB: Line 367 _processRecognizedKey. ");
            WebGenDbResult genresult = null;
            if (true) {
                int tryCount = 0;
                while ((genresult == null && (tryCount < 2))) {
                    Log.i("SKScanner", "GenDB: Try  (attempt " + (tryCount + 1)
                            + " of 2)");
                    Log.i("SKScanner", "GenDB: result =" + genresult);
                    genresult = mSkClient.genDb();
                    if (genresult == null) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    tryCount++;
                }
                webgenresult = genresult;

            }
            return genresult;
        }

        ;

        protected void onPostExecute(WebGenDbResult result) {

            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if ((result == null) || (result.Common.WebResultCode != 0)) {

                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    Toast.makeText(context,
                            "No Internet Connection", Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(getApplicationContext(),
                            SKScanner.class);
                    startActivity(intent);
                    // showAlertDialog(SKScanner.this,
                    // "No Internet Connection",
                    // "You don't have internet connection.", false);
                }
                Log.i("SKScanner",
                        "CapAct: Error during authentication of key.");
//				Toast.makeText(CaptureActivity.this,
//						"Error during authentication of key.",
//						Toast.LENGTH_LONG).show();
                sucess = sucess + SignaKeyClient.Company
                        + "\nWeb Result code failed in ScanCamera. Result = "
                        + result;

                if (result != null) {
                    Log.i("SKScanner", "CapAct: Web result code = "
                            + result.Common.WebResultCode + ".");
                    sucess = sucess + "\nWeb result code = "
                            + result.Common.WebResultCode + ".\n";

                    gendb = false;

                    Log.d("Success_100", sucess + "");

                    Intent gent = new Intent(getApplicationContext(), KeyViewer.class);
                    // String keyIdentifer = null;
                    gent.putExtra("STRING_I_NEED", sucess);

                    setResult(Activity.RESULT_OK, gent);
                    if (result.Common.WebResultCode == -800) {
                        // RESULT_CANCELED= result.Common.WebResultCode; No Access
                        setResult(Activity.RESULT_CANCELED, gent);
                    }

                }
            } else {
                Log.i("SKScanner", "CapAct: Authenticate status = "
                        + result.Common.ServiceResultMessage + " ("
                        + result.Common.ServiceResultSuccess + ").");
                sucess = SignaKeyClient.Company + "\n" + "GENDB RESULT: "
                        + result.Common.ServiceResultSuccess;

                if (!result.Common.ServiceResultSuccess) {
                    sucess = sucess
                            + "\nStatus = "
                            + (result.Common.ServiceResultMessage + " ("
                            + result.Common.ServiceResultSuccess + ").");

                } else {


                    if (result.Response.Data.Widgets.WidgetData.Details.get(0).Keyword != ""
                            && result.Response.Data.Widgets.WidgetData.Details
                            .get(0).Value.String != ""
                            && result.Response.Data.Widgets.WidgetData.Details
                            .get(result.Response.ResultCode).Keyword != "") {
                        sucess = "" + "\n";
                        sucess = sucess + result.Response.Data.Widgets.WidgetData.Details.get(0).Keyword + ":"; // Serial Number:
                        sucess = sucess + result.Response.Data.Widgets.WidgetData.Details.get(0).Value.String + "\n"; // 0618396
                        sucess = sucess + result.Response.Data.Widgets.WidgetData.Details.get(result.Response.ResultCode).Keyword;
                        Log.d("testing_outtext", "" + sucess);

                    }
                }

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                gendb = true;


                Intent gent = new Intent(getApplicationContext(),
                        KeyViewer.class);
                // String keyIdentifer = null;
                gent.putExtra("STRING_I_NEED", sucess);
                startActivity(gent);
                setResult(Activity.RESULT_OK, gent);

            }


//
//
//
//
            finish();

        }

    }


}
