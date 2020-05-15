package com.cvisionlab.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.google.zxing.LuminanceSource;
import com.google.zxing.PlanarYUVLuminanceSource;


public class LuminanceSourceCreator {

	/**
	 * Create LuminanceSource from image file.
	 * @param path - source path.
	 * @return Created LuminanceSource.
	 */
	public static LuminanceSource createFromPath(String path) {
		return createFromPath(path, false);
	}
	
	/**
	 * Create LuminanceSource from image file.
	 * @param path - source path.
	 * @param expand - expand image by white space. 
	 * @return Created LuminanceSource.
	 */
	public static LuminanceSource createFromPath(String path, boolean expand) {
		Bitmap originalBitmap = BitmapFactory.decodeFile(path);
		Log.i("SKScanner", "LuminaceSourceCreator Line 33 image path : " + path);
		if (expand) {
			// Read bitmap and grow it by white space.
			final float GROW_FACTOR = 1.5f;
			int w = originalBitmap.getWidth();
			int h = originalBitmap.getHeight();
			Bitmap.Config config = originalBitmap.getConfig();
			if (config == null) {
				config = Bitmap.Config.ARGB_8888;
			}
			Bitmap growedBitmap = Bitmap.createBitmap((int)(w*GROW_FACTOR), (int)(h*GROW_FACTOR), config);
			Matrix matrix = new Matrix();
			matrix.setTranslate((GROW_FACTOR-1)*w/2, (GROW_FACTOR-1)*h/2);
			Canvas canvas = new Canvas(growedBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(originalBitmap, matrix, null);
			Log.i("SKScanner", "LuminaceSourceCreator Line 49 image expanded. " );
			// Convert bitmap to luminance source.
			return createFromBitmap(growedBitmap);
		}
		else {
			Log.i("SKScanner", "LuminaceSourceCreator Line 54 image not expanded. " );
			return createFromBitmap(originalBitmap);
		}
	}
	
	/**
	 * Create LuminanceSource from resource image.
	 * @param cont - source context.
	 * @param id - resource ID.
	 * @return Created LuminanceSource.
	 */
	public static LuminanceSource createFromResourceId(Context cont, int id) {
		return createFromBitmap( BitmapFactory.decodeResource(cont.getResources(), id) );
	}
	
	/**
	 * Create LuminanceSource from Bitmap image.
	 * @param btm - source image.
	 * @return Created LuminanceSource.
	 */
	public static LuminanceSource createFromBitmap(Bitmap btm) {
		int w = btm.getWidth();
		int h = btm.getHeight();
		int d = w * h;
		int pixels[] = new int[d];
		byte luminance[] = new byte[d];
		btm.getPixels(pixels, 0, w, 0, 0, w, h);
		for(int i = 0; i < d; i++)
			luminance[i] = (byte)((306 * ((pixels[i] >>16) & 0xFF) +
		            		       601 * ((pixels[i] >> 8) & 0xFF) +
		                           117 * (pixels[i] & 0xFF) +
		                          (0x200)) >> 10);
		return new PlanarYUVLuminanceSource(luminance, w, h, 0, 0, w, h, true);
	}
	
	/**
	 * Create LuminanceSource from byte array.
	 * @param bytes - source byte array.
	 * @param w - image width.
	 * @param h - image height.
	 * @return Created LuminanceSource.
	 */
	public static LuminanceSource createFromBytes(byte[] bytes, int w, int h) {
		return new PlanarYUVLuminanceSource(bytes, w, h, 0, 0, w, h, true);
	}

}
