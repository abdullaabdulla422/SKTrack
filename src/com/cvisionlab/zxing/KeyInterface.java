package com.cvisionlab.zxing;

import java.util.Date;

import android.graphics.Bitmap;

public interface KeyInterface {
	
	/** Get type of key. */
	public String getType();
	
	/** Get timestamp as Date object. */
    public Date getTimestamp();
    
    /** Get timestamp as String object. */
    public String getTimestampAsString();
    
    /** Get bitmap. */
    public Bitmap getBitmap();
    
    /** Store key onto path. */
    public boolean storeTo(String pathToStorage);
    
    /** Get path to info. */
    public String getAssociatedPath();
    
    /** Set path to info. */
    public void setAssociatedPath(String path);
    
    /** Get meta data. */
    public String getMeta();

}
