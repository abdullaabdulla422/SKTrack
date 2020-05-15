package com.cvisionlab.zxing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;
import com.google.zxing.Result;
import com.signakey.sktrack.CaptureActivity;

@SuppressLint("SimpleDateFormat") public class ZXingKey implements KeyInterface {

	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
	public static final int MAX_THUMB_SIZE = 64;

	private Bitmap mBitmap = null;
	private Result mResult = null;
	private String mType = null; 
    private String mMeta = null; 
    private Date   mTimestamp = null;
    private String mTimestampAsString = null;
    private String mText;
    private String mAssociatedPath = null;
	
    /**
     * Construct key.
     * @param btm - source image.
     * @param type - key type.
     * @param meta - meta data.
     * @param value - text.
     * @param timestamp - a Date object.
     */
    public ZXingKey(Bitmap btm, String type, String meta, String value, Date timestamp) {
    	mBitmap = btm;
		mType = type;
		mMeta = meta;
		mTimestamp = timestamp;
		mTimestampAsString = TIMESTAMP_FORMAT.format(timestamp);
		mText = value;
    }
    
    /**
     * Construct key.
     * Meta will be same as text.
     * Timestamp will be generated automatically.
     * @param res - result object, contains type and text.
     */
    public ZXingKey(Result res) {
    	this((Bitmap)null, res, null);
    	mMeta = res.getText();
    }
    
    /**
     * Construct key.
     * Meta will be same as text.
     * Timestamp will be generated automatically.
     * @param path - source image path.
     * @param res - result object, contains type and text.
     */
    public ZXingKey(String path, Result res) {
    	this((Bitmap)null, res, null);
    	mBitmap = BitmapFactory.decodeFile(path);
    	mMeta = res.getText();
    }
    
    /**
     * Construct key.
     * Timestamp will be generated automatically.
     * @param path - source image path.
     * @param res - result object, contains type and text.
     * @param meta - meta data.
     */
    public ZXingKey(String path, Result res, String meta) {
    	this((Bitmap)null, res, meta);
    	mBitmap = BitmapFactory.decodeFile(path);
    }
    
    /**
     * Construct key.
     * Meta will be same as text.
     * Timestamp will be generated automatically.
     * @param btm - source image.
     * @param res - result object, contains type and text.
     */
    public ZXingKey(Bitmap btm, Result res) {
    	this(btm, res, null);
    	mMeta = res.getText();
    }
    
    /**
     * Construct key.
     * Timestamp will be generated automatically.
     * @param btm - source image.
     * @param res - result object, contains type and text.
     * @param meta - meta data.
     */
    public ZXingKey(Bitmap btm, Result res, String meta) {
    	this(btm, res, meta, new Date());
    }
    
    /**
     * Construct key.
     * @param btm - source image.
     * @param res - result object, contains type and text.
     * @param meta - meta data.
     * @param timestamp - a Date object.
     */
    public ZXingKey(Bitmap btm, Result res, String meta, Date timestamp) {
		mBitmap = btm;
		mResult = res;
		mType = res.getBarcodeFormat().name();
		mMeta = meta;
		mTimestamp = timestamp;
		mTimestampAsString = TIMESTAMP_FORMAT.format(timestamp);
		mText = res.getText();
	}
	
    public ZXingKey(CaptureActivity captureActivity) {
		// TODO Auto-generated constructor stub
	}

	/** Get type of key. */
	public String getType() {
		return mType;
	}

	/** Get timestamp as Date object. */
	public Date getTimestamp() {
		return mTimestamp;
	}

	/** Get timestamp as String object. */
	public String getTimestampAsString() {

		return mTimestampAsString;
	}

	/** Get bitmap. */
	public Bitmap getBitmap() {
		return mBitmap;
	}

	/** Store key onto path. */
	public boolean storeTo(String pathToStorage) {
        // Initialize return value.
        boolean result = false;

        // Generate name and paths.
        String imagePath = pathToStorage + mTimestampAsString + ".jpg";
        String infoPath = pathToStorage + mTimestampAsString + ".info";

        // Save image.
        if (mBitmap != null) {
            FileOutputStream imageFileOutStream = null;
            try {
                imageFileOutStream = new FileOutputStream(imagePath);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, imageFileOutStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Save info.
        try {
            BufferedWriter writer = null;
            XmlSerializer serializer = null;
            writer = new BufferedWriter(new FileWriter(infoPath));
            serializer = Xml.newSerializer();
            serializer.setOutput(writer);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "info");

            serializer.startTag("", "type");
            serializer.text(mType);
            serializer.endTag("", "type");

            serializer.startTag("", "timestamp");
            serializer.text(mTimestampAsString);
            serializer.endTag("", "timestamp");

            serializer.startTag("", "value");
            serializer.text(mText);
            serializer.endTag("", "value");

            serializer.startTag("", "meta");
            serializer.text(mMeta);
            serializer.endTag("", "meta");

            serializer.endTag("", "info");
            serializer.endDocument();
            writer.close();

            mAssociatedPath = infoPath;

            // Set result as success.
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
	    } catch (Exception e) {
	        e.printStackTrace();
	        result = false;
	    }
        return result;
    }

	/** Get path to info. */
	public String getAssociatedPath() {
		return mAssociatedPath;
	}
	
	/** Set path to info. */
	public void setAssociatedPath(String path) {
		mAssociatedPath = path;
	}

	/** Get meta data. */
	public String getMeta() {
		return mMeta;
	}
	
	/** Get text. */
	public String getText() {
		return mText;
	}
	
	/** Get result object. */
	public Result getResult() {
		return mResult;
	}
	
}
