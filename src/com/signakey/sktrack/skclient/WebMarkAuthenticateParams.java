package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.Hashtable;

public class WebMarkAuthenticateParams extends BaseObject {
    public String Token;
    public String InCustomer = "";
    public String InData;
    public String InConfirm;
    public Boolean WantRelated = true ;
    public Boolean WantRelatedMedia = false;
    public Boolean WantTargetMedia = true;
    
    
    public Object getProperty(int index) {
        switch (index) {
           case 0: 
                return Token; 
           case 1: 
        	   return InCustomer;
           case 2:
                return InData;
           case 3:
        	   	return InConfirm;
           case 4:
        	   return WantRelated;
           case 5:
        	   return WantRelatedMedia;
           case 6:
        	   return WantTargetMedia;
        }

        return null;
    }

    public int getPropertyCount() {
        return 7;
    }

    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0: 
                info.name = "Token"; 
                info.type = String.class; 
                break; 
            case 1: 
            	info.name = "InCustomer";
            	info.type = String.class;
            	break;
            case 2:
                info.name = "InData"; 
                info.type = String.class; 
                break;
            case 3:
            	info.name = "InConfirm";
            	info.type = String.class;
            	break;
            case 4:
         	   info.name= "WantRelated";
         	   info.type = Boolean.class;
         	   break;
            case 5:
         	   info.name =  "WantRelatedMedia";
         	   info.type = Boolean.class;
         	   break;
            case 6:
         	   info.name = "WantTargetMedia";
         	   info.type = Boolean.class;
         	   break;
            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0: 
                Token = (String)value; 
                break; 
            case 1: 
            	InCustomer = (String) value;
            	break;
            case 2:
                InData = (String)value; 
                break;
            case 3:
            	InConfirm = (String) value;
            	break;
            case 4:
          	   WantRelated = (Boolean)value;
          	   break;
             case 5:
          	   WantRelatedMedia = (Boolean)value;
          	   break;
             case 6:
          	   WantTargetMedia = (Boolean)value;
          	   break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebDemoLookupContainedParams", this.getClass());
    }
}
