package com.signakey.sktrack.skclient;

import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class WebValidateUserResult extends BaseObject {
    public WebResult Common;
    public boolean Wizarding;
    public int CompanyId;
    public String CompanyShortName;
    public String CompanyLongName;
    public int AccountId;
    public String AccountShortName;
    public String AccountLongName;
    public String UserName;
    public String UserFullName;
    public String Token;
    Logger logger;
    Date date = new Date() ;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;


    public Object getProperty(int index) {
        switch (index) {
            case 0: 
                return Common; 
            case 1: 
                return Wizarding; 
            case 2: 
                return CompanyId; 
            case 3: 
                return CompanyShortName;   
            case 4: 
                return CompanyLongName; 
            case 5: 
                return AccountId; 
            case 6: 
                return AccountShortName; 
            case 7: 
                return AccountLongName; 
            case 8: 
                return UserName; 
            case 9: 
                return UserFullName; 
            case 10: 
                return Token; 
        }

        return null;
    }

    public int getPropertyCount() {
        return 11;
    }

    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "Common";
                info.type = WebResult.class;
                break;
            case 1:
                info.name = "Wizarding";
                info.type = Boolean.TYPE;
                break;
            case 2:
                info.name = "CompanyId";
                info.type = Integer.TYPE;
                break;
            case 3:
                info.name = "CompanyShortName";
                info.type = String.class;
                break;
            case 4:
                info.name = "CompanyLongName";
                info.type = String.class;
                break;
            case 5:
                info.name = "AccountId";
                info.type = Integer.TYPE;
                break;
            case 6:
                info.name = "AccountShortName";
                info.type = String.class;
                break;
            case 7:
                info.name = "AccountLongName";
                info.type = String.class;
                break;
            case 8:
                info.name = "UserName";
                info.type = String.class;
                break;
            case 9:
                info.name = "UserFullName";
                info.type = String.class;
                break;
            case 10:
                info.name = "Token";
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
               // logger.addLog(dateFormat.format(date) + ": " +"SKScanner: " + "WebValidateUser response, Common: " + Common);
                break;
            case 1:
                Wizarding = Boolean.getBoolean(value.toString());
                Log.i("SKScanner", "WebValidateUserResult setProperty Wizarding = " + Wizarding);
                logger.addLog(dateFormat.format(date) + ": " +"SKScanner: " + "WebValidateUser Response, Wizarding: " + Wizarding);
                break;
            case 2:
                CompanyId = Integer.parseInt(value.toString());
                Log.i("SKScanner", "WebValidateUserResult setProperty CompanyId = " + CompanyId);
                logger.addLog( "CompanyId: " + CompanyId);
                break;
            case 3:
                CompanyShortName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty CompanyShortName = " + CompanyShortName);
                logger.addLog("CompanyShortName: " + CompanyShortName);
                break;
            case 4:
                CompanyLongName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty CompanyLongName = " + CompanyLongName);
                logger.addLog("CompanyLongName: " + CompanyLongName);
                break;
            case 5:
                AccountId = Integer.parseInt(value.toString());
                Log.i("SKScanner", "WebValidateUserResult setProperty AccountId = " + AccountId);
                logger.addLog("AccountId: " + AccountId);
                break;
            case 6:
                AccountShortName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty AccountShortName = " + AccountShortName);
                logger.addLog("AccountShortName: " + AccountShortName);
                break;
            case 7:
                AccountLongName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty AccountLongName = " + AccountLongName);
                logger.addLog("AccountLongName: " + AccountLongName);
                break;
            case 8:
                UserName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty UserName = " + UserName);
                logger.addLog("UserName: " + UserName);
                break;
            case 9:
                UserFullName = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty UserFullName = " + UserFullName);
                logger.addLog("UserFullName: " + UserFullName);
                break;
            case 10:
                Token = (String)value;
                Log.i("SKScanner", "WebValidateUserResult setProperty Token = " + Token);
                logger.addLog("Token: " + Token);
                break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebDemoLookupShortResult", this.getClass()); // Web result structure
        new WebResult().register(envelope); 
    }
}
