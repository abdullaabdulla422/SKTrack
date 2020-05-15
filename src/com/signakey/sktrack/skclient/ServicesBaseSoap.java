package com.signakey.sktrack.skclient;

import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.util.Log;


@SuppressWarnings({ "deprecation", "unused" })
public final class ServicesBaseSoap {
    public String mWebValidateUserUrl ="https://www.signakey.com/Machine/ServicesBase.asmx"; //"http://75.67.233.32/SignaKey/Machine/ServicesBase.asmx";   // Bob 
    public String mWebMarkAuthenticateSymbolsUrl = "https://www.signakey.com/Machine/ServicesDemo.asmx"; //"http://75.67.233.32/SignaKey/Machine/ServicesDemo.asmx";  //
    public String mWebGenDbRequestUrl = "https://www.SignaKey.com/Machine/ServicesGenDb.asmx";
    public String mWebMarkDecodeSymbolsUrl = "https://www.signakey.com/Machine/ServicesDecode.asmx";
    //    public static Vector media;
    

    public WebValidateUserResult WebValidateUser(WebValidateUser params) throws Exception {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(params.GetSoapParams());
        envelope.setAddAdornments(false);

        new WebValidateUserParams().register(envelope);
        new WebValidateUserResponse().register(envelope);

        AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(mWebValidateUserUrl);
        androidHttpTransport.call(params.GetSoapAction(), envelope);

        @SuppressWarnings("rawtypes")
		Vector response = (Vector)(envelope.getResponse());
        Log.i("SKScanner", "ServiceSoapBase: response.get(0) = " + ( response.get(0)));
        return (WebValidateUserResult)(response.get(0));
    }

    //Get Logo after success on Login
    public WebValidateLogoResult WebValidateLogo(WebValidateLogo params) throws Exception {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(params.GetSoapParams());
        envelope.setAddAdornments(false);

        new WebValidateLogoParams().register(envelope);
        new WebValidateLogoResponse().register(envelope);

        AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(mWebValidateUserUrl);
        androidHttpTransport.call(params.GetSoapAction(), envelope);

        @SuppressWarnings("rawtypes")
		Vector response = (Vector)(envelope.getResponse());
        Log.i("SKScanner", "ServiceSoapBase: response.get(0) = " + ( response.get(0)));
        return (WebValidateLogoResult)(response.get(0));
    }
    
    public WebMarkAuthenticateResult WebMarkAuthenticateSymbols(WebMarkAuthenticateSymbols params) throws Exception {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(params.GetSoapParams());
        envelope.setAddAdornments(false);
        

        new WebMarkAuthenticateParams().register(envelope);
        new WebMarkAuthenticateSymbolsResponse().register(envelope);

        AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(mWebMarkAuthenticateSymbolsUrl);
        androidHttpTransport.debug = true;
        try {
            androidHttpTransport.call(params.GetSoapAction(), envelope);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

       @SuppressWarnings("rawtypes")
	Vector response = (Vector)(envelope.getResponse());
     
        Log.i("SKScanner", "ServiceSoapBase: response.get(0) = " + ( response.get(0)));
        return (WebMarkAuthenticateResult)( response.get(0));

    }
    public WebGenDbResult WebGenDbRequest(WebGenDbRequest params) throws Exception {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        
        envelope.dotNet = true;
        envelope.setOutputSoapObject(params.GetSoapParams());
        envelope.setAddAdornments(false);
       
       
        

       new WebGenDbRequestParams().register(envelope);
        new WebGenDbRequestResponse().register(envelope);

        AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(mWebGenDbRequestUrl);
        androidHttpTransport.debug = true;
        try {
            androidHttpTransport.call(params.GetSoapAction(), envelope);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

       @SuppressWarnings("rawtypes")
	Vector response = (Vector)(envelope.getResponse());
     
        Log.i("SKScanner", "ServiceSoapBase: response.get(0) = " + ( response.get(0)));
        return (WebGenDbResult)( response.get(0));

    }
    public WebMarkDecodeSymbolsResult WebMarkDecodeSymbols (WebMarkDecodeSymbols params) throws Exception
    {
    	 SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
         envelope.dotNet = true;
         envelope.setOutputSoapObject(params.GetSoapParams());
         envelope.setAddAdornments(false);
         
         new WebMarkDecodeParams().register(envelope);
         new WebMarkDecodeSymbolsResponse().register(envelope);
         AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(mWebMarkDecodeSymbolsUrl);
         androidHttpTransport.debug = true;
         try {
             androidHttpTransport.call(params.GetSoapAction(), envelope);
         }
         catch (Exception e) {
             e.printStackTrace();
             throw e;
         }
         @SuppressWarnings("rawtypes")
     	Vector response = (Vector)(envelope.getResponse());
          
             Log.i("SKScanner", "ServiceSoapBase: response.get(0) = " + ( response.get(0)));
             return (WebMarkDecodeSymbolsResult)( response.get(0));
         
		
    	
    }
    
    
    
}
