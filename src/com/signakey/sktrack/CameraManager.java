package com.signakey.sktrack;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.signakey.sktrack.skclient.SessionData;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraManager extends Activity {


	static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
	static {
		int sdkInt;
		try {
			sdkInt = Integer.parseInt(Build.VERSION.SDK);
		} catch (NumberFormatException nfe) {
			// Just to be safe
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
		// Camera
	}


	// Camera
	public static CameraManager cameraManager;
	public static Camera camera;
	public static CameraConfigurationManager configManager;
	private static Rect framingRect;
	private static Rect framingRectInPreview; // for decode

	// Camera Handler
	private Handler previewHandler;
	private int previewMessage;
	private static Handler autoFocusHandler;
	private static int autoFocusMessage;
	private static final long AUTOFOCUS_INTERVAL_MS = 1000L;

	// Control flags
	private boolean initialized;
	private boolean previewing;
	private final boolean useOneShotPreviewCallback;

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 480;
	private static final int MAX_FRAME_HEIGHT = 360;

	int currentZoomLevel = 0, maxZoomLevel = 0;
//	Context context = CameraManager.this;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (!useOneShotPreviewCallback) {
				camera.setPreviewCallback(null);
				Log.i("SKCamera",
						"CamMgr: Line 65 uswOneShotPreviewCallback false.");
			}
			if (previewHandler != null) {
				Point cameraResolution = configManager.getCameraResolution();
				Message message = previewHandler.obtainMessage(previewMessage,
						cameraResolution.x, cameraResolution.y, data);
				message.sendToTarget();
				previewHandler = null;

				Log.i("SKCamera", "CamMgr: Line 73 PreviewCallback");
			} else {
				Log.d("SKCamera",
						"CamMgr: Line 75 Got preview callback, but no handler for it");
			}
		}
	};




	static void focusOnTouch(MotionEvent event) {
		if (camera != null ) {


			//SessionData.getInstance().setAutoFocus(0);

			Camera.Parameters parameters = camera.getParameters();


			if (parameters.getMaxNumMeteringAreas() > 0) {
//				Log.i(TAG,"fancy !");
				//Rect rect = calculateFocusArea(event.getX(), event.getY());
				Rect rect = getFramingRectInPreview();
				//	parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				//parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//				List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
//				meteringAreas.add(new Camera.Area(framingRect, 800));
//				parameters.setFocusAreas(meteringAreas);

				List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
				Camera.Size cs = sizes.get(0);
				parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				//	parameters.setPreviewSize(cs.width, cs.height);
				parameters.getSupportedPreviewSizes();
				camera.autoFocus(autoFocusCallback);
				camera.setParameters(parameters);
				//camera.cancelAutoFocus();
				//camera.autoFocus(autoFocusCallback);
			} else {
				parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

				camera.setParameters(parameters);
				camera.autoFocus(autoFocusCallback);
				camera.cancelAutoFocus();

				//CameraManager.get().requestAutoFocus(autoFocusHandler, R.id.auto_focus);
			}
			/*if (parameters.getMaxNumMeteringAreas() > 0) {

				Rect rect = getFramingRectInPreview();
				List<Camera.Area> focusList = new ArrayList<Camera.Area>();
				Camera.Area focusArea = new Camera.Area(rect, 1000);
				focusList.add(focusArea);

				Camera.Parameters param = camera.getParameters();
				param.setFocusAreas(focusList);
				param.setMeteringAreas(focusList);
				camera.setParameters(param);

				camera.autoFocus(autoFocusCallback);


//				Handler handler=new Handler();
//				final Runnable r=new Runnable() {
//					@Override
//					public void run() {
//
//					}
//				};
//				handler.postDelayed(r,1000);



//				Log.i(TAG,"fancy !");
				//Rect rect = calculateFocusArea(event.getX(), event.getY());
//				Rect rect = getFramingRectInPreview();
//				parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO
//				);
//				//parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
////				List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
////				meteringAreas.add(new Camera.Area(framingRect, 800));
////				parameters.setFocusAreas(meteringAreas);
//
//				List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
//				Camera.Size cs = sizes.get(0);
//				parameters.getSupportedPreviewSizes();
//				if(SessionData.getInstance().getAutoFocus()==1) {
//					camera.autoFocus(autoFocusCallback);
//				}
//				camera.setParameters(parameters);
//				camera.cancelAutoFocus();
//				camera.setParameters(parameters);


				//camera.autoFocus(autoFocusCallback);
			} else {
//				parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
//
//				camera.setParameters(parameters);
//				if(SessionData.getInstance().getAutoFocus()==1) {
//					camera.autoFocus(autoFocusCallback);
//				}
				//CameraManager.get().requestAutoFocus(autoFocusHandler, R.id.auto_focus);
			}*/

		}
	}

	/**
	 * Autofocus callbacks arrive here, and are dispatched to the Handler which
	 * requested them.
	 */
	private static final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {

			if (autoFocusHandler != null) {
				Log.d("onAutoFocus ", "onAutoFocus: "+success);
				//if(SessionData.getInstance().getAutoFocus()==1){
					Message message = autoFocusHandler.obtainMessage(
							autoFocusMessage, success);
					// Simulate continuous autofocus by sending a focus request
					// every
					// AUTOFOCUS_INTERVAL_MS milliseconds.
					autoFocusHandler.sendMessageDelayed(message,
							AUTOFOCUS_INTERVAL_MS);
					autoFocusHandler = null;
					camera.cancelAutoFocus();
					Log.i("SKCamera", "CamMgr: Line 89 Autofocus.");
					SessionData.getInstance().setAutoFocus(0);
				//}

			}
		}
	};

	/**
	 * Initializes this static object with the Context of the calling Activity.
	 *
	 * @param context
	 *            The Activity which wants to use the camera.
	 */
	public static void init(Context context) {
		if (cameraManager == null) {
			cameraManager = new CameraManager(context);
			Log.i("SKCamera", "CamMgr: Line 102 init Context.");
		}
	}

	/**
	 * Gets the CameraManager singleton instance.
	 *
	 * @return A reference to the CameraManager singleton.
	 */
	public static CameraManager get() {
		Log.i("SKCamera", "CamMgr: Line 112 CameraMgr singleton.");
		return cameraManager;
	}

	private CameraManager(Context context) {
		this.configManager = new CameraConfigurationManager(context);
		camera = null;
		initialized = false;
		previewing = false;
		Log.i("SKCamera", "CamMgr: Line 121.");

		// Camera.setOneShotPreviewCallback() has a race condition in Cupcake,
		// so we use the older
		// Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later,
		// we need to use
		// the more efficient one shot callback, as the older one can swamp the
		// system and cause it
		// to run out of memory. We can't use SDK_INT because it was introduced
		// in the Donut SDK.
		useOneShotPreviewCallback = SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
	}

	public void onpause(){

		if (camera != null) {
			camera.release();

			Log.i("SKCamera", "CamMgr: Line 171 closeDriver & release camera.");
		}



	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 *
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public void openDriver(SurfaceHolder holder) throws IOException {
		if (camera == null) {
			camera = Camera.open();
			if (camera == null) {
				throw new IOException();
			}//camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);


			if (!initialized) {
				initialized = true;
				configManager.initFromCameraParameters(camera);

			}
			Log.i("SKCamera",
					"CamMgr: Line 147 openDriver & init camera parameters.");
			configManager.setDesiredCameraParameters(camera);
		}

	}

	// Set Zoom
	public void setzoom(int zoom) {
		stopPreview();
		Parameters params = camera.getParameters();
		params.setZoom(currentZoomLevel);
		camera.setParameters(params);

		Message restart = Message
				.obtain(autoFocusHandler, R.id.restart_preview);
		autoFocusHandler.handleMessage(restart);
		// startPreview();
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
			Log.i("SKCamera", "CamMgr: Line 171 closeDriver & release camera.");
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public void startPreview() {
		if (camera != null && !previewing) {
			camera.startPreview();
			//camera.setDisplayOrientation(90);
			Log.i("SKCamera", "CamMgr: Line 181 startPreview.");
			previewing = true;
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public void stopPreview() {
		if (camera != null && previewing) {
			if (!useOneShotPreviewCallback) {
				camera.setPreviewCallback(null);
			}
			camera.stopPreview();
			// previewHandler = null;
			// autoFocusHandler = null;
			previewing = false;
			Log.i("SKCamera", "CamMgr: Line 198 stopPreview.");
		}
	}

	// @Override // Back button pressed
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// return super.onKeyDown(keyCode, event);
	// }

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 *
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public void requestPreviewFrame(Handler handler, int message) {
		if (camera != null && previewing) {
			previewHandler = handler;
			previewMessage = message;


			Log.i("SKCamera", "CamMgr: Line 218 previewHandler = " + handler
					+ " previewMessage= " + message);
			if (useOneShotPreviewCallback) {
				camera.setOneShotPreviewCallback(previewCallback);
			} else {
				camera.setPreviewCallback(previewCallback);
			}
			Log.i("SKCamera", "CamMgr: Line 224 requestPreviewFrame.");
		}

	}

	/**
	 * Asks the camera hardware to perform an autofocus.
	 *
	 * @param handler
	 *            The Handler to notify when the autofocus completes.
	 * @param message
	 *            The message to deliver.
	 */
	public void requestAutoFocus(Handler handler, int message) {
		if (camera != null && previewing) {

			if(SessionData.getInstance().getAutoFocus()==1) {
			autoFocusHandler = handler;
			autoFocusMessage = message;

			camera.autoFocus(autoFocusCallback);
			Log.i("SKCamera", "CamMgr: Line 239 requestAutoFocus.");
			SessionData.getInstance().setAutoFocus(0);
			}
		}
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 *
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public static Rect getFramingRect() {
		Point screenResolution = configManager.getScreenResolution();
		Log.i("SKCamera", "CamMgr: Line 252 getFramingRect.");
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}

			int width = screenResolution.x * 2 / 4;
			if (width < MIN_FRAME_WIDTH) {
				width = MIN_FRAME_WIDTH;
			} else if (width > MAX_FRAME_WIDTH) {
				width = MAX_FRAME_WIDTH;
			}
			int height = screenResolution.y * 2 / 4;
			if (height < MIN_FRAME_HEIGHT) {
				height = MIN_FRAME_HEIGHT;
			} else if (height > MAX_FRAME_HEIGHT) {
				height = MAX_FRAME_HEIGHT;
			}
			int leftOffset = (screenResolution.x - width) / 2 + 10;
			int topOffset = (screenResolution.y - height) / 2 ;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.i("SKCamera", "CamMgr: Line 273 Calculated framing rect: "
					+ framingRect);
		}
		return framingRect;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 */
	public static Rect getFramingRectInPreview() {
		if (framingRectInPreview == null) {
			Rect rect = new Rect(getFramingRect());
			Point cameraResolution = configManager.getCameraResolution();
			Log.i("SKCamera", "CamMgr: Line 286 cameraResolution: "
					+ cameraResolution);
			Point screenResolution = configManager.getScreenResolution();
			Log.i("SKCamera", "CamMgr: Line 288 screenResolution: "
					+ screenResolution);
			rect.left = rect.left * cameraResolution.x / screenResolution.x;
			Log.i("SKCamera", "CamMgr: Line 290 rect.left: " + rect.left);
			rect.right = rect.right * cameraResolution.x / screenResolution.x;
			Log.i("SKCamera", "CamMgr: Line 292 rect.right: " + rect.right);
			rect.top = rect.top * cameraResolution.y / screenResolution.y;
			Log.i("SKCamera", "CamMgr: Line 294 rext.top: " + rect.top);
			rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
			Log.i("SKCamera", "CamMgr: Line 296 rect.bottom: " + rect.bottom);
			framingRectInPreview = rect;
		}
		Log.i("SKCamera", "CamMgr: Line 299 getFramingRectInPreview."
				+ framingRectInPreview);
		return framingRectInPreview;
	}

	/**
	 * A factory method to build the appropriate DecodeBufferSource object based
	 * on the format of the preview buffers, as described by Camera.Parameters.
	 *
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A DecodeBufferSource instance.
	 */
	public DecodeBufferSource buildDecodeBuffer(byte[] data, int width,
												int height) {
		Rect rect = getFramingRectInPreview();
		Log.i("SKCamera", "CamMgr: Line 290 DecodeBufferSource.");
		int previewFormat = configManager.getPreviewFormat();


		String previewFormatString = configManager.getPreviewFormatString();
		switch (previewFormat) {
			// This is the standard Android format which all devices are REQUIRED to
			// support.
			// In theory, it's the only one we should ever care about.
			case PixelFormat.YCbCr_420_SP:
				// This format has never been seen in the wild, but is compatible as
				// we only care
				// about the Y channel, so allow it.
			case PixelFormat.YCbCr_422_I:

				return new DecodeBufferSource(data, width, height, rect.left,
						rect.top, rect.width(), rect.height());
			default:
				// The Samsung Moment incorrectly uses this variant instead of the
				// 'sp' version.
				// Fortunately, it too has all the Y data up front, so we can read
				// it.
				if ("yuv420p".equals(previewFormatString)) {

					Log.i("previewFormatString",""+previewFormatString);
					return new DecodeBufferSource(data, width, height, rect.left,
							rect.top, rect.width(), rect.height());
				}
		}
		throw new IllegalArgumentException("Unsupported picture format: "
				+ previewFormat + '/' + previewFormatString);
	}


}

