package com.signakey.sktrack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.signakey.sktrack.skclient.DbPattern;
import com.signakey.sktrack.skclient.DbRequest;
import com.signakey.sktrack.skclient.DetailPattern;
import com.signakey.sktrack.skclient.Details;
import com.signakey.sktrack.skclient.KeySelectorSerial;
import com.signakey.sktrack.skclient.Logger;
import com.signakey.sktrack.skclient.MediaFileMatch;
import com.signakey.sktrack.skclient.NumberMatch;
import com.signakey.sktrack.skclient.ServicesBaseSoap;
import com.signakey.sktrack.skclient.SessionData;
import com.signakey.sktrack.skclient.SignaKeyMatch;
import com.signakey.sktrack.skclient.StringMatch;
import com.signakey.sktrack.skclient.TimestampMatch;
import com.signakey.sktrack.skclient.ValuePattern;
import com.signakey.sktrack.skclient.WebGenDbRequest;
import com.signakey.sktrack.skclient.WebGenDbRequestParams;
import com.signakey.sktrack.skclient.WebGenDbResult;
import com.signakey.sktrack.skclient.WebMarkAuthenticateParams;
import com.signakey.sktrack.skclient.WebMarkAuthenticateResult;
import com.signakey.sktrack.skclient.WebMarkAuthenticateSymbols;
import com.signakey.sktrack.skclient.WebMarkDecodeParams;
import com.signakey.sktrack.skclient.WebMarkDecodeSymbols;
import com.signakey.sktrack.skclient.WebMarkDecodeSymbolsResult;
import com.signakey.sktrack.skclient.WebValidateLogo;
import com.signakey.sktrack.skclient.WebValidateLogoParams;
import com.signakey.sktrack.skclient.WebValidateLogoResult;
import com.signakey.sktrack.skclient.WebValidateUser;
import com.signakey.sktrack.skclient.WebValidateUserParams;
import com.signakey.sktrack.skclient.WebValidateUserResult;
import com.signakey.sktrack.skclient.WidgetMatch;
import com.signakey.sktrack.skclient.WidgetPattern;
import com.signakey.sktrack.skclient.Widgets;

