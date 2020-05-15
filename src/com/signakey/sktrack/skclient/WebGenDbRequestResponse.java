package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;


public class WebGenDbRequestResponse extends BaseObject {

    public WebGenDbResult WebGenDbRequestResult;
    public WebGenDbRequestInParams WebGenDbRequestParams;


    public Object getProperty(int index)
    {
    	switch(index)
    	{
    	case 0:
    	return WebGenDbRequestResult;
    	case 1:
    		 return WebGenDbRequestParams;
    	}
		return null;

    	

    }

    public int getPropertyCount()
    {
        return 2;
    }

    @SuppressWarnings("unchecked")
	public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info)
    {
    	switch(index)
    	{
    	case 0:
    		info.name = "WebGenDbRequestResult";
            info.type = new WebGenDbResult().getClass();
            break;
    	case 1:
    		info.name = "inParams";
            info.type = new WebGenDbRequestInParams().getClass();
            break;
            
    	}

    	

    }

    public void setProperty(int index, Object value)
    {
    	switch(index)
    	{
    	case 0:
    		WebGenDbRequestResult = (WebGenDbResult) value;
    		break;
    	case 1:
    		WebGenDbRequestParams = (WebGenDbRequestInParams)value;
    		break;
    		
    	}

    	

    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "WebGenDbRequestResponse", this.getClass());
        new WebGenDbResult().register(envelope);
        new WebGenDbRequestInParams().register(envelope);
    }

}
