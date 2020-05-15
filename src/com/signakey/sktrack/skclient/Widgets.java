package com.signakey.sktrack.skclient;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class Widgets extends BaseObject {
	public WidgetPattern WidgetPattern;
	public WidgetData WidgetData;

	@Override
	public Object getProperty(int index) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			return WidgetPattern;
		case 1:
			return WidgetData;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			info.name = "WidgetPattern";
			info.type = WidgetPattern.class;
			break;
		case 1:
			info.name = "WidgetData";
			info.type = WidgetData.class;
			break;
			
			
		}
		
	}

	@Override
	public void setProperty(int index, Object value) {
		// TODO Auto-generated method stub
		switch(index)
		{
		case 0:
			WidgetPattern = (WidgetPattern)value;
			break;
		case 1:
			WidgetData = (WidgetData)value;
			break;
			
		}
		
	}
	 public void register(SoapSerializationEnvelope envelope)
	    {
	        envelope.addMapping(NAMESPACE, "Widgets", this.getClass());
	                   new WidgetData().register(envelope); 
	                   new WidgetPattern().register(envelope);

	    }

}
