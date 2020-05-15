package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class ConstraintTimestamp extends BaseObject {

     public String Format;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Format; 

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
                info.name = "Format"; 
                info.type = Format.getClass(); 
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
                Format = (String)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "ConstraintTimestamp", this.getClass());
        
    }

}
