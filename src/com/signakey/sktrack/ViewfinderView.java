package com.signakey.sktrack;

//import tw.com.SKScanner.sdk.demo.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ViewfinderView extends View {
	private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
	private static final long ANIMATION_DELAY = 100L;
	
	private final Paint paint;
	private final int maskColor;
	private final int frameColor;
	private final int laserColor;
	private int scannerAlpha;
	
	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    Log.i("SKScanner", "ViewFinder Line 26.");
	    // Initialize these once for performance rather than calling them every time in onDraw().
	    paint = new Paint();
	    Resources resources = getResources();
	    maskColor = resources.getColor(R.color.viewfinder_mask);
	    frameColor = resources.getColor(R.color.viewfinder_frame);
	    laserColor = resources.getColor(R.color.viewfinder_laser);
	    scannerAlpha = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.i("SKScanner", "ViewFinder onDraw Line 38.");
		Rect frame = CameraManager.get().getFramingRect();
	    if (frame == null) {
	      return;
	    }
	    
	    int width = canvas.getWidth();
	    int height = canvas.getHeight();
	    
	    // Draw the exterior (i.e. outside the framing rect) darkened
	    paint.setColor(maskColor);
	    canvas.drawRect(0, 0, width, frame.top, paint);
	    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
	    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
	    canvas.drawRect(0, frame.bottom + 1, width, height, paint);
	    Log.i("SKScanner", "ViewFinder Line 53 Draw exterior fram maskColor " + maskColor);
	    // Draw a two pixel solid black border inside the framing rect
        paint.setColor(frameColor);
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
        canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
        canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
        canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
        Log.i("SKScanner", "ViewFinder Line 60 Draw 2 pixel frame " + frameColor);
	    // Draw a red "laser scanner" line through the middle to show decoding is active
	    paint.setColor(laserColor);
	    paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
	    scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
	    int middle = frame.height() / 2 + frame.top;
	    canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
	    Log.i("SKScanner", "ViewFinder Line 67 Draw exterior fram laserColor " + laserColor);
	    // Request another update at the animation interval, but only repaint the laser line,
	    // not the entire viewfinder mask.
	    postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
	}
	
	public void drawViewfinder() {
		Log.i("SKScanner", "ViewFinder Line 74 invalidate.");
	    invalidate();
	}

}
