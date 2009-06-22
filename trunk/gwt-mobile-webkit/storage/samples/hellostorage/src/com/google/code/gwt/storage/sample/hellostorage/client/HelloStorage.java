/*
 * Copyright 2009 Bart Guijt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.code.gwt.storage.sample.hellostorage.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.code.gwt.storage.client.Storage;
import com.google.code.gwt.storage.client.StorageEvent;
import com.google.code.gwt.storage.client.StorageEventHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloStorage implements EntryPoint {

  private List<StorageEventHandler> handlers = new ArrayList<StorageEventHandler>();
  private TextArea eventArea;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    VerticalPanel main = new VerticalPanel();
    RootPanel.get().add(main);
    RootPanel.get().setWidgetPosition(main, 10, 10);

    HorizontalPanel eventPanel = new HorizontalPanel();
    main.add(eventPanel);

    eventArea = new TextArea();
    eventArea.setStyleName("widePanel");
    eventArea.setHeight("60px");
    eventArea.setText("[StorageEvent info]");
    eventPanel.add(eventArea);
    eventPanel.add(new Button("Add a Handler", new ClickHandler() {
      public void onClick(ClickEvent event) {
        StorageEventHandler handler = new MyHandler(handlers.size() + 1);
        handlers.add(handler);
        Storage.addStorageEventHandler(handler);
      }
    }));
    eventPanel.add(new Button("Delete a Handler", new ClickHandler() {
      public void onClick(ClickEvent event) {
        if (handlers.size() > 0) {
          StorageEventHandler handler = handlers.remove(handlers.size() - 1);
          Storage.removeStorageEventHandler(handler);
        }
      }
    }));

    Storage local = Storage.getLocalStorage();
    Storage session = Storage.getSessionStorage();
    if (local == null) {
      Window.alert("Web Storage NOT supported in this browser!");
      return;
    }

    TabPanel tabs = new TabPanel();
    main.add(tabs);
    tabs.add(createStorageTab(local), "localStorage");
    tabs.add(createStorageTab(session), "sessionStorage");
    tabs.selectTab(0);
  }

  private Widget createStorageTab(final Storage storage) {
    final Grid grid = new Grid();
    grid.setCellPadding(5);
    grid.setBorderWidth(1);
    renderGrid(grid, storage);

    VerticalPanel p = new VerticalPanel();

    HorizontalPanel hp = new HorizontalPanel();
    p.add(hp);
    hp.add(new Label("key:"));
    final TextBox keyInput = new TextBox();
    hp.add(keyInput);
    hp.add(new Label("data:"));
    final TextBox dataInput = new TextBox();
    hp.add(dataInput);

    hp.add(new Button("Put in storage", new ClickHandler() {
      public void onClick(ClickEvent event) {
        storage.setItem(keyInput.getText(), dataInput.getText());
        renderGrid(grid, storage);
      }
    }));

    p.add(new Button("Clear storage", new ClickHandler() {
      public void onClick(ClickEvent event) {
        storage.clear();
        renderGrid(grid, storage);
      }
    }));

    p.add(grid);

    return p;
  }

  private static void renderGrid(Grid grid, Storage storage) {
    grid.clear();
    grid.resize(storage.getLength() + 1, 3);
    grid.setWidget(0, 0, new HTML("<b>Key</b>"));
    grid.setWidget(0, 1, new HTML("<b>Data</b>"));
    for (int i = 1; i <= storage.getLength(); i++) {
      String key = storage.key(i - 1);
      grid.setWidget(i, 0, new Label(key));
      grid.setWidget(i, 1, new Label(storage.getItem(key)));
      grid.setWidget(i, 2, new DeleteButton(storage, key, grid));
    }
  }

  static class DeleteButton extends Button implements ClickHandler {
    private Storage s;
    private String key;
    private Grid grid;

    DeleteButton(Storage s, String key, Grid grid) {
      this.s = s;
      this.key = key;
      this.grid = grid;
      setText("Delete");
      addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
      s.removeItem(key);
      renderGrid(grid, s);
    }
  }

  private class MyHandler implements StorageEventHandler {
    private int nr;

    private MyHandler(int nr) {
      this.nr = nr;
    }

    public void onStorageChange(StorageEvent event) {
      eventArea.setText(eventArea.getText() + "\nStorageEvent: Handler=" + nr
          + ", key=" + event.getKey() + ", oldValue=" + event.getOldValue()
          + ", newValue=" + event.getNewValue() + ", url=" + event.getUrl()
          + ", timestamp=" + new Date());
      // eventArea.setText("StorageEvent: attrs=" +
      // event.enumerateAttributes() + ", timestamp=" + new Date());
    }
  }
}
