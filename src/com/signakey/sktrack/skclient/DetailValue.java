package com.signakey.sktrack.skclient;

import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DetailValue extends BaseObject implements KvmSerializable {

     public Boolean Boolean;
     public MediaFile MediaFile;
    // public MediaFile MediaFile;
     public DecimalFormatSymbols Number;
     public KeyNamedSerial SignaKey; 
     public String String;
    // int a = Integer.parseInt(String);
   
//     
    public Date Timestamp;  
    public ArrayOfInt WidgetIndexes;
 
     
  
//
    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Boolean; 
           case 1: 
               return MediaFile;
//          case 1: 
//                return MediaFile; 
           case 2:  
                return Number; 
           
          case 3: 
               return SignaKey;
          case 4: 
              return String; 
         case 5: 
              return Timestamp;
         case 6: 
             return WidgetIndexes;
//           case 6:  
//              return WidgetIndexes; 

    	} 

    	return null;
    }
 
    public int getPropertyCount()
    {
        return 7 ;
    }

    @SuppressWarnings("unchecked")
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
    	//info.type = PropertyInfo.STRING_CLASS;
    	switch (index) {

           case 0: 
                info.name = "Boolean"; 
                info.type = Boolean.class; 
                             break; 
           case 1: 
               info.name = "MediaFile"; 
               info.type = MediaFile.class; 
                            break;
//           case 1: 
//                info.name = "MediaFile"; 
//                info.type = MediaFile.getClass(); 
//                             break; 
           case 2: 
                info.name = "Number"; 
                info.type = DecimalFormatSymbols.class; 
                             break; 
         
           case 3: 
               info.name = "SignaKey"; 
               info.type = KeyNamedSerial.class; 
                            break; 

           case 4: 
                info.name = "String"; 
                info.type = String.class;
                		
                             break; 
           case 5: 
                info.name = "Timestamp"; 
               info.type = Timestamp.getClass(); 
                            break; 
           case 6:
                            info.name = "WidgetIndexes"; 
                            info.type = WidgetIndexes.getClass();
                            break;
//                            
//           case 6: 
//                info.name = "WidgetIndexes"; 
//                info.type = WidgetIndexes.getClass(); 
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
                Boolean = (Boolean)value;
                  break; 
          
           case 1: 
                MediaFile = (MediaFile)value; 
                 break; 
           case 2: 
                Number =  (DecimalFormatSymbols)value; 
                 break;  
           case 3: 
               SignaKey = (KeyNamedSerial)value; 
                 break; 
           case 4: 
                String = (String)value;
                  break; 
           
          case 5: 
       	   Timestamp = (Date)value ;
                  break;
           case 6: 
               WidgetIndexes = (ArrayOfInt)value; 
                 break; 

//           case 6: 
//                WidgetIndexes = (ArrayOfInt)value; 
//                 break; 
 
    	}
  
    }
  
    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DetailValue", this.getClass());
                  // new MediaFile().register(envelope); 
          new KeyNamedSerial().register(envelope); 
          new ArrayOfInt().register(envelope);
          new MediaFile().register(envelope); 
          // new ConstraintTimestamp().register(envelope);
          // new ArrayOfInt().register(envelope); 
 
    }
  
}
	

