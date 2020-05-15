package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class StringMatch extends BaseObject {

     public int Style;
     public String Pattern;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Style; 
           case 1: 
                return Pattern; 

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
                info.name = "Style"; 
                info.type = Integer.TYPE; 
                             break; 
           case 1: 
                info.name = "Pattern"; 
                info.type = String.class; 
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
                Style = Integer.parseInt(value.toString()); 
                  break; 
           case 1: 
                Pattern = (String)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "StringMatch", this.getClass());
        
    } 

}
