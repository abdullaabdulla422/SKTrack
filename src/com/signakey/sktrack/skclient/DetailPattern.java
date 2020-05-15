package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DetailPattern extends BaseObject {

     public String Keyword;
     public int WildDatatype;
     public int ReadResult;
     public int ReadRule;
     public int ReadSkip;
     public int ReadTake;
     public ValuePattern ValueFilter;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Keyword; 
           case 1: 
                return WildDatatype; 
           case 2: 
                return ReadResult; 
           case 3: 
                return ReadRule; 
           case 4: 
                return ReadSkip; 
           case 5: 
                return ReadTake; 
           case 6: 
                return ValueFilter; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 7;
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
                info.name = "WildDatatype"; 
                info.type = Integer.TYPE; 
                             break; 
           case 2: 
                info.name = "ReadResult"; 
                info.type = Integer.TYPE; 
                             break; 
           case 3: 
                info.name = "ReadRule"; 
                info.type = Integer.TYPE; 
                             break; 
           case 4: 
                info.name = "ReadSkip"; 
                info.type =  Integer.TYPE; 
                             break; 
           case 5: 
                info.name = "ReadTake"; 
                info.type = Integer.TYPE;  
                             break; 
           case 6: 
                info.name = "ValueFilter"; 
                info.type = ValuePattern.class; 
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
                WildDatatype = Integer.parseInt(value.toString()); 
                  break; 
           case 2: 
                ReadResult = Integer.parseInt(value.toString()); 
                  break; 
           case 3: 
                ReadRule = Integer.parseInt(value.toString()); 
                  break; 
           case 4: 
                ReadSkip =   Integer.parseInt(value.toString());
                  break; 
           case 5: 
                ReadTake =  Integer.parseInt(value.toString());
                  break; 
           case 6: 
                ValueFilter = (ValuePattern)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DetailPattern", this.getClass());
                   new ValuePattern().register(envelope); 

    }

}
