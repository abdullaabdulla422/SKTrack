package com.signakey.sktrack;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.MediaFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ResMedia extends Activity implements OnBufferingUpdateListener {

	Logger logger;
	private ProgressDialog mProgressDialog;

	@Override
		protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.media);
			Button button = (Button) findViewById(R.id.btn_info);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ResMedia.this, Info.class);
					startActivity(intent);
				}
			});
	        _initMediaButton1();
	        _initMediaButton2();
	        _initMediaButton3();
	        _initMediaButton4();
	        _initMediaButton5();
	        _initMediaButton6();

		}
		
		@Override
		protected void onResume() {
			super.onResume();
			
			
			int _mediafiles = 0;
			_mediafiles =	CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.size();
			if (_mediafiles >0){
				Log.i("SKScanner", "ResMedia set number of media files attached _mediafiles= " + _mediafiles);

				logger.addLog("SKScanner : ResMedia," +
						"ResMedia set number of media files attached _mediafiles= "
						+ _mediafiles);
			if(_mediafiles >= 1){
				Button button1 = (Button) findViewById(R.id.btn_media1);
				MediaFile mediafile =  CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(0);
				_setbuttontext(mediafile, button1);
				button1.setVisibility(View.VISIBLE); //To set visible
				}
			if(_mediafiles >= 2){
				Button button2 = (Button) findViewById(R.id.btn_media2);
				MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(1);
				_setbuttontext(mediafile,button2);
				button2.setVisibility(View.VISIBLE); //To set visible
				}
			if(_mediafiles >= 3){
				Button button3 = (Button) findViewById(R.id.btn_media3);
				MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(2);
				_setbuttontext(mediafile,button3);
				button3.setVisibility(View.VISIBLE); //To set visible
				}
			if(_mediafiles >= 4){
				Button button4 = (Button) findViewById(R.id.btn_media4);
				MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(3);
				_setbuttontext(mediafile,button4);
				button4.setVisibility(View.VISIBLE); //To set visible
				}
			if(_mediafiles >= 5){
				Button button5 = (Button) findViewById(R.id.btn_media5);
				MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(4);
				_setbuttontext(mediafile,button5);
				button5.setVisibility(View.VISIBLE); //To set visible
				}
			if(_mediafiles == 6){
				Button button6 = (Button) findViewById(R.id.btn_media6);
				MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(5);
				_setbuttontext(mediafile, button6);
				button6.setVisibility(View.VISIBLE); //To set visible
				}
			}
		}
		// Back button pressed
