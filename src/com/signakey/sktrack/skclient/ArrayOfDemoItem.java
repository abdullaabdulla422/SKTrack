package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;




import android.util.Log;

public class ArrayOfDemoItem extends BaseObject implements KvmSerializable {
	
	public Class<com.signakey.sktrack.skclient.DemoItem> DemoItem[];
	


	@Override
	public Object getProperty(int info) {
		
		return DemoItem[info];
	}

	@Override
	public int getPropertyCount() {
		
		return 1;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
    	Log.i("SKScanner", "ArrayofDemoItem: " );
        info.name = "ItemList";
        info.type = DemoItem.class;
        return ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(int info, Object arg1) {
		
		DemoItem[info] = (Class<com.signakey.sktrack.skclient.DemoItem>) arg1;
	}
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ArrayOfItemList", this.getClass());
//        new MediaFile().register(envelope);
    }

}
