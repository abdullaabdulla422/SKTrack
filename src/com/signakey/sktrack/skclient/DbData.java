package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DbData extends BaseObject {

     public String Keyword;
     public Widgets Widgets;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Keyword; 
           case 1: 
                return Widgets; 

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
                info.name = "Keyword"; 
                info.type = String.class; 
                             break; 
           case 1: 
                info.name = "Widgets"; 
                info.type = Widgets.class; 
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
                Keyword = (String)value; 
                  break; 
           case 1: 
        	   Widgets = (Widgets)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DbData", this.getClass());
                   new Widgets().register(envelope); 

    }

}
