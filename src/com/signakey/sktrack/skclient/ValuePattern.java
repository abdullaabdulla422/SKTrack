package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class ValuePattern extends BaseObject {

     public String Boolean;
     public MediaFileMatch MediaFile;
     public NumberMatch  Number;
   
     public SignaKeyMatch SignaKey;
     
     public StringMatch String;
     public TimestampMatch Timestamp;
     public WidgetMatch Widget;
    
     
    


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Boolean; 
           case 1: 
               return MediaFile; 
           case 2: 
               return Number;
          
           case 3: 
                return SignaKey; 
           case 4: 
               return String; 
           case 5: 
               return Timestamp;
           case 6: 
               return Widget;
           

    	}

    	return null;
    }

    public int getPropertyCount()
    {
        return 7;
    }

    @SuppressWarnings("unchecked")
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
    	//info.type = PropertyInfo.STRING_CLASS;
    	switch (index) {

           case 0: 
                info.name = "Boolean"; 
                info.type = String.class; 
                             break; 
           case 1: 
               info.name = "MediaFile"; 
               info.type = MediaFileMatch.class; 
                            break;
           case 2: 
               info.name = "Number"; 
               info.type = NumberMatch.class; 
                            break;
      
           case 3: 
                info.name = "SignaKey"; 
                info.type = SignaKeyMatch.class; 
                             break; 
           case 4: 
               info.name = "String"; 
               info.type = StringMatch.class; 
                            break;
           case 5: 
               info.name = "Timestamp"; 
               info.type = TimestampMatch.class; 
                            break;
           case 6: 
               info.name = "Widget"; 
               info.type = WidgetMatch.class; 
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
                Boolean = (String)value;
                  break; 
           case 1: 
        	   MediaFile = (MediaFileMatch)value; 
                 break; 
           case 2: 
        	   Number = (NumberMatch)value; 
                 break; 
        
           case 3: 
                SignaKey = (SignaKeyMatch)value; 
                  break; 
           case 4: 
        	   String = (StringMatch)value; 
                 break; 
           case 5: 
        	   Timestamp = (TimestampMatch)value; 
                 break; 
           case 6: 
        	   Widget = (WidgetMatch)value; 
                 break; 
         

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "ValuePattern", this.getClass());
                
      
           new SignaKeyMatch().register(envelope); 
           new StringMatch().register(envelope);
           new MediaFileMatch().register(envelope);
           new NumberMatch().register(envelope);
           new TimestampMatch().register(envelope);
           new WidgetMatch().register(envelope);
           
           
         

    }

}
