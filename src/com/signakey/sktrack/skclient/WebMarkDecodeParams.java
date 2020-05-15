package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebMarkDecodeParams extends BaseObject {
	public String token;
	public int keyStyle;
	public Boolean wantBinary;
	public String inData;

	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0:
			return token;
		case 1:
			return keyStyle;
		case 2:
			return wantBinary;
		case 3:
			return inData;
		}

		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub

		return 4;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0:
			info.type = String.class;
			info.name = "Token";
			break;
		case 1:
			info.type = Integer.TYPE;
			info.name = "KeyStyle";
			break;
		case 2:
			info.type =  Boolean.TYPE;
			info.name = "WantBinary";
			break;
		case 3:
			info.type = String.class;
			info.name = "InData";
			break;
		}

	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0:
			token = (String) value;
			break;

		case 1:
			keyStyle = Integer.parseInt(value.toString());
			break;
		case 2:
			wantBinary = Boolean.parseBoolean(value.toString());
			break;
		case 3:
			inData = (String) value;
			break;

		}

	}

	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(NAMESPACE, "WebMarkDecodeParams", this.getClass());

	}

}
