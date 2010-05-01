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

package com.google.code.gwt.database.sample.hellodatabase.client;

import java.util.Date;
import java.util.List;

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.RowIdListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloDatabase implements EntryPoint {

  ClickCountDataService dbService = GWT.create(ClickCountDataService.class);
  private VerticalPanel vPanel;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    if (!Database.isSupported()) {
      Window.alert("HTML 5 Database is NOT supported in this browser!");
      return;
    }

    // Create the dialog box
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setText("Welcome to GWT Database Demo!");
    dialogBox.setAnimationEnabled(true);
    Button closeButton = new Button("close", new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });
    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.setWidth("100%");
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    final VerticalPanel clickedData = new VerticalPanel();
    dialogVPanel.add(clickedData);
    dialogVPanel.add(closeButton);

    dialogBox.setWidget(dialogVPanel);

    Image img = new Image("http://code.google.com/webtoolkit/logo-185x175.png");
    Button addClickButton = new Button("Add Click", new ClickHandler() {
      public void onClick(ClickEvent event) {
        dbService.insertClick(new Date(), new RowIdListCallback() {
          public void onFailure(DataServiceException error) {
            Window.alert("Failed to add click! " + error);
          }

          public void onSuccess(final List<Integer> rowIds) {
            dbService.getClicks(new ListCallback<ClickRow>() {
              public void onFailure(DataServiceException error) {
                Window.alert("Failed to query clicks! " + error);
              }

              public void onSuccess(List<ClickRow> result) {
                clickedData.clear();
                clickedData.add(new Label("Last click insert ID: "
                    + rowIds.get(0)));
                for (ClickRow row : result) {
                  clickedData.add(new Label("Clicked on " + row.getClicked()));
                }
                dialogBox.center();
                dialogBox.show();
              }
            });
          }
        });
      }
    });
    Button getCountButton = new Button("Get Counts", new ClickHandler() {
      public void onClick(ClickEvent event) {
        getCount();
      }
    });

    vPanel = new VerticalPanel();
    // We can add style names.
    vPanel.addStyleName("widePanel");
    vPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    vPanel.add(img);
    vPanel.add(addClickButton);
    vPanel.add(getCountButton);

    // Add image and button to the RootPanel
    RootPanel.get().add(vPanel);

    // Create table 'clickcount' if it doesn't exist already:
    dbService.initTable(new VoidCallback() {
      public void onFailure(DataServiceException error) {
        Window.alert("Failed to initialize table! " + error);
      }

      public void onSuccess() {
        Window.alert("Database initialized successfully.");
        getCount();
      }
    });

    getVersion();
  }

  private void getVersion() {
    dbService.getSqliteVersion(new ScalarCallback<String>() {
      public void onFailure(DataServiceException error) {
        Window.alert("Failed to get SQLite version! " + error);
      }

      public void onSuccess(String result) {
        vPanel.add(new Label("SQLite version: " + result));
      }
    });
  }

  private void getCount() {
    dbService.getClickCount(new ScalarCallback<Integer>() {
      public void onFailure(DataServiceException error) {
        Window.alert("Failed to get count! " + error);
      }

      public void onSuccess(Integer result) {
        vPanel.add(new Label("There are " + result + " recorded clicks."));
      }
    });
  }
}
