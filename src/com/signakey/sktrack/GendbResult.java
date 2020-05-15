package com.signakey.sktrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.signakey.sktrack.skclient.WebMarkDecodeSymbolsResult;

public class GendbResult extends Activity {
    Button detailinfo, back;


    public WebMarkDecodeSymbolsResult result;


    //int version, client, item, sequence;
    public String dresult;


    private SignaKeyClient mSkClient = null;

    TextView webresult;

    //genresult = SessionData.getInstance().getGenkey();


    //views.setText(result);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gendb);
        webresult = (TextView) findViewById(R.id.gendbview);


        mSkClient = new SignaKeyClient(this);
        detailinfo = (Button) findViewById(R.id.btn_info);


        webresult.setText(dresult);
        detailinfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        back = (Button) findViewById(R.id.btnbackview);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });


    }

    public void onBackPressed() {
        Intent inten = new Intent(GendbResult.this, SKScanner.class);
		/* intent.addCategory(Intent.CATEGORY_HOME);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        startActivity(inten);
    }

    ;


//	


}





			
	

