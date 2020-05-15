package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.*;

public class ArrayOfWidgetPattern extends LiteralArrayVector {


    protected String getItemDescriptor()
    {
        return "WidgetPattern";
    }

    // This describes what type of objects are to be contained in the Array
    protected Class getElementClass() {
        return new WidgetPattern().getClass();
    }

    
    public void register(SoapSerializationEnvelope envelope)
    {
        super.register(envelope, BaseObject.NAMESPACE, "ArrayOfWidgetPattern");
    }
    

}
