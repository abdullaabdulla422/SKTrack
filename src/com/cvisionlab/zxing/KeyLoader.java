package com.cvisionlab.zxing;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.signakey.sktrack.SignaKey;

public class KeyLoader {
	
	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
	public static final int MAX_THUMB_SIZE = 0;

	/**
	 * Load recognized Key with full size image.
	 * @param pathToInfo - source path
	 * @return key recognized
	 */
    public static KeyInterface load(String pathToInfo) {
        return load(pathToInfo, false);
    }

    /**
     * Load recognized Key with thumb instead if image.
     * @param pathToInfo - source path
     * @param thumb - flag
     * @return key recognized
     */
    public static KeyInterface load(String pathToInfo, boolean thumb) {
    	KeyInterface key = null;
        try {
            // Prepare doc to parse.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(pathToInfo));

            // Read type.
            String type = doc.getElementsByTagName("type").item(0).getFirstChild().getNodeValue();

            // Read timestamp and metadata.
            Date timestamp = TIMESTAMP_FORMAT.parse(doc.getElementsByTagName("timestamp").item(0).getFirstChild().getNodeValue());
            String meta = doc.getElementsByTagName("meta").item(0).getFirstChild().getNodeValue();

            // Read value.
            String valueString = doc.getElementsByTagName("value").item(0).getFirstChild().getNodeValue();

            // Read bitmap.
            String pathToBitmap = pathToInfo.replace(".info", ".jpg");
            Bitmap bitmap = null;
            if (thumb) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                BitmapFactory.Options tempOpt = new BitmapFactory.Options();
                tempOpt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pathToBitmap, tempOpt);

                if (tempOpt.outHeight > MAX_THUMB_SIZE || tempOpt.outWidth > MAX_THUMB_SIZE)
                    opt.inSampleSize = (int)Math.pow(2, Math.ceil(Math.log(Math.max(tempOpt.outHeight, tempOpt.outWidth) / (double) MAX_THUMB_SIZE) / Math.log(2)));
                else
                    opt.inSampleSize = 1;

                // Decode image using options.
                bitmap = BitmapFactory.decodeFile(pathToBitmap, opt);
            }
            else {
                bitmap = BitmapFactory.decodeFile(pathToBitmap);
            }

            // Make key object which will be returned.
            if (type==SignaKey.TYPE_SIGNAKEY) {
            	String[] valueSplittedStrings = valueString.split(",");
                byte[] value = new byte[valueSplittedStrings.length];
                for (int i=0; i<valueSplittedStrings.length; i++) {
                    value[i] = Byte.parseByte(valueSplittedStrings[i]);
                }
                key = new SignaKey(bitmap, type, meta, value, timestamp);
            }
            else
            	key = new ZXingKey(bitmap, type, meta, valueString, timestamp);
            
            key.setAssociatedPath( pathToInfo );
        } catch (Exception e) {
            e.printStackTrace();
            key = null;
        }
        return key;
    }

}
