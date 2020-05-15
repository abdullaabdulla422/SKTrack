package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DetailValuedata  extends BaseObject{
	 public String Boolean;
	  
	     public String Number;
	     public String string;
	    
	     public KeyNamedSerial SignaKey;
	     public DateTime Timestamp;

	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			return Boolean;
		case 1:
			 return Number;
		case 2:
			return string;
			
		case 3:
			return SignaKey;
		case 4:
			return Timestamp;
			
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
		switch(index)
		{
		 case 0: 
	         info.name = "Boolean"; 
	         info.type = String.class; 
	                      break; 
		 case 1: 
	         info.name = "Number"; 
	         info.type = String.class; 
	                      break; 
		 case 2: 
	         info.name = "String"; 
	         info.type = String.class; 
	                      break; 
		 case 3: 
	         info.name = "SignaKey"; 
	         info.type = KeyNamedSerial.class; 
	                      break; 
		 case 4: 
             info.name = "Timestamp"; 
            info.type = DateTime.class; 
                         break; 
	                      
	                      
		}
		
	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub
		switch (index)
    	{
           case 0: 
                Boolean = (String)value;
                  break; 
//           case 1: 
//                MediaFile = (MediaFile)value; 
//                  break; 
           case 1: 
                Number =  (String)value; 
                  break; 
           case 2: 
        	   string =  (String)value; 
                 break;
           case 3:
        	   SignaKey = (KeyNamedSerial)value;
        	   break;
           case 4: 
        	   Timestamp = (DateTime)value;
                  break; 
	}
	
	}
	 public void register(SoapSerializationEnvelope envelope)
	    {
	        envelope.addMapping(NAMESPACE, "DetailValuedata", this.getClass());
	                 
	           new KeyNamedSerial().register(envelope); 
	         

	    }
}
