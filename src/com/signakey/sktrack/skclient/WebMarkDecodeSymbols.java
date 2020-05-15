package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.SoapObject;

public class WebMarkDecodeSymbols {
	 private static final String METHOD_NAME ="WebMarkDecodeSymbols";
	    private static final String NAMESPACE ="http://SignaKeyWeb/";
	    
	     public WebMarkDecodeParams inParams;
	     public SoapObject GetSoapParams() {
		        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		      
		        request.addProperty("inParams", inParams);
		        
		        
		       // request.addProperty("Request", Request);
		        return request;
			}
		    public String GetSoapAction() {
				return NAMESPACE + METHOD_NAME;
			}
	     

}
