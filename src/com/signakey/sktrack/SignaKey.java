package com.signakey.sktrack;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import com.cvisionlab.zxing.KeyInterface;
import android.graphics.Bitmap;
import android.util.Xml;

public class SignaKey implements KeyInterface {
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
    public static final String TYPE_SIGNAKEY = "SignaKey";
    public static final int MAX_THUMB_SIZE = 32;

    private String mType = null;
    private String mMeta = null;
    private String mTimestampAsString = null;
    private Bitmap mBitmap = null;
    private byte[] mSignaKey;
    private String mAssociatedPath = null;
    
    public static String Token = null;

    /**
     * Construct key. Timestamp will be generated automatically.
     * @param bitmap is an image.
     * @param key is a recognized key as array of bytes.
     * @param meta is a plain text for any meta information.
     * @return True if success, false otherwise.
     */
    public SignaKey(Bitmap bitmap, String type, String meta, byte[] key) {
        this(bitmap, type, meta, key, new Date());
    }

    /**
     * Construct key.
     * @param bitmap is an image.
     * @param key is a recognized key as array of bytes.
     * @param meta is a plain text for any meta information.
     * @param timestamp is a timestamp as Date object. 
     * @return True if success, false otherwise.
     */
    public SignaKey(Bitmap bitmap, String type, String meta, byte[] key, Date timestamp) {
        mBitmap = bitmap;
        mType = type;
        mMeta = meta;
        mSignaKey = key;
        mTimestampAsString = TIMESTAMP_FORMAT.format(timestamp);
    }

    /**
     * Get type of key.
     * Currently only "SignaKey" may be returned.
     * @return Type if key as String object.
     */
    public String getType() {
        return mType;
    }

    /**
     * Get timestamp as Date object.
     * @return null if some error is occurred.
     */
    public Date getTimestamp() {
        Date timestamp = null;
        try {
            timestamp = TIMESTAMP_FORMAT.parse(mTimestampAsString);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /** Get timestamp as String object. */
    public String getTimestampAsString() {
        return mTimestampAsString;
    }

    /** Get bitmap. */
    public Bitmap getBitmap() {
        return mBitmap;
    }

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
            serializer.text("SignaKey");
            serializer.endTag("", "type");

            serializer.startTag("", "timestamp");
            serializer.text(mTimestampAsString);
            serializer.endTag("", "timestamp");

            serializer.startTag("", "value");
            String keyString = Byte.toString(mSignaKey[0]);
            for (int i=1; i<mSignaKey.length; i++) {
                keyString += ","+mSignaKey[i];
            }
            serializer.text(keyString);
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
        }
        return result;
    }

    public String getAssociatedPath() {
        return mAssociatedPath;
    }
    
	public void setAssociatedPath(String path) {
		mAssociatedPath = new String(path);
	}

    public String getMeta() {
        return mMeta;
    }
    
    public static String keyBufferToMeta(byte[] keyBuffer) {
        // Prepare meta-data.
        String meta = new String();

        meta = "Decode Result for " ;
        return meta;
    }
}
