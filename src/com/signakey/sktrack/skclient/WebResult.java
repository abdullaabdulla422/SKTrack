package com.signakey.sktrack.skclient;

import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.Hashtable;

public class WebResult extends BaseObject {
	public int WebResultCode;
	public String WebResultMessage;
	public boolean ServiceResultSuccess;
	public String ServiceResultMessage;
	Logger logger;

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			Log.i("SKScanner", "WebResult: " + WebResultCode);



			return WebResultCode;
		case 1:
			Log.i("SKScanner", "WebResult: " + WebResultMessage);


			return WebResultMessage;
		case 2:
			Log.i("SKScanner", "WebResult: " + ServiceResultSuccess);


			return ServiceResultSuccess;
		case 3:
			Log.i("SKScanner", "WebResult: " + ServiceResultMessage);


			return ServiceResultMessage;
		}
		return null;
	}

	public int getPropertyCount() {
		return 4;
	}

	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties,
			PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "WebResultCode";
			info.type = Integer.TYPE;
			break;
		case 1:
			info.name = "WebResultMessage";
			info.type = String.class;
			break;
		case 2:
			info.name = "ServiceResultSuccess";

			info.type = Boolean.TYPE;
			break;
		case 3:
			info.name = "ServiceResultMessage";
			info.type = String.class;
			break;
		default:
			break;
		}
	}

	public void setProperty(int index, Object value) {
		switch (index) {
		case 0:
			WebResultCode = Integer.parseInt(value.toString());
			Log.i("SKScanner", "WebResult: WebResultCode = " + WebResultCode);
			break;
		case 1:
			WebResultMessage = (String) value;
			Log.i("SKScanner", "WebResult: WebResultMessage = "
					+ WebResultMessage);
			break;
		case 2:
			ServiceResultSuccess = Boolean.parseBoolean(value.toString());
			Log.i("SKScanner", "WebResult: ServiceResultSuccess = "
					+ ServiceResultSuccess);


			break;
		case 3:
			ServiceResultMessage = (String) value;
			Log.i("SKScanner", "WebResult: ServiceResultMessage = "
					+ ServiceResultMessage);

			break;
		}
	}

	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(NAMESPACE, "WebResult", this.getClass());
	}
}
