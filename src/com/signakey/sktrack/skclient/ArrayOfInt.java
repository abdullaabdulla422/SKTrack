package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.*;

public class ArrayOfInt extends LiteralArrayVector {


    protected String getItemDescriptor()
    {
        return "int";
    }

    // This describes what type of objects are to be contained in the Array
    protected Class getElementClass() {
        return new ArrayOfInt().getClass();
    }

    
    public void register(SoapSerializationEnvelope envelope)
    {
        super.register(envelope, BaseObject.NAMESPACE, "ArrayOfInt");
    }
    

}
