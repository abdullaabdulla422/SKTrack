package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DbResponse extends BaseObject {

     public int ResultCode;
     public String ErrorText;
     public DbData Data;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return ResultCode; 
           case 1: 
                return ErrorText; 
           case 2: 
                return Data; 

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
                info.name = "ResultCode"; 
                info.type = Integer.TYPE; 
                             break; 
           case 1: 
                info.name = "ErrorText"; 
                info.type = String.class;
                             break; 
           case 2: 
                info.name = "Data"; 
                info.type = DbData.class;
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
                ResultCode = Integer.parseInt(value.toString()); 
                  break; 
           case 1: 
                ErrorText = (String)value; 
                  break; 
           case 2: 
                Data = (DbData)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DbResponse", this.getClass());
                   new DbData().register(envelope); 

    }

}
