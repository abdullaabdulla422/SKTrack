package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

public class WebValidateLogoResponse extends BaseObject{
    public WebValidateLogoResult WebValidateLogoResult;
    public WebValidateLogoInParams inParams;

    public Object getProperty(int index) {
        switch (index) {
           case 0:
                return WebValidateLogoResult;
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
                info.name = "WebValidateLogoResult";
                info.type = new WebValidateUserResult().getClass();
                break;
           case 1:
                info.name = "inParams";
                info.type = new WebValidateUserInParams().getClass();
                break;
           default:
               break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                WebValidateLogoResult = (WebValidateLogoResult) value;
                Log.i("SKScanner", "WebValidateUserResponse setProperty WebValidateUserResult=" + WebValidateLogoResult);
                break;
            case 1:
                inParams = (WebValidateLogoInParams) value;
                break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebValidateLogoResponse", this.getClass());
        new WebValidateUserResult().register(envelope);
        new WebValidateUserInParams().register(envelope);
    }

}
