package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebMarkDecodeSymbolsResult extends BaseObject {
		public WebResult Common;
	    public String OutText ;
	    public String OutData;

	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		 switch(index){
         case 0:
             return Common;
         case 1:
             return OutText;
         case 2:
             return OutData;
     }
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
		 switch(index){
         case 0:
             info.type = WebResult.class;
             info.name = "Common";
             break;
         case 1:
             info.type = String.class;
             info.name = "OutText";
             break;
         case 2:
             info.type = String.class;
             info.name = "OutData";
             break;
     }
	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub
		switch (index)
    	{
           case 0: 
        	   Common =(WebResult)value;
                  break; 
          
           case 1: 
        	   OutText = (String)value;
                 break;
        case 2: 
        	OutData =(String)value;
               break;
     
                 
    	}
		
		
	}
	 public void register(SoapSerializationEnvelope envelope)
	    {
	        envelope.addMapping(NAMESPACE, "WebMarkDecodeSymbolsResult", this.getClass());
	                  
	        new WebResult().register(envelope);
	    }

}
