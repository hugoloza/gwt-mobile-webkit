// $Id$

package com.google.code.gwt.storage.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Implements the HTML5 Storage interface.
 * 
 * @see http://dev.w3.org/html5/webstorage/
 * @author bguijt
 */
public final class Storage extends JavaScriptObject {

    protected Storage() { }
    
    /**
     * @see http://dev.w3.org/html5/webstorage/#dom-localstorage
     * @return the localStorage instance.
     */
    public static native Storage getLocalStorage() /*-{
        return $wnd.localStorage;
    }-*/;
    
    /**
     * @see http://dev.w3.org/html5/webstorage/#dom-sessionstorage
     * @return the sessionStorage instance.
     */
    public static native Storage getSessionStorage() /*-{
        return $wnd.sessionStorage;
    }-*/;
    
    /**
     * Registers an eventlistener for StorageEvents.
     * 
     * @see http://dev.w3.org/html5/webstorage/#event-storage
     * @param listener
     */
    public native void addStorageEventHandler(StorageEventHandler handler) /*-{
        $doc.body.addEventListener(
            'storage',
            function(event) {
                @com.google.code.gwt.html5.client.storage.Storage::handleStorageEvent(Lcom/google/code/gwt/storge/client/StorageEventHandler;Lcom/google/code/gwt/storage/client/StorageEvent;)(handler, event);
            },
            false
        ); 
    }-*/;
    
    @SuppressWarnings("unused")
    private static final void handleStorageEvent(StorageEventHandler handler, StorageEvent event) {
        handler.onStorageChange(event);
    }
    
    public native int getLength() /*-{
        return this.length;
    }-*/;
    
    public native String key(int i) /*-{
        return this.key(i);
    }-*/;
    
    public native String getItem(String key) /*-{
        return this.getItem(key);
    }-*/;
    
    public native void setItem(String key, String data) /*-{
        this.setItem(key, data);
    }-*/;
    
    public native void removeItem(String key) /*-{
        this.removeItem(key);
    }-*/;
    
    public native void clear() /*-{
        this.clear();
    }-*/;
}
