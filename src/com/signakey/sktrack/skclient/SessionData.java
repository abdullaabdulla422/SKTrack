package com.signakey.sktrack.skclient;


import com.signakey.sktrack.SKScanner;

public class SessionData {
    private static SessionData instance;
    private String sessionId;
    private int version;
    private int client;
    private int item;
    private int sequence;
    private String genkey;
    private int valcli;
    private String valuestr;
    private int mediafiles;
    private int logger;
    private int scanhandler = 0;
    private Boolean clear_errorMsf = true;
    private Boolean auto_focus = false;
    private int autoFocus = 0;

    private String gpsSelection = "";
    private boolean badElfConnection = false;


    private String gpsCoordinates="";

    public Boolean getClear_errorMsf() {
        return clear_errorMsf;
    }

    public void setClear_errorMsf(Boolean clear_errorMsf) {
        this.clear_errorMsf = clear_errorMsf;
    }

    public static SessionData getInstance() {
        if (instance == null) {
            instance = new SessionData();
        }
        return instance;
    }

    SKScanner skScanner;


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public int getVersion() {
        return version;
    }


    public void setVersion(int version) {
        this.version = version;
    }


    public int getClient() {
        return client;
    }


    public void setClient(int client) {
        this.client = client;
    }


    public int getItem() {
        return item;
    }


    public void setItem(int item) {
        this.item = item;
    }


    public int getSequence() {
        return sequence;
    }


    public void setSequence(int sequence) {
        this.sequence = sequence;
    }


    public String getGenkey() {
        return genkey;
    }


    public void setGenkey(String genkey) {
        this.genkey = genkey;
    }


    public int getValcli() {
        return valcli;
    }


    public void setValcli(int valcli) {
        this.valcli = valcli;
    }


    public String getValuestr() {
        return valuestr;
    }


    public void setValuestr(String valuestr) {
        this.valuestr = valuestr;
    }


    public int getMediafiles() {
        return mediafiles;
    }

    public void setMediafiles(int mediafiles) {
        this.mediafiles = mediafiles;
    }

    public int getLogger() {
        return logger;
    }

    public void setLogger(int logger) {
        this.logger = logger;
    }

    public int getScanhandler() {
        return scanhandler;
    }

    public void setScanhandler(int scanhandler) {
        this.scanhandler = scanhandler;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(String gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public Boolean getAuto_focus() {
        return auto_focus;
    }

    public void setAuto_focus(Boolean auto_focus) {
        this.auto_focus = auto_focus;
    }

    public int getAutoFocus() {
        return autoFocus;
    }

    public void setAutoFocus(int autoFocus) {
        this.autoFocus = autoFocus;
    }

    public String getGpsSelection() {
        return gpsSelection;
    }

    public void setGpsSelection(String gpsSelection) {
        this.gpsSelection = gpsSelection;
    }

    public boolean isBadElfConnection() {
        return badElfConnection;
    }

    public void setBadElfConnection(boolean badElfConnection) {
        this.badElfConnection = badElfConnection;
    }

    public SKScanner getSkScanner() {
        return skScanner;
    }

    public void setSkScanner(SKScanner skScanner) {
        this.skScanner = skScanner;
    }
}
