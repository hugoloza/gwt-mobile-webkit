// $Id$

package com.google.code.gwt.iui.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class UnorderedList extends FlowPanel {
    
    public UnorderedList() {
        init();
    }
    
    public UnorderedList(String id, String title) {
        init();
        getElement().setId(id);
        getElement().setTitle(title);
    }
    
    private void init() {
        setElement(DOM.createElement("ul"));
    }

    public void setSelected(boolean selected) {
        getElement().setAttribute("selected", String.valueOf(selected));
    }
    
    public void add(Widget w) {
        super.add(w, getElement());
    }

    public void insert(Widget w, int beforeIndex) {
        super.insert(w, getElement(), beforeIndex, true);
    }
}
