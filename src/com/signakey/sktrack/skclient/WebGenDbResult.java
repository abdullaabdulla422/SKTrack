package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebGenDbResult extends BaseObject {

     public WebResult Common;
     public DbResponse Response;


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Common; 
           case 1: 
                return Response; 

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
                info.name = "Common"; 
                info.type = WebResult.class;
                             break; 
           case 1: 
                info.name = "Response"; 
                info.type = DbResponse.class; 
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
                Common = (WebResult)value; 
                  break; 
           case 1: 
                Response = (DbResponse)value; 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "WebGenDbRequestResult", this.getClass());
                   new WebResult().register(envelope); 
           new DbResponse().register(envelope); 

    }

}
