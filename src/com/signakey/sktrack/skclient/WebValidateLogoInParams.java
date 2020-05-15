package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebValidateLogoInParams extends WebValidateLogoParams{
    @Override
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "inParams", this.getClass());
    }
}
