package com.signakey.sktrack;

import java.io.File;
import java.util.List;

import com.cvisionlab.zxing.KeyInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class History extends Activity {
    @SuppressWarnings("unused")
	private List<KeyInterface> mItems;

    public class HistoryAdapter extends BaseAdapter {
        private Context mContext;
        private List<KeyInterface> mItems;

        public HistoryAdapter(Context context, List<KeyInterface> items) {
            super();

            mContext = context;
            mItems = items;
        }

        /** Get count of items in history. */
        public int getCount() {
            return mItems.size();
        }

        /** Get item's id. */
        public long getItemId(int position) {
            return position;
        }

        /** Get view for specified index. */
        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflate.
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout v = (LinearLayout)vi.inflate(R.layout.history_item, null);

            // Find sub-views.
            final ImageView thumb = (ImageView) v.findViewById(R.id.thumb);
            final TextView timestamp = (TextView) v.findViewById(R.id.timestamp);
            final TextView type = (TextView) v.findViewById(R.id.type);

            // Get key.
            KeyInterface item = mItems.get(position);

            // Set thumbnail, timestamp, type to views.
            Bitmap bitmap = item.getBitmap();
            if (bitmap != null) {
                thumb.setImageBitmap(bitmap);
            }
            timestamp.setText(item.getTimestampAsString());
            type.setText(item.getType());

            return v;
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        public List<KeyInterface> getItems() {
            return mItems;
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        _clearHistory();
        _initInfoButton();
    }



    /** Initialize "Info" button. */
    private void _initInfoButton() {
        Button button = (Button) findViewById(R.id.btn_info);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Info.class);
                startActivity(intent);
            }
        });
    }

    /** Remove all items from history. */
    private void _clearHistory() {
        StorageManager storage = new StorageManager();
        if (storage.init(getApplicationContext()) == StorageManager.NO_ERRORS) {
            // List tracks in directory.
            File[] files = (new File(storage.getBaseDir())).listFiles();
            for (File f: files) {
                f.delete();
            }

            // Update list.
           
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                onBackPressed();
                return true;

            case R.id.clear:
                _clearHistory();
                return true;

            default:
                break;
        }

        return false;
    }
}
