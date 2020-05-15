package com.signakey.sktrack;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.Result;
import com.signakey.sktrack.skclient.SessionData;

public class CaptureActivityHandler extends Handler {

//	private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final CaptureActivity activity;
    private final DecodeThread decodeThread;
    private State state;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureActivityHandler(CaptureActivity activity) {

        this.activity = activity;
        decodeThread = new DecodeThread(activity);
        decodeThread.start();
        state = State.SUCCESS;
        Log.i("SKScanner", "CapActHndlr: Line 30 CaptureActivityHandler. state = " + state);
        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }


    @Override
    public void handleMessage(Message msg) {
        Log.i("SKScanner", "CapActHndlr: Line 38 handleMessage. msg.what=" + msg.what);
        switch (msg.what) {
            case R.id.auto_focus:
                if (state == State.PREVIEW) {
                    Log.i("SKScanner", "CapActHndlr: Line 42 handleMessage requestAutoFocus. state = " + state);
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
                break;
            case R.id.restart_preview:  // Assumes a  successful decode and a new thread needed
                Log.i("SKScanner", "CapActHndlr: Line 47 handleMessage restartPreviewAndDecode. state = " + state);
                restartPreviewAndDecode();
                break;
            case R.id.decode_succeeded:
                state = State.SUCCESS;
                Log.i("SKScanner", "CapActHndlr: Line 52 handleMessage Decode Success. State = " + state);
                activity.handleDecode((Result) msg.obj);
                break;
            case R.id.decode_failed:
                state = State.PREVIEW;

                if (decodeThread.getHandler() != null) {
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                    if (SessionData.getInstance().getScanhandler() == 1) {
                        CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                    }
                    Log.i("SKScanner", "CapActHndlr: Line 59 handleMessage decode failed. state = " + state);
                }
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        Log.i("SKScanner", "CapActHndlr: Line 65 quitSynchronously, state=. " + state);
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            decodeThread.join();
        } catch (InterruptedException e) {
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        Log.i("SKScanner", "CapActHndlr: Line 80 restartPreviewAndDecode.");
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            activity.drawViewfinder();
            Log.i("SKScanner", "CapActHndlr: Line 86 restartPreviewAndDecode. state = " + state);
        } else {

//			Toast toast = Toast.makeText(getBaseContext(), "On_Touch " + p.getFlashMode(), Toast.LENGTH_LONG);
//					toast.show();
        }
    }

}
