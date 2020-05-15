package com.signakey.sktrack;

import java.io.IOException;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public final class BeepManager {

	private static final String TAG = BeepManager.class.getSimpleName();

	private static final float BEEP_VOLUME = 0.20f;
	private static final long VIBRATE_DURATION = 200L;

	private final Context activity;
	private MediaPlayer mediaPlayer;
	private boolean playBeep; 
	private boolean vibrate;
	private SharedPreferences mPreferences = null;
	public static int Filenumb = 0;
	public static String company = null;

	public BeepManager(Activity activity) {
		this.activity = activity;
		this.mediaPlayer = null;
		mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		company = mPreferences.getString("company", "");
		updatePrefs();
	}

	public void updatePrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		playBeep = shouldBeep(prefs, activity);
		vibrate = prefs.getBoolean("vibrate", false);
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			((Activity) activity)
					.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = buildMediaPlayer(activity);
		}
	}

	public void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) activity
					.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private static boolean shouldBeep(SharedPreferences prefs, Context activity) {
		boolean shouldPlayBeep = prefs.getBoolean("beep", true);
		if (shouldPlayBeep) {
			// See if sound settings overrides this
			AudioManager audioService = (AudioManager) activity
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				shouldPlayBeep = false;
			}
		}
		return shouldPlayBeep;
	}

	private static MediaPlayer buildMediaPlayer(Context activity) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// When the beep has finished playing, rewind to queue up another one.
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer player) {
						player.seekTo(0);
					}
				});

		if (company.equals("Pfizer")) {
			Filenumb = R.raw.dogbark;
		} else if (company.equals("GMPP")) {
			Filenumb = R.raw.hornhonk;
		} else if (company.equals("Ford")) {
			Filenumb = R.raw.oogahorn;
		} else if (company.equals("SignaKey")) {
			Filenumb = R.raw.tada;
		} else {
			Filenumb = R.raw.beep;
		}

		AssetFileDescriptor file = activity.getResources().openRawResourceFd(
				Filenumb);

		try {
			mediaPlayer.setDataSource(file.getFileDescriptor(),
					file.getStartOffset(), file.getLength());
			file.close();
			mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			mediaPlayer = null;
		}
		return mediaPlayer;
	}

}
