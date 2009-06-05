// $Id$

package com.google.code.gwt.iui.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class Toolbar extends Composite {

    FlowPanel toolbar;
    Element title;
    private Button leftButton;
    private Button rightButton;
    
    public Toolbar() {
        toolbar = new FlowPanel();
        initWidget(toolbar);
        toolbar.setStyleName("toolbar");
        setStyleName("toolbar");
        title = DOM.createElement("h1");
        DOM.setElementAttribute(title, "id", "pageTitle");
        DOM.appendChild(getElement(), title);
        leftButton = new Button();
        leftButton.setID("backButton");
        leftButton.setURL("#");
        leftButton.setText("");
        toolbar.add(leftButton);
        //rightButton = new Button();
        //toolbar.add(rightButton);
        setTitle("Toolbar");
    }

    /**
     * @param pageTitle the pageTitle to set
     */
    public void setTitle(String text) {
        DOM.setInnerText(title, (text == null) ? "" : text);
        super.setTitle(text);
    }

    /**
     * @return the pageTitle
     */
    public String getTitle() {
        return DOM.getInnerText(title);
    }

    /**
     * @param leftButton the leftButton to set
     */
    public void setLeftButton(Button leftButton) {
        this.leftButton = leftButton;
    }

    /**
     * @return the leftButton
     */
    public Button getLeftButton() {
        return leftButton;
    }

    /**
     * @param rightButton the rightButton to set
     */
    public void setRightButton(Button rightButton) {
        this.rightButton = rightButton;
    }

    /**
     * @return the rightButton
     */
    public Button getRightButton() {
        return rightButton;
    }
}
