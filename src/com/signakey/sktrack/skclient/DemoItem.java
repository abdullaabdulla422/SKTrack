package com.signakey.sktrack.skclient;

import android.util.Log;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;
import java.util.Hashtable;

public class DemoItem extends BaseObject implements KvmSerializable  {
	public String KeyText;
	public Boolean IsContainer;
	public Boolean IsContained;
    public String PublicText;
    public String ConsumerText;
    public String RestrictedText;
    public String PrivateText;
    public ArrayList<MediaFile> PublicFileList;
    public ArrayList<MediaFile> ConsumerFileList;
    public ArrayList<MediaFile> PrivateFileList;
    public ArrayList<MediaFile> RestrictedFileList;
    public Integer DecodeCount;
    public String DecodeOldest;
    public String DecodeNewest;
    Logger logger;
    
	@Override
	public Object getProperty(int index) {
        switch (index) {
        case 0:
        	return KeyText;
        case 1:
        	return IsContainer;
        case 2:
        	return IsContained;
        case 3:
        	return PublicText;
        case 4:
        	return ConsumerText;
        case 5:
        	return RestrictedText;
        case 6:
        	return PrivateText;
        case 7:
        	return PublicFileList;
        case 8:
        	return ConsumerFileList;
        case 9:
        	return PrivateFileList;
        case 10:
        	return RestrictedFileList;
        case 11:
        	return DecodeCount;
        case 12:
        	return DecodeOldest;
        case 13:
        	return DecodeNewest;
        }
        return null;
	}
	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 14;
	}
	@Override
	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
		// TODO Auto-generated method stub
        switch (index) {
        case 0:
            info.name = "KeyText";
            info.type = String.class;
            break;
        case 1:
        	info.name = "IsContainer";
        	info.type = Boolean.class;
        	break;
        case 2:
        	info.name = "IsContained";
        	info.type = Boolean.class;
        	break;
        case 3:
        	info.name = "PublicText";
        	info.type = String.class;
        	break;
        case 4:
        	info.name = "ConsumerText";
        	info.type = String.class;
        	break;
        case 5:
        	info.name = "RestrictedText";
        	info.type = String.class;
        	break;
        case 6:
        	info.name = "PrivateText";
        	info.type = String.class;
        	break;
        case 7:
        	info.name = "PublicFileList";
        	info.type = SoapObject.class;
        	break;
        case 8:
        	info.name = "ConsumerFileList";
        	info.type = SoapObject.class;
        	break;
        case 9:
        	info.name = "RestrictedFileList";
        	info.type = SoapObject.class;
        	break;
        case 10:
        	info.name = "PrivateFileList";
        	info.type = SoapObject.class;
        	break;
        case 11:
        	info.name = "DecodeCount";
        	info.type = Integer.class;
        	break;
        case 12:
        	info.name = "DecodeOldest";
        	info.type = String.class;
        	break;
        case 13:
        	info.name = "DecodeNewest";
        	info.type = String.class;
        	break;
        	default:
        	break;
        }
        
        	
        	
        }
	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(int index, Object value) {
            switch (index) {
            case 0:
                KeyText = (String) value;;
                Log.i("SKScanner", "ItemList: KeyText = " + KeyText);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce,ItemList:  KeyText: " + KeyText);

                break;
            case 1:
                IsContainer = (Boolean) value;;
                Log.i("SKScanner", "ItemList: IsContainer = " + IsContainer);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: IsContainer: " + IsContainer);
                break;
            case 2:
                IsContained = (Boolean) value;;
                Log.i("SKScanner", "ItemList: IsContained = " + IsContained);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce,ItemList:  IsContained: " + IsContained);
                break;
            case 3:
                PublicText = (String) value;
                Log.i("SKScanner", "ItemList: PublicText = " + PublicText);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: PublicText: " + PublicText);
                break;
            case 4:
                ConsumerText = (String) value;;
                Log.i("SKScanner", "ItemList: ConsumerText = " + ConsumerText);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: ConsumerText: " + ConsumerText);
                break;
            case 5:
                RestrictedText = (String) value;;
                Log.i("SKScanner", "ItemList: RestrictedText = " + RestrictedText);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: RestrictedText: " + RestrictedText);
                break;
            case 6:
                PrivateText = (String) value;;
                Log.i("SKScanner", "ItemList: PrivateText = " + PrivateText);
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: PrivateText: " + PrivateText);
                break;
            case 7:
                PublicFileList = (ArrayList<MediaFile>) value;
                Log.i("SKScanner", "ItemList: PublicFileList = ");
                break;
            case 8:
            	ConsumerFileList = (ArrayList<MediaFile>) value; //Integer.parseInt(value.toString());
            	Log.i("SKScanner", "ItemList: ConsumerFileList = ");
            	break;
            case 9:
            	RestrictedFileList = (ArrayList<MediaFile>) value; //Integer.parseInt(value.toString());
            	Log.i("SKScanner", "ItemList: RestrictedFileList = ");
            	break;
            case 10:
            	PrivateFileList = (ArrayList<MediaFile>) value; //Integer.parseInt(value.toString());
            	Log.i("SKScanner", "ItemList: PrivateFileList = ");;
            	break;
            case 11:
            	DecodeCount = Integer.parseInt(value.toString());
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: DecodeCount: " + DecodeCount);
            	break;
            case 12:
            	DecodeOldest = (String) value;
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: DecodeOldest: " + DecodeOldest);

                break;
            case 13:
            	DecodeNewest = (String) value;
                logger.addLog("SKScanner: " + "WebMarkAuthenticateSymbols responce, ItemList: DecodeNewest: " + DecodeNewest);

                break;
            }
            }
            
	public void register(SoapSerializationEnvelope envelope) {
        		// TODO Auto-generated method stub
        		envelope.addMapping(NAMESPACE, "ItemList", this.getClass());
        	}
}
