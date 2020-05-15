package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WidgetPattern  extends BaseObject {

     public String Keyword;
     public int ReadResult;
     public int ReadSkip;
     public int ReadTake;
     public Details Details;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Keyword; 
           case 1: 
                return ReadResult; 
           case 2: 
                return ReadSkip; 
           case 3: 
                return ReadTake; 
           case 4: 
                return Details; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 5;
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
                info.name = "ReadResult"; 
                info.type = Integer.TYPE; 
                             break; 
           case 2: 
                info.name = "ReadSkip"; 
                info.type =Integer.TYPE;
                             break; 
           case 3: 
                info.name = "ReadTake"; 
                info.type =Integer.TYPE;
                             break; 
           case 4: 
                info.name = "Details"; 
                info.type = Details.class; 
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
                ReadResult = Integer.parseInt(value.toString()); 
                  break; 
           case 2: 
                ReadSkip = Integer.parseInt(value.toString()); 
                  break; 
           case 3: 
                ReadTake = Integer.parseInt(value.toString()); 
                  break; 
           case 4: 
                Details = (Details)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "WidgetPattern", this.getClass());
                   new Details().register(envelope); 

    }

}
