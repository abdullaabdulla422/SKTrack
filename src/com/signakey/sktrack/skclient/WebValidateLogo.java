package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.SoapObject;

public class WebValidateLogo {
    private static final String METHOD_NAME = "WebGetCustomer";
    private static final String NAMESPACE = "http://SignaKeyWeb/";
//    private static final String NAMESPACE = "http://localhost/";
    public WebValidateLogoParams inParams;

	public SoapObject GetSoapParams() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("inParams", inParams);
        return request;
	}

	public String GetSoapAction() {
		return NAMESPACE + METHOD_NAME;
	}
}
