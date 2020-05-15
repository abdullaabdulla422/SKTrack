package com.signakey.sktrack;

import com.cvisionlab.zxing.ZXingKey;
import com.google.zxing.Result;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class DecodeThread extends Thread{
	private final CaptureActivity activity;
	private Handler handler;
	public ZXingKey decoder;

	DecodeThread(CaptureActivity activity){
		this.activity = activity;
		Log.i("SKScanner", "DecThread Line 17 DecodeThread.");
		this.decoder= activity.decoder;   // We will need to hook up with the right activity.


	}

 
	@Override
	public void run() {
		super.run();
		Log.i("SKScanner", "DecThread Line 26 run .");
		Looper.prepare();
		Log.i("SKScanner", "DecThread Line 28 loop start");
		createHandler();
		Looper.loop(); 
	}


	Handler getHandler() {
		if(handler==null){
			createHandler();
			Log.i("SKScanner", "DecThread Line 35 run . Handler was null, so recreated with CreateHandler()");
		}


	    return handler;
	}
	
	/**
	   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
	   * reuse the same reader objects from one decode to the next.
	   *
	   * @param data   The YUV preview frame.
	   * @param width  The width of the preview frame.
	   * @param height The height of the preview frame.
	   */
	private void decode(byte[] data, int width, int height) {
		Log.i("SKScanner", "DecThread Line 59 decode. width = " + width + " height = " + height);
		DecodeBufferSource source = CameraManager.get().buildDecodeBuffer(data, width, height);
		
		/**
	     *  SKScannerSDK Decoder
	     *  Param : 
	     *  	byte[] imageBuffer    	: The data of the bitmap.
	     *  	int    imageWidth   	: The width of the bitmap.
	     *  	int    imageHeight  	: The height of the bitmap.
	     *  Output:
	     *  	String					: result.
	     */
		// Result rawResult = ScanCamera.DecodeBufferSource1 (source.getMatrix(), source.getWidth(), source.getHeight());
		ScanCamera SKdecoder = new ScanCamera();
		Result rawResult = SKdecoder.DecodeBufferSource (source.getMatrix(), source.getWidth(), source.getHeight());
		
		Log.i("SKScanner", "DecThread Line 75 Return from Scan_Camera after decode.");
		if (rawResult  != null) {
			Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
			Log.i("SKScanner", "DecThread Line 78 Return from Scan_Camera after decodeed success. message = " + R.id.decode_succeeded);
			message.sendToTarget();
		}else{
			Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
			Log.i("SKScanner", "DecThread Line 78 Return from Scan_Camera after decode failed. message = " + R.id.decode_failed);
		    message.sendToTarget();

		}
	}
	private void createHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				Log.i("SKScanner", "DecThread Line 29 handler start");

				switch (message.what) {
					case R.id.decode:
						Log.i("SKScanner", "DecThread Line 33 decode =" + message.what);
						decode((byte[]) message.obj, message.arg1, message.arg2);
						break;
					case R.id.quit:
						Log.i("SKScanner", "DecThread Line 37 Looper quit= "+ message.what);
						Looper.myLooper().quit();
						break;

				}
			}
		};
	}

}
