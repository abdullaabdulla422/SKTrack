package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WidgetMatch extends BaseObject {

     public int WidgetIndex;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return WidgetIndex; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 1;
    }

    @SuppressWarnings("unchecked")
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
    	//info.type = PropertyInfo.STRING_CLASS;
    	switch (index) {

           case 0: 
                info.name = "WidgetIndex"; 
                info.type = Integer.TYPE; 
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
                WidgetIndex = Integer.parseInt(value.toString()); 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "WidgetMatch", this.getClass());
        
    }

}
