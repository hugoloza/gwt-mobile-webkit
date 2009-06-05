/**
 * 
 */
package com.google.code.gwt.iui.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * @author bguijt
 */
public class LinkListItem extends ListItem {

    public LinkListItem() {
        super();
    }
    
    public LinkListItem(String text, String url) {
        super();
        setLink(text, url);
    }
    
    public void setLink(String text, String url) {
        Element a = DOM.createAnchor();
        a.setAttribute("href", url);
        a.setInnerText(text);
        getElement().appendChild(a);
    }
}
