package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public class DetailPatternNew extends DetailPattern{
	public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "DetailPattern", this.getClass());
    }


}
