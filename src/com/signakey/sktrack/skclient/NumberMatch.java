package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class NumberMatch extends BaseObject {

     public Float ValueBase;
     public Float ValueMaximum;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return ValueBase; 
           case 1: 
                return ValueMaximum; 

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
                info.name = "ValueBase"; 
                info.type = Float.class;
                             break; 
           case 1: 
                info.name = "ValueMaximum"; 
                info.type =Float.class;
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
                ValueBase = (Float)value;
                  break; 
           case 1: 
                ValueMaximum =(Float)value;
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "NumberMatch", this.getClass());
        
    }

}
