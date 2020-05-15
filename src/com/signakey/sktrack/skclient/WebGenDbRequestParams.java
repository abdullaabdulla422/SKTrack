package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebGenDbRequestParams extends BaseObject {

     public String Token;
     public DbRequest Request;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Token; 
           case 1: 
                return Request; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
    	//info.type = PropertyInfo.STRING_CLASS;
    	switch (index) {

           case 0: 
                info.name = "Token"; 
                info.type = String.class; 
                             break; 
           case 1: 
                info.name = "Request"; 
                info.type = DbRequest.class; 
                             break; 

        default:
            break;
        }
    }

    public void setProperty(int index, Object value)
    {
    	switch (index)
    	{
           case 0: 
                Token = (String)value; 
                  break; 
           case 1: 
                Request = (DbRequest)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "WebGenDbRequestParams", this.getClass());
                   new DbRequest().register(envelope); 

    }

}
