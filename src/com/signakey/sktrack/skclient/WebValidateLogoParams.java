package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebValidateLogoParams extends BaseObject {
//    public int ValidateStyle;
    public String Token;
    public String Company;
    public String Account;
    public String UserName;
//    public String Password;

    public Object getProperty(int index) {
        switch (index) {
            case 0: 
                return Token; 
            case 1: 
                return Company; 
            case 2: 
                return Account; 
            case 3: 
                return UserName; 

        }

        return null;
    }

    public int getPropertyCount() {
        return 4;
    }

    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
        switch (index) {

           case 0:
                info.name = "Token";
                info.type = String.class;
                break;
           case 1:
                info.name = "Company";
                info.type = String.class;
                break;
           case 2:
                info.name = "Account";
                info.type = String.class;
                break;
           case 3:
                info.name = "UserName";
                info.type = String.class;
                break;

          default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
        	case 0:
        		Token = (String)value;

            case 1:
                Company = (String)value;
                break;
            case 2:
                Account = (String)value;
                break;
            case 3:
                UserName = (String)value;
                break;

        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebValidateLogoParams", this.getClass());
    }

}
