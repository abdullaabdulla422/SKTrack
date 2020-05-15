package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DbPattern extends BaseObject {

	public int ReadSkip;
     public int ReadTake;
     public String Keyword;
     public Widgets Widgets;
    


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return ReadSkip; 
           case 1: 
                return ReadTake; 
           case 2: 
                return Keyword; 
        
           case 3: 
                return Widgets; 

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
                info.name = "ReadSkip"; 
                info.type = Integer.TYPE; 
                             break; 
           case 1: 
                info.name = "ReadTake"; 
                info.type =  Integer.TYPE;  
                             break; 
           case 2: 
                info.name = "Keyword"; 
                info.type = String.class; 
                             break; 
         
           case 3: 
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
                ReadSkip = Integer.parseInt(value.toString());
                  break; 
           case 1: 
                ReadTake =  Integer.parseInt(value.toString());
                  break; 
          
           case 2: 
                Keyword = (String)value; 
                  break; 
          
           case 3: 
        	   Widgets = (Widgets)value; 
                 break;

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DbPattern", this.getClass());
                   new Widgets().register(envelope);
                 //  new ArrayOfWidgetPattern().register(envelope);
                   
                   

    }

}
