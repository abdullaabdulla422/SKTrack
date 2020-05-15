package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public class WebValidateUserInParams extends WebValidateUserParams {
    @Override
    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "inParams", this.getClass());
    }
}
