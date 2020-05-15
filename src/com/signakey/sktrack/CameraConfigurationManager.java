package com.signakey.sktrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.List;
import java.util.regex.Pattern;

public class CameraConfigurationManager {

//	private static final String TAG = CameraConfigurationManager.class.getSimpleName();
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	
	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;
	private int previewFormat;
	private String previewFormatString;
	  

	  
	public CameraConfigurationManager(Context context) {
	    this.context = context;
	   
	}   
	
	/**
	   * Reads, one time, values from the camera that are needed by the app.
	   */
	public void initFromCameraParameters(Camera camera) {
	    Camera.Parameters parameters = camera.getParameters();
//		parameters.setFlashMode("off");

		previewFormat = parameters.getPreviewFormat();
	    previewFormatString = parameters.get("preview-format");
	    Log.i("SKCamera", "CamConfig Line 39 Default preview format: " + previewFormat + '/' + previewFormatString);
	  
	  WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	  final DisplayMetrics displayMetrics = new DisplayMetrics();
	   Display display = manager.getDefaultDisplay();
	  

	   
	    display.getMetrics(displayMetrics);
	    @SuppressWarnings("deprecation")
		int Screenheight = display.getHeight();
	    @SuppressWarnings("deprecation")
		int screenweight = display.getWidth();
	   
	    
	 
	 
	    screenResolution = new Point(Screenheight,screenweight);
	    Log.i("SKCamera", "CamConfig Line 44 Screen resolution: " + screenResolution);
	    
	    cameraResolution = getCameraResolution(parameters, screenResolution);
	    Log.i("SKCamera", "CamConfig Line 47 Camera resolution: " + screenResolution);
	}
	

	public void setDesiredCameraParameters(Camera camera) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        int mode = Integer.parseInt(preferences.getString("mode", "3"), 10);
		Log.i("SKCamera", "CamConfig: Line 59 Set camera parameters.");
	    Camera.Parameters parameters = camera.getParameters();
	    Log.i("SKCamera", "CamConfig: Line 61 Setting preview size: " + cameraResolution);
	    parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
	   
	    Log.i("SKCamera", "CamConfig: Line 63 Setting preview cameraResolution.x " + cameraResolution.x + "  cameraResolution.y " + cameraResolution.y );
	    @SuppressWarnings("unused")
		List<Integer> ratios = parameters.getZoomRatios();
	    // This is the standard setting to turn the flash off that all devices should honor.
	    parameters.set("flash-mode", "off");
	    
	    // Set zoom to 2x if available. This helps encourage the user to pull back.
	    // Some devices like the Behold have a zoom parameter
	    if (mode == 2){
	    	parameters.set("zoom", "2.0");
	    	Log.i("SKCamera", "CamConfig: Line 71 set Zoom = 2.0 " );
	   	    parameters.set("taking-picture-zoom", "20");
	   	    Log.i("SKCamera", "CamConfig: Line 73 set Zoom = 20 " ); 
	    	}
	    
		if(mode == 1) {

	               Log.i("SKCamera", "CameraConfig Line 78 zoom = 2.0" );
	               parameters.set("zoom","2.0"); 
//	               parameters.setZoom(6);
//	            } 
	            // Set Camera for steadymode
//	            List<String> scmodes = parameters.getSupportedSceneModes();
//	             if ((scmodes != null) && scmodes.contains(Camera.Parameters.SCENE_MODE_STEADYPHOTO)) {
//	                 Log.i("SKCamera", "CameraConfig Line 94: SCENE_MODE_STEADYPHOTO ");
//	                 parameters.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
//
//
//	             }
	    	
	    	 }
	   camera.setDisplayOrientation(90);   
	    
	    camera.setParameters(parameters); 
	    
	    
	    Log.i("SKCamera", "CameraConfig Line 93 parameters " + parameters  );
	    }
	
	public Point getCameraResolution() {
	    return cameraResolution;
	}

	public Point getScreenResolution() {
	    return screenResolution;
	}

	public int getPreviewFormat() {
	    return previewFormat;
	}

	public String getPreviewFormatString() {
	    return previewFormatString;
	}
	
	private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
		String previewSizeValueString = parameters.get("preview-size-values");
		Log.i("SKCamera", "CamConfig: Line 115 get Camera Resolution." + previewSizeValueString);
		// saw this on Xperia
	    if (previewSizeValueString == null) {
	      previewSizeValueString = parameters.get("preview-size-value");
	      Log.i("SKCamera", "CamConfig: Line 119 get previewSizeValueString." + previewSizeValueString);
	    }

	    Point cameraResolution = null;

	    if (previewSizeValueString != null) {
	    	
	      cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
	      Log.i("SKCamera", "CamConfig: Line 125. preview-size-values parameter: " + cameraResolution);
	    }

	    if (cameraResolution == null) {
	      // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
	      cameraResolution = new Point(
	          (screenResolution.x >> 2) << 2,
	          (screenResolution.y >> 2) << 2);
	    }
	    Log.i("SKCamera", "CamConfig: Line 136. preview-size-values parameter: " + cameraResolution);
	    return cameraResolution;
	}
	
	private static Point findBestPreviewSizeValue(String previewSizeValueString,
		      Point screenResolution) {
		    int bestX = 0;
		    int bestY = 0;
		    int diff = Integer.MAX_VALUE;
		    Log.i("SKCamera", "CamConfig: Line 145 findBestPreviewSizeValue.");
		    for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

		      previewSize = previewSize.trim();
		      int dimPosition = previewSize.indexOf('x');
		      if (dimPosition < 0) {
		    	  Log.i("SKCamera", "CamConfig: Line 151 preview =" + previewSize);
		        continue;
		      }

		      int newX;
		      int newY;
		      try {
		        newX = Integer.parseInt(previewSize.substring(0, dimPosition));
		        newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
		      } catch (NumberFormatException nfe) {
		        Log.i("SKCamera", "CamConfig Line 161 Bad preview-size: " + previewSize);
		        continue;
		      }

		      int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
		      if (newDiff == 0) {
		        bestX = newX;
		        bestY = newY;
		        break;
		      } else if (newDiff < diff) {
		        bestX = newX;
		        bestY = newY;
		        diff = newDiff;
		      }

		    }

		    if (bestX > 0 && bestY > 0) {
		    	//bestX = 854;
		    	//bestY = 480;
		    	Log.i("SKCamera", "CamConfig Line 179 best preview size best x = " + bestX + " bestY = " + bestY);
		      return new Point(bestX, bestY);
		    }
		    return null;
	}
	
	
}
