package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebMarkDecodeInParams extends WebMarkDecodeParams {
	
	 public void register(SoapSerializationEnvelope envelope) {
	        envelope.addMapping(NAMESPACE, "inParams", this.getClass());
	    }
}