import org.kobjects.base64.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SignaKeyClient extends Activity {
	public static final int VALIDATE_STYLE = 3;
	public static final int TOKEN_MINUTES = -1;

	
	public static String Company = null;
	Logger logger;
	public static String Username = null;
	private Context mContext = null;
	private SharedPreferences mPreferences = null;
	private WebValidateUserResult mWebValidateUserResult = null;
	private WebValidateLogoResult mWebValidateLogoResult = null;
	private WebValidateLogoParams mWebValidateLogoParams = null;
	private boolean mPending = false;
	public boolean LoginStatus = false;
	public static String TOKEN = null;
	Date date = new Date() ;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
	

	@SuppressWarnings("unused")
	private boolean ReTry = false;
	

	public SignaKeyClient(Context context) {
		// Save context.
		mContext = context;

		// Get shared preferences.
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public boolean login() {
		mPending = true;

		// Prepare request params using values from preferences.
		WebValidateUser webValidateUser = new WebValidateUser();
		webValidateUser.inParams = new WebValidateUserParams();
		webValidateUser.inParams.ValidateStyle = VALIDATE_STYLE;

		webValidateUser.inParams.TokenMinutes = TOKEN_MINUTES;
		webValidateUser.inParams.Company = mPreferences
				.getString("company", "");

		webValidateUser.inParams.Account = mPreferences
				.getString("account", "");

		webValidateUser.inParams.UserName = mPreferences.getString("username",
				"");

		webValidateUser.inParams.Password = mPreferences.getString("password",
				"");
 
		Company = mPreferences.getString("company", "");


		logger.addLog(dateFormat.format(date) + ": " +"SKScanner: " + "WebValidateUser request, ValidateStyle:" +webValidateUser.inParams.ValidateStyle );
//		logger.addLog("TokenMinutes:" +webValidateUser.inParams.TokenMinutes );
//		logger.addLog("Company: " +webValidateUser.inParams.Company );
//		logger.addLog("Account: " +webValidateUser.inParams.Account );
//		logger.addLog("UserName: " +webValidateUser.inParams.UserName );
//		logger.addLog("Password: " +webValidateUser.inParams.Password );

		// Login.
		ServicesBaseSoap serviceBaseSoap = new ServicesBaseSoap();
		try {
			mWebValidateUserResult = serviceBaseSoap
					.WebValidateUser(webValidateUser);

			mPending = false;
			SignaKey.Token = mWebValidateUserResult.Token;
		} catch (Exception e) {

			logger.addLog("SKScanner: " + "WebValidateUser Exception " +e);
			mWebValidateUserResult = null;
			mPending = false;
			e.printStackTrace();
		}

		return isLoggedIn();
	}

	public boolean isLoggedIn() {
		LoginStatus = (mWebValidateUserResult != null)
				&& mWebValidateUserResult.Common.ServiceResultSuccess;

		return LoginStatus;
	}

	public String getToken() {
		// String token = null;
		if (isLoggedIn())
			TOKEN = mWebValidateUserResult.Token;

		return TOKEN;
	}

	public Integer keystyle() {
		return 3;
	}

	// This get the logo url based on the login success
	@SuppressWarnings("unused")
	private void _getLogoURL(String logourl) {
		WebValidateLogo webValidateLogo = new WebValidateLogo();
		webValidateLogo.inParams = new WebValidateLogoParams();
		mWebValidateLogoParams.Token = SignaKey.Token+SessionData.getInstance().getGpsCoordinates();
		mWebValidateLogoParams.Company = mPreferences.getString("company", "");
		Log.d("company", mWebValidateLogoParams.Company);


		mWebValidateLogoParams.Account = mPreferences.getString("account", "");
		mWebValidateLogoParams.UserName = mPreferences
				.getString("username", "");

		ServicesBaseSoap serviceBaseSoap = new ServicesBaseSoap();
		try {
			mWebValidateLogoResult = serviceBaseSoap
					.WebValidateLogo(webValidateLogo);
			mPending = false;
		} catch (Exception e) { 
			mWebValidateLogoResult = null;
			mPending = false;
			e.printStackTrace();
		}

		logourl = mWebValidateLogoResult.LogoURL;
		return;
	}

	public WebMarkAuthenticateResult authenticateSymbol(byte[] data) {
		mPending = true;
		WebMarkAuthenticateResult result = null;
		/*
		 * if (isLoggedIn() == false) { mPending = false; return result; }
		 */
		// _getToken();
		// Prepare request params.
		WebMarkAuthenticateSymbols webMarkAuthenticateSymbols = new WebMarkAuthenticateSymbols();
		webMarkAuthenticateSymbols.inParams = new WebMarkAuthenticateParams();
		webMarkAuthenticateSymbols.inParams.Token = SignaKey.Token+SessionData.getInstance().getGpsCoordinates();
		Log.d( "SKScanner","authenticateSymbol: + gps cordinates : "  +SessionData.getInstance().getGpsCoordinates()+"" +
				"SignaKey.Token"+SignaKey.Token+"");

		webMarkAuthenticateSymbols.inParams.InCustomer = mPreferences
				.getString("company", "");



//		webMarkAuthenticateSymbols.inParams.InCustomer = "";
		webMarkAuthenticateSymbols.inParams.InData = Base64.encode(data);
		webMarkAuthenticateSymbols.inParams.InConfirm = CaptureActivity.DataMatrixResult;
		Log.i("SKScanner",
				"SignaKeyClient: Line 128 Prepared parameters. Token = "
						+ SignaKey.Token+SessionData.getInstance().getGpsCoordinates());

		System.out.println("GPS 1 SignaKeyClient " +SessionData.getInstance().getGpsCoordinates() );

	//	Toast.makeText(getApplicationContext(),"SignaKeyClient "+SessionData.getInstance().getGpsCoordinates(),Toast.LENGTH_SHORT).show();

		if (mPreferences.getString("account", "").length() != 0) {
			webMarkAuthenticateSymbols.inParams.InCustomer = webMarkAuthenticateSymbols.inParams.InCustomer
					+ "." + mPreferences.getString("account", "");
		}

		Log.d("Account: InCustomer",""+mPreferences.getString("account", "").length());
		// Request.
		Log.i("SKScanner", "SignaKeyClient: InCustomer ="
				+ webMarkAuthenticateSymbols.inParams.InCustomer);

		logger.addLog(dateFormat.format(date) + ": " +"SKScanner: " + "WebMarkAuthenticateSymbols request, InConfirm: " + webMarkAuthenticateSymbols.inParams.InConfirm);
		logger.addLog("InCustomer: " + webMarkAuthenticateSymbols.inParams.InCustomer);
		logger.addLog("InData: " + webMarkAuthenticateSymbols.inParams.InData);
		logger.addLog("Token: " + webMarkAuthenticateSymbols.inParams.Token);
		logger.addLog("WantRelated: " + webMarkAuthenticateSymbols.inParams.WantRelated);
		logger.addLog("WantRelatedMedia: " + webMarkAuthenticateSymbols.inParams.WantRelatedMedia);
		logger.addLog("WantTargetMedia: " + webMarkAuthenticateSymbols.inParams.WantTargetMedia);


		ServicesBaseSoap serviceBaseSoap = new ServicesBaseSoap();
		try {
			result = serviceBaseSoap
					.WebMarkAuthenticateSymbols(webMarkAuthenticateSymbols);
			Log.i("SKScanner", "SignaKeyClient: result = " + result);

			//logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols result: " + result);
			mPending = false;
		} catch (Exception e) {

			result = null;
			logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols Exception: " + e.getStackTrace().toString());

			mPending = false;

			Log.i("SKScanner", "SignaKeyClient: StackTrace ??????? ");
		}

		return result;
	}
	public WebMarkDecodeSymbolsResult decodesymbol(byte[] data) {
		

	
		
		mPending = true;

		//byte[] bytes = new byte[encodedata.length() * sizeof(char.class)];
		WebMarkDecodeSymbolsResult resultdecode = null;
		WebMarkDecodeSymbols webmarkdecodesymbol = new WebMarkDecodeSymbols();
		webmarkdecodesymbol.inParams = new WebMarkDecodeParams();

		webmarkdecodesymbol.inParams.token = SignaKey.Token+SessionData.getInstance().getGpsCoordinates();
		webmarkdecodesymbol.inParams.keyStyle = 1;
		webmarkdecodesymbol.inParams.inData = Base64.encode(data);
		
		//webmarkdecodesymbol.inParams.InData = bytes.toString(); 
		webmarkdecodesymbol.inParams.wantBinary = true;
 
		ServicesBaseSoap serviceBaseSoap = new ServicesBaseSoap(); 
		
		try { 
			// resultdecode =
			// serviceBaseSoap.WebMarkDecodeSymbols(webmarkdecodesymbol);
			resultdecode = serviceBaseSoap
					.WebMarkDecodeSymbols(webmarkdecodesymbol);
			Log.i("SKScanner", "SignaKeyClient: result = " + resultdecode);
			mPending = false;
		} catch (Exception e) {
			
			resultdecode = null;

			mPending = false;
			e.printStackTrace();

			Log.i("SKScanner", "SignaKeyClient: StackTrace ??????? ");
		}

		return resultdecode;
	} 
 
	public WebGenDbResult genDb()

	{
		
		mPending = true;
		WebGenDbResult resultgen = null;
		WebGenDbRequest webGenDbRequest = new WebGenDbRequest();
		webGenDbRequest.inParams = new WebGenDbRequestParams();
		webGenDbRequest.inParams.Token = SignaKey.Token+SessionData.getInstance().getGpsCoordinates();
	Log.i("token", "" +webGenDbRequest.inParams.Token);
	

		webGenDbRequest.inParams.Request = new DbRequest();
		Log.i("token", "" +webGenDbRequest.inParams.Token);
		
		webGenDbRequest.inParams.Request.Operation = 20;
		
		
//
		webGenDbRequest.inParams.Request.Pattern = new DbPattern();
	
		webGenDbRequest.inParams.Request.Pattern.Keyword = "GMPP";
	
		webGenDbRequest.inParams.Request.Pattern.ReadSkip = -1;

		webGenDbRequest.inParams.Request.Pattern.ReadTake = -1;
//
		
//		
	
//		
//
		webGenDbRequest.inParams.Request.Pattern.Widgets = new Widgets();
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern = new WidgetPattern();
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Keyword = "Engine";
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.ReadResult = 3;
		
		

		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.ReadSkip = -1;
		
	webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.ReadTake = -1;
		
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details = new Details();
		//webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details = new DbDetailspattern();
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern = new DetailPattern();
		
		
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.Keyword = "";
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ReadResult = -1;
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ReadRule = 10;
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ReadSkip = -1;
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ReadTake = -1;
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.WildDatatype = 11;
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter = new ValuePattern();
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Boolean = null;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.MediaFile = new MediaFileMatch();
		//webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.MediaFile = "";
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Number = new NumberMatch();
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Number.ValueBase = null;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Number.ValueMaximum = null;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Timestamp = new TimestampMatch();
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Timestamp.Style = -1;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Timestamp.ValueBase = null;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Timestamp.ValueMaximum = null;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Widget = new WidgetMatch();
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.Widget.WidgetIndex = -1;
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey = new SignaKeyMatch();
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Style = 0;
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.String = new StringMatch();
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.String.Style = 0;
	    webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.String.Pattern = "";
		
		
		
	    webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern = new KeySelectorSerial();
		
	    webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.AccountIdentifier = "";
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.AccountWild = false;
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.ClientIdentifier = "";
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.ClientNumber = SessionData.getInstance().getClient();
		Log.d("clent number", "" +SessionData.getInstance().getClient());
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.ClientWild = false;
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.CustomerIdentifier = "";
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.CustomerWild = false;
		
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.ItemIdentifier = "";
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.ItemNumber = SessionData.getInstance().getItem();
		Log.d("Item number", "" +SessionData.getInstance().getItem());
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.ItemWild = false;
	
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.SequenceNumber = SessionData.getInstance().getSequence();
		Log.d("sequence number", "" +SessionData.getInstance().getSequence());
		webGenDbRequest.inParams.Request.Pattern.Widgets.WidgetPattern.Details.DetailPattern.ValueFilter.SignaKey.Pattern.VersionNumber = SessionData.getInstance().getVersion();
		Log.d("version number", "" +SessionData.getInstance().getVersion());
//
		Log.d("SKScanneroutputttt",""+ webGenDbRequest);
		ServicesBaseSoap serviceBaseSoap = new ServicesBaseSoap();
		
		try {
			resultgen = serviceBaseSoap.WebGenDbRequest(webGenDbRequest);
			//Log.i("SKScanner", "WebGenDbRequestinputparams: result = " + webGenDbRequest);
			Log.i("SKScanner", "WebGenDbRequestResult: result = " + resultgen);
			mPending = false;
		} catch (Exception e) {

			resultgen = null;
 
			mPending = false;
			e.printStackTrace();
			Log.i("SKScanner", "WebGenDbRequestResult: StackTrace ??????? ");
		}

		return resultgen;

	} 

	public boolean isPending() {
		return mPending;
	}

	@SuppressWarnings("unused")
	private void _getToken() {
		String Token = "Token.txt";
		FileInputStream fis = null;
		try {
			fis = openFileInput(Token);
			Log.i("SKScanner", "SKScanner: File Open Result fis= " + fis);
		} catch (FileNotFoundException e) {
			Log.i("SKScanner", "SKScanner: FileNotFoundException e " + e);
			e.printStackTrace();
		}
		TOKEN = fis.toString();
	}

}
