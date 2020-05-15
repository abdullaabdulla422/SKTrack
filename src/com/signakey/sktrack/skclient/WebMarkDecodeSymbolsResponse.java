package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebMarkDecodeSymbolsResponse extends BaseObject{
   public WebMarkDecodeSymbolsResult webMarkDecodeResult;
   public WebMarkDecodeInParams inParams;
	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			 return webMarkDecodeResult;
			 
		case 1:
			return inParams;
		
		}
		return null;
	}

	@Override
	public int getPropertyCount() {  
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
switch(index)
		
		{
	     case 0: 
             info.name = "WebMarkDecodeSymbolsResult"; 
             info.type = new WebMarkDecodeSymbolsResult().getClass(); 
              break; 
	     case 1:
	    	  info.name ="inParams";
	    	  info.type =new WebMarkDecodeInParams().getClass();
	    	  break;
		}
		
	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub
		switch (index)
    	{
           case 0: 
        	   webMarkDecodeResult =(WebMarkDecodeSymbolsResult)value;
                  break; 
          
           case 1: 
        	   inParams = (WebMarkDecodeInParams)value; 
                 break;
    	}
		
	}
	public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "WebMarkDecodeSymbolsResponse", this.getClass());
                   new WebMarkDecodeSymbolsResult().register(envelope);
                   new WebMarkDecodeInParams().register(envelope);
             //   new WebGenDbRequestparams().register(envelope);

    }

}
