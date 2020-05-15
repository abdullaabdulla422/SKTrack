package com.signakey.sktrack.skclient;

import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class TimestampMatch extends BaseObject {

     public int Style;
     public Date ValueBase;
     public Date ValueMaximum;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Style; 
           case 1: 
                return ValueBase; 
           case 2: 
                return ValueMaximum; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 3;
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
                info.name = "ValueBase"; 
                info.type = Date.class; 
                             break; 
           case 2: 
                info.name = "ValueMaximum"; 
                info.type = Date.class; 
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
                ValueBase = (Date)value; 
                  break; 
           case 2: 
                ValueMaximum = (Date)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "TimestampMatch", this.getClass());
        
    }

}
