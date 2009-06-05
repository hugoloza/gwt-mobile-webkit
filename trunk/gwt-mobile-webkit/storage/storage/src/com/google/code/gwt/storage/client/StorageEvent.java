// $Id$

package com.google.code.gwt.storage.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

public class StorageEvent extends JavaScriptObject {

    public final native String getKey() /*-{
        return this.key;
    }-*/;
    
    public final native String getOldValue() /*-{
        return this.oldValue;
    }-*/;
    
    public final native String getNewValue() /*-{
        return this.newValue;
    }-*/;

    public final native String getUrl() /*-{
        return this.url;
    }-*/;

    public final native Window getSource() /*-{
        return this.source;
    }-*/;
    
    public final native Storage getStorageArea() /*-{
        return this.storageArea;
    }-*/;
}
