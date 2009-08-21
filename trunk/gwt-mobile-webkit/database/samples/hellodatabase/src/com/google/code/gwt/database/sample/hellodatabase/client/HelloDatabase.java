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

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.SQLError;
import com.google.code.gwt.database.client.SQLResultSet;
import com.google.code.gwt.database.client.SQLTransaction;
import com.google.code.gwt.database.client.StatementCallback;
import com.google.code.gwt.database.client.TransactionCallback;
import com.google.gwt.core.client.EntryPoint;
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

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    if (!Database.isSupported()) {
      Window.alert("HTML 5 Database is NOT supported in this browser!");
      return;
    }

    // Prepare database
    final Database db = Database.openDatabase("ClckCnt", "1.0",
        "Click Counter", 10000);

    if (db == null) {
      Window.alert("opened Database is NULL! Should not happen (hosted mode?)");
      return;
    }

    // Create table 'clickcount' if it doesn't exist already:
    db.transaction(new TransactionCallback() {
      public void onTransactionStart(SQLTransaction tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS clickcount ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "clicked INTEGER)", null);
      }

      public void onTransactionFailure(SQLError error) {
        Window.alert("Failed to execute SQL! Code: " + error.getCode()
            + ", msg: " + error.getMessage());
      }

      public void onTransactionSuccess() {
      }
    });

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

    Image img = new Image("http://code.google.com/webtoolkit/logo-185x175.png");
    Button button = new Button("Click me", new ClickHandler() {
      public void onClick(ClickEvent event) {
        db.transaction(new TransactionCallback() {
          public void onTransactionStart(SQLTransaction tx) {
            tx.executeSql("INSERT INTO clickcount (clicked) VALUES (?)",
                new Object[] {new Date().getTime()});
            tx.executeSql("SELECT clicked FROM clickcount", null,
                new StatementCallback<ClickRow>() {
                  public boolean onFailure(SQLTransaction transaction,
                      SQLError error) {
                    return false;
                  }

                  public void onSuccess(SQLTransaction transaction,
                      SQLResultSet<ClickRow> resultSet) {
                    clickedData.clear();
                    for (ClickRow row : resultSet.getRows()) {
                      clickedData.add(new Label("Clicked on "
                          + row.getClicked()));
                    }
                  }
                });
          }

          public void onTransactionFailure(SQLError error) {
            Window.alert("Failed SQL TX! Code: " + error.getCode() + ", msg: "
                + error.getMessage());
          }

          public void onTransactionSuccess() {
          }
        });

        dialogBox.center();
        dialogBox.show();
      }
    });

    VerticalPanel vPanel = new VerticalPanel();
    // We can add style names.
    vPanel.addStyleName("widePanel");
    vPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    vPanel.add(img);
    vPanel.add(button);

    // Add image and button to the RootPanel
    RootPanel.get().add(vPanel);

    // Set the contents of the Widget
    dialogBox.setWidget(dialogVPanel);
  }
}
