// $Id$

package com.google.code.gwt.iui.client;

/**
 * @see http://developer.apple.com/documentation/appleapplications/Reference/WebKitDOMRef/Console_idl/Classes/Console/index.html
 * @author bguijt
 */
public class Console {

    public static final native void error(String text) /*-{
        $wnd.console.error(text);
    }-*/;

    public static final native void warn(String text) /*-{
        $wnd.console.warn(text);
    }-*/;

    public static final native void info(String text) /*-{
        $wnd.console.info(text);
    }-*/;

    public static final native void log(String text) /*-{
        $wnd.console.log(text);
    }-*/;
}
