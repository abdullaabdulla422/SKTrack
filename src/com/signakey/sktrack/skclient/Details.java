package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class Details extends BaseObject {
	public DetailPattern DetailPattern;
	public DetailData DetailData;
	// public DetailValuedata data;
	

	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			return DetailPattern;
		case 1:
			 return DetailData;
////		  case 2:
//       	   return data;
			 
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 2 ;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			info.name ="DetailPattern";
			info.type = DetailPattern.class;
			break;
		case 1:
			info.name ="DetailData";
			info.type = DetailData.class;
			break;
//		 case 3: 
//             info.name = "data"; 
//             info.type = DetailValuedata.class; 
//                          break; 
                          
		default:
			break;
			
			
		}
		
	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			DetailPattern = (DetailPattern)value;
			break;
		case 1:
			DetailData = (DetailData)value;
			break;
//		  case 2: 
//       	   data = (DetailValuedata)value; 
//                break;
		default:
			break;
		}
		
	}
	
	private String GetNullableString(SoapObject entry, String name) {
		try {
			Object found = entry.getProperty(name);
			if (found == null) {
				return "";
			} else {
				return found.toString();
			}
		} catch (Exception ex) {

			return "";

		}

	}

	  
//	private ArrayList<DetailData> GetDetailLists(Object value) {
//		// Log.i("SKScanner",
//		// "WebMarkAuthenticateResult setProperty PublicFileList value = " +
//		// value);
//	
//		SoapObject stufff = (SoapObject) value;
//		ArrayList<DetailData> pojos = null;
//		int pcounts = stufff.getPropertyCount(); // Check to see what is attached
//		if (pcounts > 0) {
//			pojos = new ArrayList<DetailData>();
//			for (int pcount = 0; pcount < pcounts; pcount++) { // process the
//																	// number of
//																	// media 
//																	// files
//				SoapObject p = (SoapObject) stufff.getProperty(pcount);
//				DetailData m = new DetailData();
//				m.Keyword = GetNullableString(p, "Keyword");
//				m.FoundCount = GetNullableString(p, "FoundCount"); 
//			/*	m.Value = new DetailValue();
//				m.Value.Boolean = GetNullableString(p, "Boolean");
//				m.Value.Number = GetNullableString(p, "Number"); 
//				m.Value.String = GetNullableString(p, "String");
//				m.Value.SignaKey = new KeyNamedSerial();
//				m.Value.SignaKey.ClientNumber = Integer.parseInt(p.getProperty("ClientNumber")
//						.toString());
//				m.Value.SignaKey.ItemNumber = Integer.parseInt(p.getProperty("ItemNumber")
//						.toString()); 
//				m.Value.SignaKey.SequenceNumber = Integer.parseInt(p.getProperty("SequenceNumber")
//						.toString()); 
//				m.Value.SignaKey.VersionNumber = Integer.parseInt(p.getProperty("ItemNumber")
//						.toString()); 
//				
//				m.Value.Timestamp = GetNullableString(p, "String");*/
//
//				pojos.add(m);
//
//			} 
//		}
//		return pojos;
//	}
	
	
	
	
	
	
	
	 public void register(SoapSerializationEnvelope envelope)
	    {
	        envelope.addMapping(NAMESPACE, "Details", this.getClass());
	                   new DetailPattern().register(envelope); 
	                   new ArrayOfDetailData().register(envelope);
	                   new DetailData().register(envelope);
	                  // new DetailValue().register(envelope);
	                  // new KeyNamedSerial().register(envelope);
	                   
	                  // new DetailValuedata().register(envelope);
 
	    }

}
