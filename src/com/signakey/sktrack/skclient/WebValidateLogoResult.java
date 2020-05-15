package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

public class WebValidateLogoResult extends BaseObject {
    public WebResult Common;
    public String CompanyShortName;
    public String CompanyLongName;
    public String AccountShortName;
    public String AccountLongName;
    public String UserName;
    public String LogoURL;



    public Object getProperty(int index) {
        switch (index) {
            case 0: 
                return Common; 
            case 1: 
                return CompanyShortName; 
            case 2: 
                return CompanyLongName;  
            case 3: 
                return AccountShortName; 
            case 4: 
                return AccountLongName; 
            case 5: 
                return UserName; 
            case 6: 
                return LogoURL; 

        }

        return null;
    }

    public int getPropertyCount() {
        return 7;
    }

    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Common";
                info.type = WebResult.class;
                break;
            case 1:
                info.name = "CompanyShortName";
                info.type = String.class;
                break;
            case 2:
                info.name = "CompanyLongName";
                info.type = String.class;
                break;
            case 3:
                info.name = "AccountShortName";
                info.type = String.class;
                break;
            case 4:
                info.name = "AccountLongName";
                info.type = String.class;
                break;
            case 5:
                info.name = "UserName";
                info.type = String.class;
                break;
            case 6:
                info.name = "LogoURL";
                info.type = String.class;
                break;
            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                Common = (WebResult)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty Common = " + Common);
                break;
            case 1:
                CompanyShortName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty CompanyShortName = " + CompanyShortName);
                break;
            case 2:
                CompanyLongName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty CompanyLongName = " + CompanyLongName);
                break;
            case 3:
                AccountShortName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty AccountShortName = " + AccountShortName);
                break;
            case 4:
                AccountLongName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty AccountLongName = " + AccountLongName);
                break;
            case 5:
                UserName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty UserName = " + UserName);
                break;
            case 6:
                LogoURL = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty UserFullName = " + LogoURL);
                break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebGetCustomerResult", this.getClass()); // Web result structure
        new WebResult().register(envelope); 
    }

}
