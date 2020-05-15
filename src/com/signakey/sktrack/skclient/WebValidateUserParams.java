package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebValidateUserParams extends BaseObject {
    public int ValidateStyle;
    public int TokenMinutes;
    public String Company;
    public String Account;
    public String UserName;
    public String Password;

    public Object getProperty(int index) {
        switch (index) {
            case 0: 
                return ValidateStyle; 
            case 1: 
                return TokenMinutes; 
            case 2: 
                return Company; 
            case 3: 
                return Account; 
            case 4: 
                return UserName; 
            case 5: 
                return Password;  
        }

        return null;
    }

    public int getPropertyCount() {
        return 6;
    }

    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
        switch (index) {
           case 0:
                info.name = "ValidateStyle";
                info.type = Integer.TYPE;
                break;
           case 1:
                info.name = "TokenMinutes";
                info.type = Integer.TYPE;
                break;
           case 2:
                info.name = "Company";
                info.type = String.class;
                break;
           case 3:
                info.name = "Account";
                info.type = String.class;
                break;
           case 4:
                info.name = "UserName";
                info.type = String.class;
                break;
           case 5:
                info.name = "Password";
                info.type = String.class;
                break;
          default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                ValidateStyle = Integer.parseInt(value.toString());
                break;
            case 1:
                TokenMinutes = Integer.parseInt(value.toString());
                break;
            case 2:
                Company = (String)value;
                break;
            case 3:
                Account = (String)value;
                break;
            case 4:
                UserName = (String)value;
                break;
            case 5:
                Password = (String)value;
                break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebValidateUserParams", this.getClass());
    }
}
