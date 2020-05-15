package com.signakey.sktrack.skclient;

import org.ksoap2.serialization.*;

/** Base object for SignaKey's WCF server communication. */
public abstract class BaseObject implements KvmSerializable {
//	protected static final String NAMESPACE = "http://localhost/";
    protected static final String NAMESPACE = "http://SignaKeyWeb/";
    public BaseObject() {
        super();
    }
}
