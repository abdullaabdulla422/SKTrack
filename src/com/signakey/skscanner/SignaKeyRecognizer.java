package com.signakey.skscanner;

import android.util.Log;

/** Wrapper around signakey library. */
public class SignaKeyRecognizer {
	public static int DEFAULT_CONFIDENCE_THRESHOLD = 120;
	public final static int SIGNA_KEY_SIZE = 256;

	/** Recognize SignaKey on raw gray image's buffer. */
	public native int recognizeFromBuffer(byte[] image, int h, int w,
			byte[] key, boolean crop, boolean bgblack, int symbol_mode);

	/** Recognize SignaKey on image specified by path. */
	public native int recognizeFromPath(String path, byte[] key, boolean crop,
			boolean bgblack, int symbol_mode);

	// Use shared library.
	static {
		System.loadLibrary("signakey");
		Log.i("SKScanner", "SignaKeyRecognizer line 19 load library.");
	}

}
