package com.signakey.sktrack;

import java.io.File;
import android.content.Context;
import android.os.Environment;

public class StorageManager {
    public static final int NO_ERRORS = 0;
    public static final int CANNOT_CREATE_DIR = 1;
    public static final int NOT_MOUNTED = 2;

    private String mBaseDir;

    /**
     * Initialize manager.
     * @param conext is an application context.
     * @return Error code.
     */
    public int init(Context context) {
        int code = NO_ERRORS;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Initialize path to directories.
            mBaseDir = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.base_dir) + "/";
            for (String dirPath : new String[]{mBaseDir}) {
                File dir = new File(dirPath);
                if ((dir.exists() == false) && (dir.mkdirs() == false)) {
                    code = CANNOT_CREATE_DIR;
                }
            }
        }
        else {
            code = NOT_MOUNTED;
        }
        return code;
    }

    /**
     * Get application's base directory located in external storage.
     * @return Full path to base directory.
     */
    public String getBaseDir() {
        return mBaseDir; 
    }
}