//	    public boolean onKeyDown(int keyCode, KeyEvent event){ 
//    	Log.i("SKScanner", "ResMedia: Line 98 Back Button pressed keyCode = " + keyCode + " KeyEvent=" + event);
//    	super.onKeyDown(keyCode, event);
//        if(keyCode == KeyEvent.KEYCODE_BACK) { 
//        	Log.i("SKScanner", "ResMedia: Line 101 Back Button pressed keyCode = " + keyCode + " KeyEvent=" + event);
//            Intent Act2Intent = new Intent(this, ResMedia.class);               
//            startActivity(Act2Intent);           
//            finish(); 
//            return true; 
//    } 
//    return false; 
//    } 
		@Override
		protected void onPause() {
		//	finish(); 
			super.onPause();
			
		}

	    /** Initialize "Media" button. */
	    private void _initMediaButton1() {
	    	Log.i("SKScanner", "ResMedia _initMediaButton 1 ");
	        Button button1 = (Button) findViewById(R.id.btn_media1);
	 //       button.setVisibility(0);
	        button1.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(0);
	            	Log.i("SKScanner", "ResMedia: RestrictedFileList(0)." + mediafile);
	            	if(mediafile.URL != null){   // the media file has a URL attached.
	            		switch (mediafile.Medium) {
	            		case 0: // medium not recognized
	            			Toast.makeText(getApplicationContext(), "ResMedia: Media type is not recognized.", Toast.LENGTH_LONG).show();
	            		return;
	            		case 1:  // Display picture
	            			_displaypicture(mediafile);
	        			return;
	            		case 2:  // Play sound file
	            			_playsound(mediafile);
	        			return;
	            		case 3:  // Display PDF file
	            			_displayPDF(mediafile);
	        			return;
	            		case 4:  // Play video
	            			_playvideo(mediafile);
	        			return;
	            		case 5:	// Go to Link
	            			_gotolink(mediafile);
	        			return;
	        			
	        			
	            		}
	            	}
	              	Toast.makeText(getApplicationContext(), "Media: Media is not attached..", Toast.LENGTH_LONG).show();
	            }
	        });
	    }
	    /** Initialize "Media" button. */
	    private void _initMediaButton2() {
	        	Log.i("SKScanner", "ResMedia: _initMediaButton2 ");
	            Button button2 = (Button) findViewById(R.id.btn_media2);
	     //       button.setVisibility(0);
	            button2.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(1);
	                	Log.i("SKScanner", "ResMedia: RestrictedFileList(1)." + mediafile);
	                	if(mediafile.URL != null){   // the media file has a URL attached.
	                		switch (mediafile.Medium) {
	                		case 0: // medium not recognized
	                			Toast.makeText(getApplicationContext(), "ResMedia: Media type is not recognized.", Toast.LENGTH_LONG).show();
	                		return;
	                		case 1:  // Display picture
	                			_displaypicture(mediafile);
	            			return;
	                		case 2:  // Play sound file
	                			_playsound(mediafile);
	            			return;
	                		case 3:  // Display PDF file
	                			_displayPDF(mediafile);
	            			return;
	                		case 4:  // Play video
	                			_playvideo(mediafile);
	            			return;
	                		case 5:	// Go to Link
	                			_gotolink(mediafile);
	            			return;
	            			
	            			
	                		}
	                	}
	                  	Toast.makeText(getApplicationContext(), "ResMedia: Media is not attached..", Toast.LENGTH_LONG).show();
	                }
	        });
	    }
	    /** Initialize "Media" button. */
	    private void _initMediaButton3() {
	            	Log.i("SKScanner", "ResMedia: _initMediaButton3 ");
	                Button button3 = (Button) findViewById(R.id.btn_media3);
	         //       button.setVisibility(0);
	                button3.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(2);
	                	Log.i("SKScanner", "ResMedia: RestrictedFileList(2)." + mediafile);
	                	if(mediafile.URL != null){   // the media file has a URL attached.
	                		switch (mediafile.Medium) {
	                		case 0: // medium not recognized
	                			Toast.makeText(getApplicationContext(), "ResMedia: Media type is not recognized.", Toast.LENGTH_LONG).show();
	                		return;
	                		case 1:  // Display picture
	                			_displaypicture(mediafile);
	            			return;
	                		case 2:  // Play sound file
	                			_playsound(mediafile);
	            			return;
	                		case 3:  // Display PDF file
	                			_displayPDF(mediafile);
	            			return;
	                		case 4:  // Play video
	                			_playvideo(mediafile);
	            			return;
	                		case 5:	// Go to Link
	                			_gotolink(mediafile);
	            			return;
	            			
	            			
	                		}
	                	}
	                  	Toast.makeText(getApplicationContext(), "ResMedia: Media is not attached..", Toast.LENGTH_LONG).show();
	                    }
	        });
	    }
	    /** Initialize "Media" button. */
	    private void _initMediaButton4() {
	    		Log.i("SKScanner", "ResMedia: _initMediaButton4 ");
	            Button button4 = (Button) findViewById(R.id.btn_media4);
	          
	            button4.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(3);
	                	Log.i("SKScanner", "ResMedia: RestrictedFileList(3)." + mediafile);
	                	if(mediafile.URL != null){   // the media file has a URL attached.
	                		switch (mediafile.Medium) {
	                		case 0: // medium not recognized
	                			Toast.makeText(getApplicationContext(), "ResMedia: Media type is not recognized.", Toast.LENGTH_LONG).show();
	                		return;
	                		case 1:  // Display picture
	                			_displaypicture(mediafile);
	            			return;
	                		case 2:  // Play sound file
	                			_playsound(mediafile);
	            			return;
	                		case 3:  // Display PDF file
	                			_displayPDF(mediafile);
	            			return;
	                		case 4:  // Play video
	                			_playvideo(mediafile);
	            			return;
	                		case 5:	// Go to Link
	                			_gotolink(mediafile);
	            			return;
	            			
	            			
	                		}
	                	}
	                  	Toast.makeText(getApplicationContext(), "ResMedia: Media is not attached..", Toast.LENGTH_LONG).show();
	                    }
	        });
	    }
	    /** Initialize "Media" button. */
	    private void _initMediaButton5() {
	        	Log.i("SKScanner", "ResMedia:  _initMediaButton5 ");
	            Button button5 = (Button) findViewById(R.id.btn_media5);
	              
	            button5.setOnClickListener(new OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                    	MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(4);
	                    	Log.i("SKScanner", "ResMedia: RestrictedFileList(4)." + mediafile);
	                    	if(mediafile.URL != null){   // the media file has a URL attached.
	                    		switch (mediafile.Medium) {
	                    		case 0: // medium not recognized
	                    			Toast.makeText(getApplicationContext(), "ResMedia: Media type is not recognized.", Toast.LENGTH_LONG).show();
	                    		return;
	                    		case 1:  // Display picture
	                    			_displaypicture(mediafile);
	                			return;
	                    		case 2:  // Play sound file
	                    			_playsound(mediafile);
	                			return;
	                    		case 3:  // Display PDF file
	                    			_displayPDF(mediafile);
	                			return;
	                    		case 4:  // Play video
	                    			_playvideo(mediafile);
	                			return;
	                    		case 5:	// Go to Link
	                    			_gotolink(mediafile);
	                			return;
	                			
	                			
	                    		}
	                    	}
	                      	Toast.makeText(getApplicationContext(), "ResMedia: Media is not attached..", Toast.LENGTH_LONG).show();
	                        }
	            });
	    }
	    /** Initialize "Media" button. */
	    private void _initMediaButton6() {
	        	Log.i("SKScanner", "ResMedia:  _initMediaButton6 ");
	            Button button6 = (Button) findViewById(R.id.btn_media6);
	                  
	            button6.setOnClickListener(new OnClickListener() {
	            		@Override
	                    public void onClick(View v) {
	                    	MediaFile mediafile = CaptureActivity.globalresult.ItemList.get(CaptureActivity.globalresult.TargetIndex).RestrictedFileList.get(5);
	                    	Log.i("SKScanner", "ResMedia: RestrictedFileList(5)." + mediafile);
	                    	if(mediafile.URL != null){   // the media file has a URL attached.
	                    		switch (mediafile.Medium) {
	                    		case 0: // medium not recognized
	                    			Toast.makeText(getApplicationContext(), "ResMedia: Media type is not recognized.", Toast.LENGTH_LONG).show();
	                    		return;
	                    		case 1:  // Display picture
	                    			_displaypicture(mediafile);
	                			return;
	                    		case 2:  // Play sound file
	                    			_playsound(mediafile);
	                			return;
	                    		case 3:  // Display PDF file
	                    			_displayPDF(mediafile);
	                			return;
	                    		case 4:  // Play video
	                    			_playvideo(mediafile);
	                			return;
	                    		case 5:	// Go to Link
	                    			_gotolink(mediafile);
	                			return;
	                			
	                			
	                    		}
	                    		
	                    	}
	                    	Toast.makeText(getApplicationContext(), "ResMedia: Media is not attached..", Toast.LENGTH_LONG).show();
	                        }
	            });
	    }

	        // Handle the image Media
