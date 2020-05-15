package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

public class DetailData extends BaseObject implements KvmSerializable{

     public String Keyword;
     public Integer FoundCount;
     public DetailValue Value;
    
     
    // public DetailValuedata data;
    


    public Object getProperty(int index)
    { 
    	switch (index)
    	{
           case 0: 
                return Keyword;  
           case 1: 
                return FoundCount; 
           case 2: 
                return Value;
          
          /* case 3:
        	    return data;*/
//           case 3:
//        	   return data;

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
                info.name = "Keyword"; 
                info.type = String.class; 
                             break; 
           case 1: 
                info.name = "FoundCount"; 
                info.type =  Integer.class;
                             break; 
           case 2: 
                info.name = "Value"; 
                info.type = DetailValue.class; 
                             break;
        
        /*   case 3: 
               info.name = "data"; 
               info.type = DetailValuedata.class; 
                            break; */

        default:
            break;
        }
    }

    public void setProperty(int index, Object value)
    {
    	switch (index)
    	{
           case 0: 
                Keyword = (String)value; 
                  break; 
           case 1: 
                FoundCount = (Integer)value;  
                  break; 
           case 2: 
                Value = (DetailValue) value; 
                Log.i("SKScanner", "ItemList: Value = ");
                  break; 
           
                  
          /* case 3: 
        	   data = (DetailValuedata)value; 
                 break; 
*/ 
    	}
 
    }
 
    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DetailData", this.getClass());
                 // new DetailValue().register(envelope); 
                
                  
                //  new ArrayOfDetailValue().register(envelope);
            
                  // new DetailValuedata().register(envelope);

    }

}
