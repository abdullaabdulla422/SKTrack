package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class KeySelectorSerial extends BaseObject {

     public int VersionNumber;
     public int ClientNumber;
     public int ItemNumber;
     public int SequenceNumber;
    public String CustomerIdentifier;
     public String AccountIdentifier;
     public String ClientIdentifier;
    public String ItemIdentifier;
     public boolean CustomerWild;
     public boolean AccountWild;
     public boolean ClientWild;
     public boolean ItemWild;


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
           case 4: 
                return CustomerIdentifier; 
           case 5: 
                return AccountIdentifier; 
           case 6: 
                return ClientIdentifier; 
           case 7: 
                return ItemIdentifier; 
           case 8: 
                return CustomerWild; 
           case 9: 
                return AccountWild; 
           case 10: 
                return ClientWild; 
           case 11: 
                return ItemWild; 

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 12;
    }

    @SuppressWarnings("unchecked")
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
    	//info.type = PropertyInfo.STRING_CLASS;
    	switch (index) {

           case 0: 
                info.name = "VersionNumber"; 
                info.type = Integer.TYPE; 
                             break; 
           case 1: 
                info.name = "ClientNumber"; 
                info.type = Integer.TYPE; 
                             break; 
           case 2: 
                info.name = "ItemNumber"; 
                info.type = Integer.TYPE;
                             break; 
           case 3: 
                info.name = "SequenceNumber"; 
                info.type =  Integer.TYPE;
                             break; 
           case 4: 
                info.name = "CustomerIdentifier"; 
                info.type =String.class; 
                             break; 
           case 5: 
                info.name = "AccountIdentifier"; 
                info.type = String.class;
                             break; 
           case 6: 
                info.name = "ClientIdentifier"; 
                info.type = String.class;
                             break; 
           case 7: 
                info.name = "ItemIdentifier"; 
                info.type =  String.class;
                             break; 
           case 8: 
                info.name = "CustomerWild"; 
                info.type = Boolean.TYPE; 
                             break; 
           case 9: 
                info.name = "AccountWild"; 
                info.type = Boolean.TYPE; 
                             break; 
           case 10: 
                info.name = "ClientWild"; 
                info.type = Boolean.TYPE; 
                             break; 
           case 11: 
                info.name = "ItemWild"; 
                info.type = Boolean.TYPE; 
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
           case 4: 
                CustomerIdentifier = (String)value; 
                  break; 
           case 5: 
                AccountIdentifier = (String)value; 
                  break; 
           case 6: 
                ClientIdentifier = (String)value; 
                  break; 
           case 7: 
                ItemIdentifier = (String)value; 
                  break; 
           case 8: 
                CustomerWild = Boolean.getBoolean(value.toString()); 
                  break; 
           case 9: 
                AccountWild = Boolean.getBoolean(value.toString()); 
                  break; 
           case 10: 
                ClientWild = Boolean.getBoolean(value.toString()); 
                  break; 
           case 11: 
                ItemWild = Boolean.getBoolean(value.toString()); 
                  break; 

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "KeySelectorSerial", this.getClass());
        
    }

}