//	    private void _displaypicture(MediaFile mediafile) {
//
//	        ImageView imView = null;
//
//	           setContentView(R.layout.mediadisplay);
//	           imView = (ImageView)findViewById(R.id.imview);
//
//	           	Bitmap bmImg = null;
//
//
//	                 URL myFileUrl = null;
//					try {
//						myFileUrl = new URL(mediafile.URL);
//						Log.i("SKScanner", "ResMedia _displayPicture myFileUrl = " + myFileUrl );
//
//						logger.addLog("SKScanner : " +
//								"ResMedia _displayPicture myFileUrl = " + myFileUrl);
//					} catch (MalformedURLException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//						Log.i("SKScanner", "ResMedia _displayPicture MalformedURLException = " + e1);
//
//						logger.addLog("SKScanner : " +
//								"ResMedia _displayPicture MalformedURLException = " + e1 );
//					}
//
//	             try {
//	                  HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
//	                  Log.i("SKScanner", "ResMedia _displayPicture conn = " + conn);
//
//
//					 conn.setDoInput(true);
//	                  conn.connect();
//	                  InputStream is = conn.getInputStream();
//	                  Log.i("SKScanner", "ResMedia _displayPicture is = " + is );
//					 logger.addLog("SKScanner : " +
//							 "ResMedia _displayPicture is = " + is);
//
//					 bmImg = BitmapFactory.decodeStream(is);
//	                  Log.i("SKScanner", "ResMedia _displayPicture bmImg = " + bmImg );
//	                  imView.setImageBitmap(bmImg);
//	                  conn.disconnect();
//
//	             } catch (IOException e) {
//	                  // TODO Auto-generated catch block
//	                  e.printStackTrace();
//	                  Log.i("SKScanner", "ResMedia _displayPicture IOException e = " + e);
//
//					 logger.addLog("SKScanner : " +
//							 "ResMedia _displayPicture IOException e = " + e );
//	             }
//	             ImageView imview = (ImageView) findViewById(R.id.imview);
//	             imview.setOnClickListener(new OnClickListener() {
//	                 @Override
//	                 public void onClick(View v) {
//	                 //    Intent intent = new Intent(this, Media.class);
//	                 //    startActivity(intent);
//	                	// onResume();
//
//	                	 finish();
//
//	                 }
//	             });
//	        }
	 
	    
//	    private void _playsound(MediaFile mediafile)  {
//
//	        MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(mediafile.URL));
//	        Log.i("SKScanner", "ResMedia _playsound = " + mediafile.URL);
//			mediaPlayer.start();
//
//
//	    }
	    
	    private void _displayPDF(MediaFile mediafile) {
	    	
	    	String googleDocsUrl = "http://docs.google.com/viewer?url="; 
	    	 
	    	Intent intent = new Intent(Intent.ACTION_VIEW); 
	    	intent.setData(Uri.parse(googleDocsUrl + mediafile.URL));
	    	Log.i("SKScanner", "ResMedia _displayPDF intent " + intent);
	    	 startActivity( intent  );
	    	
	    }
