package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.*;

import android.util.Log;

public class ArrayOfDetailPattern extends BaseObject implements KvmSerializable {


public Class<com.signakey.sktrack.skclient.DetailPattern> DetailPattern[];
	


	@Override
	public Object getProperty(int info) {
		
		return DetailPattern[info];
	}

	@Override
	public int getPropertyCount() {
		
		return 1;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
    	Log.i("SKScanner", "ArrayOfDetailPattern: " + DetailPattern);
        info.name = "DetailPattern";
        info.type = DetailPattern.class;
        return ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(int info, Object arg1) {
		
		DetailPattern[info] = (Class<com.signakey.sktrack.skclient.DetailPattern>) arg1;
	}
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ArrayOfDetailPattern", this.getClass());
//        new MediaFile().register(envelope);
    }


}
