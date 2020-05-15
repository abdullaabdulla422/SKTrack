package com.signakey.sktrack;

import android.util.Log;

/**
 * This object around an array of YUV data returned from the camera driver,
 * with the option to crop to a rectangle within the full data. This can be used to exclude
 * superfluous pixels around the perimeter and speed up decoding.
 *
 * It works for any pixel format where the Y channel is planar and appears first, including
 * YCbCr_420_SP and YCbCr_422_SP.
 */
public class DecodeBufferSource {
	private final byte[] yuvData;
	private final int dataWidth;
	private final int dataHeight;
	private final int left;
	private final int top;
	private final int width;
	private final int height;
	
	public DecodeBufferSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top,
		      int width, int height) {
		Log.i("SKScanner", "DecodeBufferSource line 24.");
		    if (left + width > dataWidth || top + height > dataHeight) {
		      throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
		    }

		    this.yuvData = yuvData;
		    this.dataWidth = dataWidth;
		    this.dataHeight = dataHeight;
		    this.left = left;
		    this.top = top;
		    this.width = dataWidth;
		    this.height = dataHeight;
	}
	
	/**
	   * Fetches luminance data for the underlying bitmap. Values should be fetched using:
	   * int luminance = array[y * width + x] & 0xff;
	   *
	   * @return A row-major 2D array of luminance values. Do not use result.length as it may be
	   *         larger than width * height bytes on some platforms. Do not modify the contents
	   *         of the result.
	   */
	public byte[] getMatrix() {
		Log.i("SKScanner", "DecBufSrc Line 47 getMatrix.");
	    // If the caller asks for the entire underlying image, save the copy and give them the
	    // original data. The docs specifically warn that result.length must be ignored.
	    if (width == dataWidth && height == dataHeight) {
	      return yuvData;
	    }

	    int area = width * height;
	    byte[] matrix = new byte[area];
	    int inputOffset = top * dataWidth + left;

	    // If the width matches the full width of the underlying data, perform a single copy.
	    if (width == dataWidth) {
	      System.arraycopy(yuvData, inputOffset, matrix, 0, area);
	      return matrix;
	    }

	    // Otherwise copy one cropped row at a time.
	    byte[] yuv = yuvData;
	    for (int y = 0; y < height; y++) {
	      int outputOffset = y * width;
	      System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
	      inputOffset += dataWidth;
	    }
	    return matrix;
	  }
	
	  /**
	   * @return The width of the bitmap.
	   */
	  public final int getWidth() {
	    return width;
	  }

	  /**
	   * @return The height of the bitmap.
	   */
	  public final int getHeight() {
	    return height;
	  }
}

