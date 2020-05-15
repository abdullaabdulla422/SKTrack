package com.signakey.sktrack.skclient;

import java.util.ArrayList;
import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

public class WidgetData extends BaseObject {

	public String Keyword;
	public int Rank;
	public String FoundCount;
	public ArrayList<DetailData> Details;
	String valuestring;
	char b = 's';

	// public ArrayList<DetailData> DetailData;

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return Keyword;
		case 1:
			return Rank;
		case 2:
			return FoundCount;
		case 3:
			return Details;
			// case 4:
			// return DetailData;
		}

		return null;
	}

	public int getPropertyCount() {
		return 4;
	}

	@SuppressWarnings("unchecked")
	public void getPropertyInfo(int index, Hashtable properties,
			PropertyInfo info) {
		// info.type = PropertyInfo.STRING_CLASS;
		switch (index) {

		case 0:
			info.name = "Keyword";
			info.type = String.class;
			break;
		case 1:
			info.name = "Rank";
			info.type = Integer.TYPE;
			break;
		case 2:
			info.name = "FoundCount";
			info.type = String.class;
			break;
		case 3:
			info.name = "Details";
			info.type = SoapObject.class;
			break;
		// case 4:
		// info.name = "DetailData";
		// info.type = SoapObject.class;
		// break;

		default:
			break;
		}
	}

	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:
			Keyword = (String) value;
			break;
		case 1:
			Rank = Integer.parseInt(value.toString());
			break;
		case 2:
			FoundCount = (String) value;
			break;
		case 3:
			Details = GetDetailLists(value);
			break;
		// case 4:
		// DetailData =GetDetailLists(value);
		// break;

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

	private char GetNullablechar(SoapObject entry, char name) {
		try {
			Object found = entry.getProperty(name);
			if (found == null) {
				return name;
			} else {
				return (char) found;
			}
		} catch (Exception ex) {

			return name;

		}

	}

	private ArrayList<DetailData> GetDetailLists(Object value) {
		// Log.i("SKScanner",
		// "WebMarkAuthenticateResult setProperty PublicFileList value = " +
		// value);

		SoapObject stufff = (SoapObject) value;
		ArrayList<DetailData> pojos = null;
		int pcounts = stufff.getPropertyCount();
		//int[] nums = (int[]) inputOStream.readObject();
		// Check to see what is
		// attached
		if (pcounts > 0) {
			pojos = new ArrayList<DetailData>();
			for (int pcount = 0; pcount < pcounts; pcount++) { // process the
																// number of
																// media
																// files
				SoapObject  p = (SoapObject) stufff.getProperty(pcount);
				
				String strproperties= p.getProperty(2).toString();
			
				//strproperties.
				
				//String strval = strproperties.substring(strproperties.indexOf("String :") + 8,6);
				
				String[] strarr = strproperties.split(";");
				
				String strval = strarr[2].replace("String=","");
				
				Log.d("string value", "" +strval);		
				
				
				DetailData m = new DetailData();
				m.Keyword = GetNullableString(p, "Keyword");

				m.FoundCount = null; 
				m.Value = new DetailValue();
				//DetailValue[] num = new DetailValue[2];
				//valuestring  =	num[0].String; 
 
				m.Value.Boolean = null;

				m.Value.Number = null;
				if (pcount != 0) {
					m.Value.SignaKey = new KeyNamedSerial();
					m.Value.SignaKey.ClientNumber = SessionData.getInstance()
							.getClient();
					Log.d("clent number", ""
							+ SessionData.getInstance().getClient());
					m.Value.SignaKey.ItemNumber = SessionData.getInstance()
							.getItem();
					m.Value.SignaKey.SequenceNumber = SessionData.getInstance()
							.getSequence();
					m.Value.SignaKey.VersionNumber = SessionData.getInstance()
							.getVersion();
				}


				else {

					m.Value.String = strval;
				}

				m.Value.Timestamp = null;
				m.Value.WidgetIndexes = new ArrayOfInt();

				pojos.add(m);

			}
		}
		return pojos;
	}
				/*m.Value = new DetailValue();

				m.Value.Boolean = null;

				m.Value.Number = null;
				if (pcount != 0) {
					m.Value.SignaKey = new KeyNamedSerial();
					m.Value.SignaKey.ClientNumber = SessionData.getInstance()
							.getClient();
					Log.d("clent number", ""
							+ SessionData.getInstance().getClient());
					m.Value.SignaKey.ItemNumber = SessionData.getInstance()
							.getItem();
					m.Value.SignaKey.SequenceNumber = SessionData.getInstance()
							.getSequence();
					m.Value.SignaKey.VersionNumber = SessionData.getInstance()
							.getVersion();
				}

				else {

					m.Value.String = GetNullableString(p, "String");
				}

				m.Value.Timestamp = null;
				m.Value.WidgetIndexes = new ArrayOfInt();

				pojos.add(m);

			}
		}
		return pojos;
	}*/

	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(NAMESPACE, "WidgetData", this.getClass());
		new Details().register(envelope);
		new DetailData().register(envelope);
		new ArrayOfDetailData().register(envelope);
		new KeyNamedSerial().register(envelope);

	}

}
