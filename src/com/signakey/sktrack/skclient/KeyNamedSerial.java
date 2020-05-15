package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class KeyNamedSerial extends BaseObject {

     public Integer VersionNumber;
     public Integer ClientNumber;
     public Integer ItemNumber;
     public Integer SequenceNumber;
//     public boolean CustomerWild;
//     public boolean AccountWild;
//     public boolean ClientWild;
//     public boolean ItemWild;

     public KeyNamedSerial(){}
    public KeyNamedSerial(SoapObject j) {
		// TODO Auto-generated constructor stub
	}
	public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return VersionNumber; 
           case 1: 
                return ClientNumber; 
           case 2: 
                return ItemNumber; 
           case 3: 
                return SequenceNumber; 
//           case 4: 
//                return CustomerWild; 
//           case 5: 
//                return AccountWild; 
//           case 6: 
//                return ClientWild; 
//           case 7: 
//                return ItemWild; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 4;
    }

    @SuppressWarnings("unchecked")
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
    	//info.type = PropertyInfo.STRING_CLASS;
    	switch (index) {

           case 0: 
                info.name = "VersionNumber"; 
                info.type = Integer.class; 
                             break; 
           case 1: 
                info.name = "ClientNumber"; 
                info.type = Integer.class;  
                             break; 
           case 2: 
                info.name = "ItemNumber"; 
                info.type = Integer.class; 
                             break; 
           case 3: 
                info.name = "SequenceNumber"; 
                info.type =  Integer.class; 
                             break; 
//           case 4: 
//                info.name = "CustomerWild"; 
//                info.type = Boolean.TYPE; 
//                             break; 
//           case 5: 
//                info.name = "AccountWild"; 
//                info.type = Boolean.TYPE;  
//                             break; 
//           case 6: 
//                info.name = "ClientWild"; 
//                info.type = Boolean.TYPE;  
//                             break; 
//           case 7: 
//                info.name = "ItemWild"; 
//                info.type =  Boolean.TYPE; 
//                             break; 

        default:
            break;
        }
    }

    public void setProperty(int index, Object value)
    {
    	switch (index)
    	{
           case 0: 
                VersionNumber = Integer.parseInt(value.toString());
                  break; 
           case 1: 
                ClientNumber = Integer.parseInt(value.toString());
                  break; 
           case 2: 
                ItemNumber = Integer.parseInt(value.toString());
                  break; 
           case 3: 
                SequenceNumber = Integer.parseInt(value.toString());
                  break; 
//           case 4: 
//        	   CustomerWild = Boolean.parseBoolean(value.toString()); 
//                  break; 
//           case 5: 
//        	   AccountWild =  Boolean.parseBoolean(value.toString()); 
//                  break; 
//           case 6: 
//        	   ClientWild =  Boolean.parseBoolean(value.toString()); 
//                  break; 
//           case 7: 
//        	   ItemWild = Boolean.parseBoolean(value.toString()); 
//                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "KeyNamedSerial", this.getClass());
     
        
    }

}
