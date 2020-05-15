package com.cvisionlab.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class ReaderInterface {

	/**
	 * Try to read code from image file.
	 * @param path - source path.
	 * @return reading result or null if no code found.
	 */
	public static Result tryToRead(String path) {
		return tryToRead(path, false);
	}

	/**
	 * Try to read code from image file.
	 * @param path - source path.
	 * @param expand - expand image by white space. 
	 * @return reading result or null if no code found.
	 */
	public static Result tryToRead(String path, boolean expand) {
		Result result;
        try {
        	result = readFromPath(path, expand);
        } catch (ReaderException re) {
        	result = null;
        }
        return result;
	}
	
	/**
	 * Try to read code from resource image.
	 * @param cont - source context.
	 * @param id - resource ID.
	 * @return reading result or null if no code found.
	 */
	public static Result tryToRead(Context cont, int id) {
		Result result;
        try {
        	result = readFromResourceId(cont, id);
        } catch (ReaderException re) {
        	result = null;
        }
        return result;
	}
	
	/**
	 * Try to read code from Bitmap image.
	 * @param btm - source image.
	 * @return reading result or null if no code found.
	 */
	public static Result tryToRead(Bitmap btm) {
		Result result;
        try {
        	result = readFromBitmap(btm);
        } catch (ReaderException re) {
        	result = null;
        }
        return result;
	}
	
	/**
	 * Try to read code from byte array.
	 * @param bytes - source byte array.
	 * @param w - image width.
	 * @param h - image height.
	 * @return reading result or null if no code found.
	 */
	public static Result tryToRead(byte[] bytes, int w, int h) {
		Result result;
        try {
        	result = readFromBytes(bytes, w, h);
        } catch (ReaderException re) {
        	result = null;
        }
        Log.i("SKScanner", "ReaderInterface: Line 86 result." + result);
        return result;
	}
	
	/**
	 * Try to read code from LuminanceSource.
	 * @param source to create from.
	 * @return reading result or null if no code found.
	 */
	public static Result tryToRead(LuminanceSource source) {
		Result result;
        try {
        	result = readFromLuminanceSource(source);
        } catch (ReaderException re) {
        	result = null;
        }
        return result;
	}

	/**
	 * Read code from image file.
	 * @param path - source path.
	 * @return reading result.
	 * @throws ReaderException if no code found.
	 */
	public static Result readFromPath(String path) throws ReaderException {
        return readFromPath(path, false);
	}

	/**
	 * Read code from image file.
	 * @param path - source path.
	 * @param expand - expand image by white space. 
	 * @return reading result.
	 * @throws ReaderException if no code found.
	 */
	public static Result readFromPath(String path, boolean expand) throws ReaderException {
		LuminanceSource source = LuminanceSourceCreator.createFromPath(path, expand);
        return readFromLuminanceSource(source);
	}

	/**
	 * Read code from resource image.
	 * @param cont - source context.
	 * @param id - resource ID.
	 * @return reading result.
	 * @throws ReaderException if no code found.
	 */
	public static Result readFromResourceId(Context cont, int id) throws ReaderException {
		LuminanceSource source = LuminanceSourceCreator.createFromResourceId(cont, id);
        
        return readFromLuminanceSource(source);
	}

	/**
	 * Read code from Bitmap image.
	 * @param btm - source image.
	 * @return reading result.
	 * @throws ReaderException if no code found.
	 */
	public static Result readFromBitmap(Bitmap btm) throws ReaderException {
		LuminanceSource source = LuminanceSourceCreator.createFromBitmap(btm);
        
        return readFromLuminanceSource(source);
	}
	
	/**
	 * Read code from byte array.
	 * @param bytes - source byte array.
	 * @param w - image width.
	 * @param h - image height.
	 * @return reading result.
	 * @throws ReaderException if no code found.
	 */
	public static Result readFromBytes(byte[] bytes, int w, int h) throws ReaderException {
		LuminanceSource source = LuminanceSourceCreator.createFromBytes(bytes, w, h);

		return readFromLuminanceSource(source);
	}
	
	/**
	 * Read code from LuminanceSource.
	 * @param source to create from.
	 * @return reading result.
	 * @throws ReaderException if no code found.
	 */
	public static Result readFromLuminanceSource(LuminanceSource source) throws ReaderException {
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = null;
        try {
          result = new MultiFormatReader().decode(bitmap);
        } catch (ReaderException re) {
        	throw re;
        }
        return result;
	}

}
