// $Id$

package com.google.code.gwt.iui.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;

public class Button extends Widget implements HasText, SourcesClickEvents {

    private ClickListenerCollection listeners;
    private String targetHistoryToken;
    
    public Button() {
        setElement(DOM.createAnchor());
        setStyleName("button");
        setText("Button");
        sinkEvents(Event.ONCLICK);
    }

    public String getText() {
        return DOM.getInnerText(getElement());
    }

    public void setText(String text) {
        setTitle((text == null) ? "" : text);
        DOM.setInnerText(getElement(), (text == null) ? "" : text);
    }

    public String getID() {
        return DOM.getElementAttribute(getElement(), "id");
    }

    public void setID(String id) {
        if (id == null) {
            DOM.removeElementAttribute(getElement(), "id");
        } else {
            DOM.setElementAttribute(getElement(), "id", id);
        }
    }

    public String getURL() {
        return DOM.getElementAttribute(getElement(), "href");
    }

    public void setURL(String url) {
        if (url != null) {
            DOM.setElementAttribute(getElement(), "href", url);
        } else {
            DOM.removeElementAttribute(getElement(), "href");
        }
    }

    public String getTarget() {
        return DOM.getElementAttribute(getElement(), "target");
    }

    public void setTarget(String target) {
        if (target != null) {
            DOM.setElementAttribute(getElement(), "target", target);
        } else {
            DOM.removeElementAttribute(getElement(), "target");
        }
    }

    public void addClickListener(ClickListener listener) {
        if (listeners == null) {
            listeners = new ClickListenerCollection();
        }
        listeners.add(listener);
    }

    public void removeClickListener(ClickListener listener) {
        if (listeners != null && listeners.size() > 0) {
            listeners.remove(listener);
        }
    }

    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONCLICK) {
            DOM.eventPreventDefault(event);
            if (listeners != null && listeners.size() > 0) {
                listeners.fireClick(this);
            }
        }
        super.onBrowserEvent(event);
    }

    /**
     * @param targetHistoryToken
     *            the targetHistoryToken to set
     */
    public void setTargetHistoryToken(String targetHistoryToken) {
        this.targetHistoryToken = targetHistoryToken;
        setURL("#" + targetHistoryToken);
    }

    /**
     * @return the targetHistoryToken
     */
    public String getTargetHistoryToken() {
        return targetHistoryToken;
    }
}
