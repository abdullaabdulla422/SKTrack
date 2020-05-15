package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;


public class WebMarkAuthenticateSymbolsResponse extends BaseObject {
    public WebMarkAuthenticateResult WebMarkAuthenticateResult;
    public WebMarkAuthenticateInParams inParams;

    public Object getProperty(int index) {
    	
        switch (index) {
           case 0:
                return WebMarkAuthenticateResult;
           case 1:
                return inParams;
        }
        return null;
    }

    public int getPropertyCount() {
        return 2;
    }

    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "WebDemoLookupSymbolsContainedResult";  //  WebMarkAuthenticateResult
                info.type = new WebMarkAuthenticateResult().getClass();
                break;
           case 1:
                info.name = "inParams";
                info.type = new WebMarkAuthenticateInParams().getClass();
                break;
           default:
               break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                WebMarkAuthenticateResult = (WebMarkAuthenticateResult) value;
                Log.i("SKScanner", "WebMarkAuthenticateSymbolsResult = " + WebMarkAuthenticateResult  );
                break;
            case 1:
                inParams = (WebMarkAuthenticateInParams) value;
                break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebDemoLookupSymbolsContainedResponse", this.getClass()); // WebMarkAuthenticateSymbolsResponse
        new WebMarkAuthenticateResult().register(envelope);
        new WebMarkAuthenticateInParams().register(envelope);
    }
}