//	    private void _playvideo(MediaFile mediafile) {
//
//			String LINK = mediafile.URL;
//			Log.i("SKScanner", "Media _playVideo LINK = " + LINK);
//
//			logger.addLog("SKScanner : " +
//					"ResMedia __playVideo LINK = " + LINK);
//			setContentView(R.layout.videodisplay);
//			VideoView videoView = (VideoView) findViewById(R.id.videoView1);
//
//
//			String path=mediafile.URL;
//
//			MediaController mediaController = new MediaController(this);
//
//
//			mediaController.setMediaPlayer(videoView);
//
//			videoView.setVideoPath(path);
//
//			videoView.setMediaController(mediaController);
//
//			videoView.requestFocus();
//
//			videoView.start();
//
//			mediaController.show();
//			mediaController.clearFocus();
//			mediaController.destroyDrawingCache();
//			mediaController.getParent();
//
//	    }
		private void _gotolink(MediaFile mediafile){
			String url = mediafile.URL;
		    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( url ) ); 
		    
		    startActivity( browse );
			
		}
		// Set button text
		private void _setbuttontext(MediaFile mediafile,Button button){
			
			String _text = mediafile.Identifier;
			_text = _text.replace("$", "\n");
			button.setText(_text);
		}

		@Override
		public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

	private void _displaypicture(MediaFile mediafile) {


		URL myFileUrl = null;
		try {
			myFileUrl = new URL(mediafile.URL);
			Log.i("SKScanner", "ResMedia _displayPicture myFileUrl = " + myFileUrl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.i("SKScanner", "ResMedia _displayPicture MalformedURLException = " + e1);
		}

		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			Log.i("SKScanner", "ResMedia _displayPicture conn = " + conn);
			conn.setDoInput(true);
			new AsyncLoadPicture().execute(conn);

//			conn.connect();
//			InputStream is = conn.getInputStream();
//			Log.i("SKScanner", "ResMedia _displayPicture is = " + is);
//
//			bmImg = BitmapFactory.decodeStream(is);
//			Log.i("SKScanner", "ResMedia _displayPicture bmImg = " + bmImg);


			conn.disconnect();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("SKScanner", "ResMedia _displayPicture IOException e = " + e);
		}


	}

	private void _playsound(MediaFile mediafile) {
//        showProgress();
//        final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(mediafile.URL));

		final MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource((mediafile.URL));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mediaPlayer.prepareAsync();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		Log.i("SKScanner", "ResMedia _playsound = " + mediafile.URL);
		mediaPlayer.start();

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.i("SKScanner", "ResMedia _playsound onPrepared ");
				mediaPlayer.start();

//                dismissProgress();
			}
		});


		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                Log.i("SKScanner", "ResMedia _playsound onBufferingUpdate onBufferingUpdate= " + percent);
				if (percent == 100) {

					dismissProgress();

				} else {
				}
			}
		});
		if (mProgressDialog != null) {
			mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
//                    mediaPlayer.stop();
				}
			});
		} else {
			showProgress();
		}

	}

	private void _playvideo(MediaFile mediafile) {

		showProgressCancellable();

		String LINK = mediafile.URL;
		Log.i("SKScanner", "ResMedia _playVideo LINK = " + LINK);
		setContentView(R.layout.videodisplay);
		final VideoView videoView = (VideoView) findViewById(R.id.videoView1);

		String path = mediafile.URL;

//        AsyncPlayVideo(path);

		MediaController mediaController = new MediaController(this);


		mediaController.setMediaPlayer(videoView);

		videoView.setVideoPath(path);

		videoView.setMediaController(mediaController);

		videoView.requestFocus();

//		dismissProgress();

		videoView.start();


//
		mediaController.show();
		mediaController.clearFocus();
		mediaController.destroyDrawingCache();
		mediaController.getParent();
		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				dismissProgress();

				return false;
			}
		});

		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {

				mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						Log.i("SKScanner", "ResMedia _playvideo onBufferingUpdate = " + percent);
						if (percent==100){
//                            videoView.start();
							mp.start();
							dismissProgress();
						}
					}
				});

