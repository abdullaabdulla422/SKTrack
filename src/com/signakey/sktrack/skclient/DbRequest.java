package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DbRequest extends BaseObject {

     public int Operation;
     public DbPattern Pattern;
  //   public DbData Data = null;

  


    public Object getProperty(int index)
    {
    	switch (index)
    	{
           case 0: 
                return Operation; 
           case 1: 
                return Pattern; 
//           case 2: 
//               return Data; 
//         
          
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
                info.name = "Operation"; 
                info.type = Integer.TYPE; 
                             break; 
           case 1: 
                info.name = "Pattern"; 
                info.type = DbPattern.class; 
                             break; 
//           case 2: 
//               info.name = "Data"; 
//               info.type = DbData.class; 
//                            break; 
          
           

        default:
            break;
        }
    }

    public void setProperty(int index, Object value)
    {
    	switch (index)
    	{
           case 0: 
                Operation = Integer.parseInt(value.toString()); 
                  break; 
           case 1: 
                Pattern = (DbPattern)value; 
                  break;
//           case 2: 
//        	   Data = (DbData)value; 
//                 break;
                  
          

    	}

    }

    public void register(SoapSerializationEnvelope envelope)
    {
        envelope.addMapping(NAMESPACE, "DbRequest", this.getClass());
                   new DbPattern().register(envelope); 
                  // new DbData().register(envelope);
         
    }

}
