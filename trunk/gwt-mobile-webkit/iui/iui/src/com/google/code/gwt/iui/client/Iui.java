// $Id$

package com.google.code.gwt.iui.client;

import java.util.Arrays;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Models the Javascript functions from iui.js
 * @author bguijt
 */
public class Iui implements EntryPoint {

    private static Widget currentDialog = null;
    private static Widget currentPage = null;
    private static Timer checkTimer = null;
    private static int currentWidth = 0;

    public void onModuleLoad() {
        initIui();
    }
    
    public final static void initIui() {
        Widget page = getSelectedPage();
        if (page != null) {
            showPage(page);
        }

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                preloadImages();
            }
        });
        
        setupOrientationChangeListener();
    }
    
    private static final native void setupOrientationChangeListener() /*-{
        $wnd.addEventListener("orientationchange", function() {
            @com.google.code.gwt.iui.client.Iui::handleOrientationChange(I)($wnd.orientation)
            });
    }-*/;

    public final static void showPage(Widget page) {
        showPage(page, false);
    }
    public final static void showPage(final Widget page, final boolean backwards) {
        if (page != null) {
            if (currentDialog != null) {
                currentDialog.getElement().removeAttribute("selected");
                currentDialog = null;
            }

            String[] pageStyles = page.getStyleName().split("\\s+");
            
            if (Arrays.asList(pageStyles).contains("dialog")) {
                showDialog(page);
            } else {
                final Widget fromPage = currentPage;
                currentPage = page;

                if (fromPage != null) {
                    Timer t = new Timer() {
                        public void run() {
                            slidePages(fromPage, page, backwards);
                        }
                    };
                    t.schedule(0);
                    //setTimeout(slidePages, 0, fromPage, page, backwards);
                } else {
                    updatePage(page, fromPage);
                }
            }
        }
    }
    
    public final static void showDialog(Widget page) {
        
    }
    
    public final static void updatePage(Widget page, Widget fromPage) {
        
    }
    
    public final static Widget getSelectedPage() {
        Iterator<Widget> iter = RootPanel.get().iterator();
        while (iter.hasNext()) {
            Widget w = iter.next();
            if ("true".equals(w.getElement().getAttribute("selected"))) {
                return w;
            }
        }
        return null;
    }
    
    public final static void preloadImages() {
        Element div = DOM.createDiv();
        div.setId("preloader");
        RootPanel.getBodyElement().appendChild(div);
    }
    
    public final static void handleOrientationChange(int orientation) {
        currentWidth = Window.getClientWidth();
        RootPanel.getBodyElement().setAttribute("orient", orientation == 0 ? "profile" : "landscape");
        new Timer() {
            public void run() {
                scrollTo(0, 1);
            }
        }.schedule(100);
        
        /*
        if (location.hash != currentHash)
        {
            var pageId = location.hash.substr(hashPrefix.length)
            iui.showPageById(pageId);
        }
         */
    }
    
    public final static void slidePages(Widget fromPage, Widget toPage, boolean backwards) {
        String axis = (backwards ? fromPage : toPage).getElement().getAttribute("axis");
        if ("y".equals(axis)) {
            (backwards ? fromPage : toPage).getElement().getStyle().setProperty("top", "100%");
        } else {
            toPage.getElement().getStyle().setProperty("left", "100%");
        }
        
        toPage.getElement().setAttribute("selected", "true");
        scrollTo(0, 1);
        if (checkTimer != null) checkTimer.cancel();
        
        
    }
    /*
function slidePages(fromPage, toPage, backwards)
{        
    var axis = (backwards ? fromPage : toPage).getAttribute("axis");
    if (axis == "y")
        (backwards ? fromPage : toPage).style.top = "100%";
    else
        toPage.style.left = "100%";

    toPage.setAttribute("selected", "true");
    scrollTo(0, 1);
    clearInterval(checkTimer);
    
    var percent = 100;
    slide();
    var timer = setInterval(slide, slideInterval);

    function slide()
    {
        percent -= slideSpeed;
        if (percent <= 0)
        {
            percent = 0;
            if (!hasClass(toPage, "dialog"))
                fromPage.removeAttribute("selected");
            clearInterval(timer);
            checkTimer = setInterval(checkOrientAndLocation, 300);
            setTimeout(updatePage, 0, toPage, fromPage);
        }
    
        if (axis == "y")
        {
            backwards
                ? fromPage.style.top = (100-percent) + "%"
                : toPage.style.top = percent + "%";
        }
        else
        {
            fromPage.style.left = (backwards ? (100-percent) : (percent-100)) + "%"; 
            toPage.style.left = (backwards ? -percent : percent) + "%"; 
        }
    }
}
     */
    
    public static final native void scrollTo(int x, int y) /*-{
        $wnd.scrollTo(x, y);
    }-*/;
}