//                videoView.start();
//                dismissProgress();

			}
		});
	}


	public void showProgress() {
//    	if (mProgressDialog!=null)
		mProgressDialog = new ProgressDialog(ResMedia.this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}


	public void showProgressCancellable() {
//    	if (mProgressDialog!=null)
		mProgressDialog = new ProgressDialog(ResMedia.this);
		mProgressDialog.setCancelable(true);
//        mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	public void dismissProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	public class AsyncLoadPicture extends AsyncTask<HttpURLConnection, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected Bitmap doInBackground(HttpURLConnection... connections) {
			Bitmap bmImg = null;

			try {

				HttpURLConnection conn = connections[0];
				conn.connect();
				InputStream is = conn.getInputStream();
				Log.i("SKScanner", "ResMedia _displayPicture is = " + is);

				bmImg = BitmapFactory.decodeStream(is);
				Log.i("SKScanner", "ResMedia _displayPicture bmImg = " + bmImg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bmImg;
		}

		@Override
		protected void onPostExecute(Bitmap bmImg) {

//			Bitmap bmImg = bitmaps[0];
			ImageView imView = null;

			setContentView(R.layout.mediadisplay);
			imView = (ImageView) findViewById(R.id.imview);


			if (bmImg != null) {
				imView.setImageBitmap(bmImg);
			}

			ImageView imview = (ImageView) findViewById(R.id.imview);
			imview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//   Intent intent = new Intent(this, ResMedia.class);
					// startActivity(intent);
					//onResume();

					finish();

				}
			});

			dismissProgress();
		}
	}


}


