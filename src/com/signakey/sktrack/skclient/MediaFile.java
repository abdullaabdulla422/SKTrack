package com.signakey.sktrack.skclient;

import android.util.Log;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.Hashtable;

public class MediaFile extends BaseObject implements KvmSerializable { //BaseObject

    public int Visibility;
    public int Medium;
    public int Order;
    public String Identifier;
    public String URL;
    Logger logger;

	

	@Override
	public Object getProperty(int index) {
        switch (index) {
        case 0: 
        	Log.i("SKScanner", "MediaFile: " + Visibility);
            return Visibility; 
        case 1: 
        	Log.i("SKScanner", "MediaFile: " + Medium);
            return Medium; 
        case 2: 
        	Log.i("SKScanner", "MediaFile: " + Order);
            return Order; 
        case 3: 
        	Log.i("SKScanner", "MediaFile: " + Identifier);
            return Identifier; 
        case 4: 
        	Log.i("SKScanner", "MediaFile: " + URL);
            return URL;
    }
    
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
		// TODO Auto-generated method stub
        switch (index) {
        case 0:
            info.name = "Visibility";
            info.type = Integer.TYPE;
            break;
        case 1:
            info.name = "Medium";
            info.type = Integer.TYPE;
            break;
        case 2:
            info.name = "Order";
            info.type = Integer.TYPE;
            break;
        case 3:
            info.name = "Identifier";
            info.type = String.class;
            break;
        case 4:
            info.name = "URL";
            info.type = String.class;
            break;
        default:
            break;
    }
	}

	@Override
	public void setProperty(int index, Object value) {
        switch (index) {
        case 0:
            Visibility = Integer.parseInt(value.toString());
            Log.i("SKScanner", "MediaFile: Visibility = " + Visibility);
            logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, MediaFile: Visibility: " + Visibility);
            break;
        case 1:
            Medium = Integer.parseInt(value.toString());
            Log.i("SKScanner", "MediaFile: Medium = " + Medium);
            logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, MediaFile: Medium: " + Medium);
            break;
        case 2:
            Order = Integer.parseInt(value.toString());
            Log.i("SKScanner", "MediaFile: Order = " + Order);
            logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, MediaFile: Order: " + Order);
            break;
        case 3:
            Identifier = (String) value;
            Log.i("SKScanner", "MediaFile: Identifier = " + Identifier);
            logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, MediaFile: Identifier: " + Identifier);
            break;
        case 4:
            URL = (String) value;
            Log.i("SKScanner", "MediaFile: Identifier = " + URL);
            logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, MediaFile: URL: " + URL);

            break;
		
	}
	}
//    public void register(SoapSerializationEnvelope envelope) {
//        envelope.addMapping(NAMESPACE, "MediaFile", this.getClass());
//    }

	public void register(SoapSerializationEnvelope envelope) {
		// TODO Auto-generated method stub
		envelope.addMapping(NAMESPACE, "MediaFile", this.getClass());
	}

}
