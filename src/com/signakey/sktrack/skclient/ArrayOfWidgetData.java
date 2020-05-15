package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.*;

import android.util.Log;

public class ArrayOfWidgetData extends BaseObject implements KvmSerializable {


public Class<com.signakey.sktrack.skclient.WidgetData> WidgetData[];
	


	@Override
	public Object getProperty(int info) {
		
		return WidgetData[info];
	}

	@Override
	public int getPropertyCount() {
		
		return 1;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
    	Log.i("SKScanner", "ArrayOfWidgetData: " + WidgetData);
        info.name = "WidgetData";
        info.type = WidgetData.class;
        return ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(int info, Object arg1) {
		
		WidgetData[info] = (Class<com.signakey.sktrack.skclient.WidgetData>) arg1;
	}
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ArrayOfWidgetData", this.getClass());
//        new MediaFile().register(envelope);
    }


}
