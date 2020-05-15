package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.*;

import android.util.Log;

public class ArrayOfDetailData extends BaseObject implements KvmSerializable {


public Class<com.signakey.sktrack.skclient.DetailData> DetailData[];
	


	@Override
	public Object getProperty(int info) {
		
		return DetailData[info];
	}

	@Override
	public int getPropertyCount() {
		
		return 1;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
    	Log.i("SKScanner", "ArrayOfDetailData: " + DetailData);
        info.name = "DetailData";
        info.type = DetailData.class;
        return ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(int info, Object arg1) {
		
		DetailData[info] = (Class<com.signakey.sktrack.skclient.DetailData>) arg1;
	}
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ArrayOfDetailData", this.getClass());
//        new MediaFile().register(envelope);
    }



}
