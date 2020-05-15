package com.signakey.sktrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MediaFiles extends Activity {


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_files);
        _initPubFiles();
        _initConFiles();
        _initResFiles();
        _initPriFiles();
        Button button = (Button) findViewById(R.id.btn_info);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaFiles.this, Info.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (SKScanner._pubmediafiles > 0) {
            Button button1 = (Button) findViewById(R.id.btn_pubmediafiles);
            button1.setText("Pub Media");
            button1.setEnabled(true); //Enable
        }
        if (SKScanner._conmediafiles > 0) {
            Button button2 = (Button) findViewById(R.id.btn_conmediafiles);
            button2.setText("Con Media");
            button2.setEnabled(true); //Enable
        }
        if (SKScanner._resmediafiles > 0) {
            Button button3 = (Button) findViewById(R.id.btn_resmediafiles);
            button3.setText("Res Media");
            button3.setEnabled(true); //Enable
        }
        if (SKScanner._primediafiles > 0) {
            Button button4 = (Button) findViewById(R.id.btn_primediafiles);
            button4.setText("Pri Media");
            button4.setEnabled(true); //Enable
        }
    }


    // Back button pressed
//    public boolean onKeyDown(int keyCode, KeyEvent event){ 
//    	return super.onKeyDown(keyCode, event); 
//    } 
    @Override
    protected void onPause() {
        super.onPause();

    }

    private void _initPubFiles() {
        Log.i("SKScanner", "MediaFiles _initPubFiles ");
        Button button1 = (Button) findViewById(R.id.btn_pubmediafiles);
        button1.setEnabled(false);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Media.class);
                startActivity(intent);
            }
        });
    }

    private void _initConFiles() {
        Log.i("SKScanner", "MediaFiles _initConFiles ");
        Button button2 = (Button) findViewById(R.id.btn_conmediafiles);
        button2.setEnabled(false);
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConMedia.class);
                startActivity(intent);
            }
        });
    }

    private void _initResFiles() {
        Log.i("SKScanner", "MediaFiles _initResFiles ");
        Button button3 = (Button) findViewById(R.id.btn_resmediafiles);
        button3.setEnabled(false);
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResMedia.class);
                startActivity(intent);
            }
        });
    }

    private void _initPriFiles() {
        Log.i("SKScanner", "MediaFiles _initPriFiles ");
        Button button4 = (Button) findViewById(R.id.btn_primediafiles);
        button4.setEnabled(false);
        button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PriMedia.class);
                startActivity(intent);
            }
        });

    }
}
