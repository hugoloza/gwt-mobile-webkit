// $Id$

package com.google.code.gwt.html5.applicationcache.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.EventListener;

public final class ApplicationCache extends JavaScriptObject {

    // getStatus() values:
    public static final short UNCACHED = 0; 
    public static final short IDLE = 1; 
    public static final short CHECKING = 2; 
    public static final short DOWNLOADING = 3; 
    public static final short UPDATEREADY = 4;
    public static final short OBSOLETE = 5;
    
    // event types for addEventListener():
    public static final String ONCHECKING = "checking";
    public static final String ONERROR = "error";
    public static final String ONNOUPDATE = "noupdate";
    public static final String ONDOWNLOADING = "downloading";
    public static final String ONPROGRESS = "progress";
    public static final String ONUPDATEREADY = "updateready";
    public static final String ONCACHED = "cached";

    protected ApplicationCache() { }
    
    public static native ApplicationCache getApplicationCache() /*-{
        return $wnd.applicationCache;
    }-*/;
    
    public native void addEventListener(String type, EventListener listener, boolean bubble) /*-{
        this.addEventListener(
            type,
            function(event) {
                listener.@com.google.gwt.user.client.EventListener::onBrowserEvent(Lcom/google/gwt/user/client/Event;)(event);
            },
            bubble
        );
    }-*/;
    
    public native void update() /*-{
        this.update();
    }-*/;
    
    public native void swapCache() /*-{
        this.swapCache();
    }-*/;
    
    public native int getStatus() /*-{
       return this.status;
    }-*/;
}
