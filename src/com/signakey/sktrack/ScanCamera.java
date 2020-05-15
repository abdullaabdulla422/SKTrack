package com.signakey.sktrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cvisionlab.zxing.ReaderInterface;
import com.google.zxing.Result;
import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.WebMarkAuthenticateResult;
import com.signakey.skscanner.SignaKeyRecognizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ScanCamera extends Activity {
	public static final String EXTRA_PATH_TO_INFO = "extra_path_to_info";

	// ViewFinderView

	private Button mTakePhotoButton;
	private SignaKeyRecognizer mRecognizer = null;
	private BeepManager mBeepManager = null;
	private SignaKeyClient mSkClient = null;
	static boolean mWhiteOnBlack = false;
	private ProgressDialog mProgressDialog;
	public static boolean mRecognizeSignaKey = false;
	public static boolean mRecognizeStandard = false;
	public static int mSignakeySymbol = 1;
	static int mBgMode = 1;
	private boolean decodesuccess = false;
	private int FileLength = 0;
	SeekBar mslider;
	Logger logger;
	
	
	

	@SuppressWarnings("unused")
	private PictureCallback mPictureCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {

			Log.i("SKScanner", "ScanCam: Photo is taken. Data's length is "
					+ data.length + " bytes");
			StorageManager storage = new StorageManager();
			if (storage.init(getApplicationContext()) == StorageManager.NO_ERRORS) {
				String pathToImage = storage.getBaseDir()
						+ SignaKey.TIMESTAMP_FORMAT.format(new Date()) + ".jpg";
				if (_saveJpeg(data, pathToImage) == false) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.cant_save_image),
							Toast.LENGTH_LONG).show();
					Log.i("SKScanner", getString(R.string.cant_save_image));
				} else {
					boolean isRecognized = false;

					if (mRecognizeSignaKey) {
						Log.i("SKScanner",
								"ScanCam: Line 82 Try to recognize code using SignaKey library.");

						byte[] keyBufferBgBlack = new byte[256]; 
						byte[] keyBufferBgWhite = new byte[256];
						int confidenceBgBlack = -1;
						int confidenceBgWhite = -1;
						switch (mBgMode) {
						case 3:
							confidenceBgBlack = mRecognizer.recognizeFromPath(
									pathToImage, keyBufferBgBlack, false, true,
									mSignakeySymbol);
							confidenceBgWhite = mRecognizer.recognizeFromPath(
									pathToImage, keyBufferBgWhite, false,
									false, mSignakeySymbol);
							break;
						case 2:
							confidenceBgBlack = mRecognizer.recognizeFromPath(
									pathToImage, keyBufferBgBlack, false, true,
									mSignakeySymbol);
							break;
						case 1:
						default:
							confidenceBgWhite = mRecognizer.recognizeFromPath(
									pathToImage, keyBufferBgWhite, false,
									false, mSignakeySymbol);
							break;
						}
						final int confidence = (confidenceBgBlack > confidenceBgWhite) ? confidenceBgBlack
								: confidenceBgWhite;
						final byte[] keyBuffer = (confidenceBgBlack > confidenceBgWhite) ? keyBufferBgBlack
								: keyBufferBgWhite;
						// Decode SignaKey from buffer
						if (confidence >= SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD) {
							Log.i("SKScanner",
									"ScanCam: Key is recognized on taken photo with confidence "
											+ confidence + ".");
							// _stopScan();
							mBeepManager.playBeepSoundAndVibrate();

							// Authenticate key, save key and image to history.
							_processRecognizedKey(keyBuffer, data,
									ImageFormat.JPEG);

							isRecognized = true;
						}
					}
					// Decode other bar codes from image
					if (!isRecognized && mRecognizeStandard) {
						
						Log.i("SKScanner",
								"ScanCam: Try to recognize code using ZXing library.");
						Result res = ReaderInterface.tryToRead(pathToImage);
						if (res != null) {
							Log.i("SKScanner",
									"ScanCam: Line 120 ZXing Key is recognized.");
							// _stopScan();
							mBeepManager.playBeepSoundAndVibrate();
				
						
							// Save key and image to history.
							_processRecognizedZXingKey(res, data,
									ImageFormat.JPEG);

							isRecognized = true;
							
						}
					}
					// Image is not decoded
					if (!isRecognized) {
						Toast.makeText(getApplicationContext(),
								"SacnCam: Key is not recognized.",
								Toast.LENGTH_LONG).show();
						Log.i("SKScanner", "ScanCam: Key is not recognized.");

					}
				}
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.cant_save_image), Toast.LENGTH_LONG)
						.show();
				Log.i("SKScanner", "ScanCam:"
						+ getString(R.string.cant_save_image));

			}
			mTakePhotoButton.setVisibility(View.VISIBLE);
		}
	};

	// Callback which will be called for each frame from camera.
	public Result DecodeBufferSource(byte[] nv21Buffer, int width, int height) {

		//
		Result res = null; // new Result(null, nv21Buffer, null, null);
		mRecognizer = new SignaKeyRecognizer();
		// boolean isRecognized = false;
		if (mRecognizeSignaKey) {
			Log.i("SKScanner",
					"ScanCam: Line 160 Try to recognize code using SignaKey library. mRecognizesSignaKey = "
							+ mRecognizeSignaKey);
			final byte[] keyBuffer = new byte[SignaKeyRecognizer.SIGNA_KEY_SIZE]; // allocate
																					// memory
																					// for
																					// key
			int confidence = mRecognizer.recognizeFromBuffer(nv21Buffer,
					height, width, keyBuffer, true, mWhiteOnBlack,
					mSignakeySymbol);
			Log.i("SKScanner",
					"ScanCam: Line 163 Key is recognized on frame with confidence " 
							+ confidence + ".");

			if(confidence!=-1){
				logger.addLog("confidence level " +  confidence );
			}

			// decodesuccess = confidence >=
			// SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD;
			if (confidence >= SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD) {
				logger.addLog("confidence level " +  confidence );
				Log.i("SKScanner",
						"ScanCam: Line 166 Key is recognized on frame with confidence "
								+ confidence
								+ " SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD"
								+ SignaKeyRecognizer.DEFAULT_CONFIDENCE_THRESHOLD);
				res = new Result("SignaKey Decode", keyBuffer, null, null);
				// isRecognized = _processRecognizedKey( keyBuffer);
			}
		}

		if (mRecognizeStandard) {
			CaptureActivity.DataMatrixResult = "";
			Log.i("SKScanner",
					"ScanCam: Line 174 Try to recognize code using ZXing library. mRecognizeSignaKey = "
							+ mRecognizeSignaKey);
			res = ReaderInterface.tryToRead(nv21Buffer, width, height);
			
			
			if (res != null) {
				Log.i("SKScanner", "ScanCam: Line 177 ZXing Key is recognized.");
				// _stopScan();
				// mBeepManager.playBeepSoundAndVibrate();
				// Save key and image to history.
				// _processRecognizedZXingKey(res, nv21Buffer,
				// ImageFormat.NV21);
				// isRecognized = true;
				Log.i("SKScanner", "ScanCam: Line 202 Decode Result res =."
						+ res.getText());
				// return res;
			}
		}

		// if (mBgMode == 3)
		// mWhiteOnBlack = !mWhiteOnBlack;
		// mTryCount++;
		Log.i("SKScanner", "ScanCam: Line 191 Decode Result ");
		return res;
	}

	@SuppressWarnings("unused")
	private boolean _saveNV21(byte[] nv21Buffer, String pathToImage, Size size) {
		boolean status = false;
		YuvImage yuvimage = new YuvImage(nv21Buffer, ImageFormat.NV21,
				size.width, size.height, null);
		try {
			FileOutputStream out = new FileOutputStream(pathToImage);
			yuvimage.compressToJpeg(new Rect(0, 0, size.width, size.height),
					80, out);
			out.close();
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	private boolean _saveJpeg(byte[] jpegBuffer, String pathToImage) {
		boolean status = false;
		try {
			FileOutputStream out = new FileOutputStream(pathToImage);
			out.write(jpegBuffer);
			out.close();
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Process recognized key (authenticate on web service and save to history).
	 * 
	 * @param keyBuffer
	 *            is a byte array which represents the key.
	 * @param imageBuffer
	 *            is a byte array which represents the image (several formats
	 *            are possible).
	 * @param imageBufferFormat
	 *            is indicate format of imageBuffer (ImageFormat.NV21 or
	 *            ImageFormat.JPEG).
	 */
	@SuppressLint("StaticFieldLeak")
	private boolean _processRecognizedKey(final byte[] keyBuffer,
										  final byte[] imageBuffer, final int imageBufferFormat) {
		// Make storage manager.
		// final StorageManager storage = new StorageManager();
		// int initresult = storage.init(getApplicationContext());
		Log.i("SKScanner", "ScanCam: _processRecognizedKey.");
		// if ((initresult == StorageManager.NO_ERRORS) || (initresult ==
		// StorageManager.NOT_MOUNTED)) {
		// Authenticate key on SignaKey service.
		new AsyncTask<Void, Void, WebMarkAuthenticateResult>() {
			protected void onPreExecute() {
				mProgressDialog = new ProgressDialog(ScanCamera.this);
				mProgressDialog.setCancelable(false);
				mProgressDialog.setMessage("Processing live camera image ...");
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.show();
			};

			@SuppressWarnings("unused")
			protected WebMarkAuthenticateResult doInBackground(Void... params) {
				// Wait end of login.
				try {
					Thread.sleep(1000);
					while (mSkClient.isPending()) {
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Auth key.
				WebMarkAuthenticateResult result = null;
				if (true) {
					int tryCount = 0;
					while ((result == null) && (tryCount < 2)) {
						Log.i("SKScanner",
								"ScanCam: Try to authenticate symbol (attempt "
										+ (tryCount + 1) + " of 2)");
						Log.i("SKScanner", "ScanCam: result =" + result);
						result = mSkClient.authenticateSymbol(keyBuffer);
						if (result == null) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						tryCount++;
					}
				} else {
					Log.i("SKScanner",
							"ScanCam:"
									+ getString(R.string.cant_auth_because_not_loggedin));
				}
				return result;
			};

			protected void onPostExecute(WebMarkAuthenticateResult result) {
				mProgressDialog.dismiss();

				// Make meta string from key.
				String meta = SignaKey.keyBufferToMeta(keyBuffer);

				if ((result == null) || (result.Common.WebResultCode != 0)) {
					Log.i("SKScanner",
							"ScanCam: Error during authentication of key.");
					Toast.makeText(ScanCamera.this,
							"Error during authentication of key.",
							Toast.LENGTH_LONG).show();
					meta = meta
							+ SignaKeyClient.Company
							+ "\nWeb Result code failed in ScanCamera. Result = "
							+ result;
					if (result != null) {
						Log.i("SKScanner", "ScanCam: Web result code = "
								+ result.Common.WebResultCode + ".");
						meta = meta + "\nWeb result code = "
								+ result.Common.WebResultCode + ".\n";
						decodesuccess = false;
					}
				} else {
					Log.i("SKScanner", "ScanCam: Authenticate status = "
							+ result.Common.ServiceResultMessage + " ("
							+ result.Common.ServiceResultSuccess + ").");
					meta = meta + SignaKeyClient.Company + "\n" + "STATUS: "
							+ result.Common.ServiceResultSuccess;
					if (!result.Common.ServiceResultSuccess) {
						meta = meta
								+ "\nStatus = "
								+ (result.Common.ServiceResultMessage + " ("
										+ result.Common.ServiceResultSuccess + ").");
					} else {

						decodesuccess = true;
					}
				}

				// Prepare bitmap and meta data.
				String InLogo = "Logo.bmp";
				FileInputStream fis = null;
				try {
					fis = openFileInput(InLogo);
					Log.i("SKScanner", "SKScanner: File Open Result fis= "
							+ fis);
				} catch (FileNotFoundException e) {
					Log.i("SKScanner", "SKScanner: FileNotFoundException e "
							+ e);
					e.printStackTrace();
				}
				Bitmap bitmap = BitmapFactory.decodeStream(fis);

				SignaKey k = new SignaKey(bitmap, SignaKey.TYPE_SIGNAKEY, meta,
						keyBuffer);
				StorageManager storage = new StorageManager();
				storage.init(getApplicationContext());
				if (k.storeTo(storage.getBaseDir()) == false) {
					Toast.makeText(getApplicationContext(),
							"SKScanner: Can't save key to history.",
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
				ScanCamera.this.setResult(Activity.RESULT_OK, intent);
				// }
				ScanCamera.this.finish();
			}
		};
		return decodesuccess;
	}

	/**
	 * Process recognized ZXing key (save to history).
	 * 
	 * @param recognizing
	 *            Result object.
	 * @param imageBuffer
	 *            is a byte array which represents the image (several formats
	 *            are possible).
	 * @param imageBufferFormat
	 *            is indicate format of imageBuffer (ImageFormat.NV21 or
	 *            ImageFormat.JPEG).
	 */
	private void _processRecognizedZXingKey(Result res,
			final byte[] imageBuffer, final int imageBufferFormat) {
		res.toString().getBytes();
		FileOutputStream mOutput;
		try {
			mOutput = openFileOutput("DecodeResultfile.txt", FileLength);
			mOutput.write(res.toString().getBytes());
			Log.i("SKScanner", "ScanCam: Line 755 res =  " + res.toString());
			mOutput.close();
		} catch (FileNotFoundException e) {
			Log.i("SKScanner", "ScanCam: Line 758 file not found =  " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("SKScanner", "ScanCam: Line 761 IOException =  " + e);
			e.printStackTrace();
		}

		// SharedPreferences preferences =
		// PreferenceManager.getDefaultSharedPreferences(this);
		// int mode = Integer.parseInt(preferences.getString("mode", "3"), 10);
		Log.i("SKScanner", "ScanCam : Line 379 Saved Bar Code Data");
		// if(mode == 2){
		// mode =1;
		// }
		return;
	}

}
