/*
 * Copyright 2010 Bart Guijt and others.
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
package com.google.code.gwt.geolocation.sample.hellogeolocation.client;

import com.google.code.gwt.geolocation.client.Coordinates;
import com.google.code.gwt.geolocation.client.Geolocation;
import com.google.code.gwt.geolocation.client.Position;
import com.google.code.gwt.geolocation.client.PositionCallback;
import com.google.code.gwt.geolocation.client.PositionError;
import com.google.code.gwt.geolocation.client.PositionOptions;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloGeolocation implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        RootPanel.get().add(new Label("Uncaught exception: " + e));
      }
    });
    final VerticalPanel main = new VerticalPanel();
    RootPanel.get().add(main);

    main.add(new Label("Geolocation provider: " + Geolocation.getProviderName()));
    main.add(new Label("GWT strongname: " + GWT.getPermutationStrongName()));
    
    Label l1 = new Label("Obtaining Geolocation...");
    main.add(l1);
    if (!Geolocation.isSupported()) {
      l1.setText("Obtaining Geolocation FAILED! Geolocation API is not supported.");
      return;
    }
    final Geolocation geo = Geolocation.getGeolocation();
    if (geo == null) {
      l1.setText("Obtaining Geolocation FAILED! Object is null.");
      return;
    }
    l1.setText("Obtaining Geolocation DONE!");
    
    main.add(new Button("Get Location", new ClickHandler() {
      public void onClick(ClickEvent event) {
        obtainPosition(main, geo);
      }
    }));
    
    obtainPosition(main, geo);
  }

  private void obtainPosition(final VerticalPanel main, Geolocation geo) {
    final Label l2 = new Label("Obtaining position (timeout: 15 sec)...");
    main.add(l2);
    
    geo.getCurrentPosition(new PositionCallback() {
      public void onFailure(PositionError error) {
        String message = "";
        switch (error.getCode()) {
          case PositionError.UNKNOWN_ERROR:
            message = "Unknown Error";
            break;
          case PositionError.PERMISSION_DENIED:
            message = "Permission Denied";
            break;
          case PositionError.POSITION_UNAVAILABLE:
            message = "Position Unavailable";
            break;
          case PositionError.TIMEOUT:
            message = "Time-out";
            break;
          default:
            message = "Unknown error code.";
        }
        l2.setText("Obtaining position FAILED! Message: '"
            + error.getMessage() + "', code: " + error.getCode() + " ("
            + message + ")");
      }

      public void onSuccess(Position position) {
        l2.setText("Obtaining position DONE:");
        Coordinates c = position.getCoords();
        main.add(new Label("lat, lon: " + c.getLatitude() + ", "
            + c.getLongitude()));
        main.add(new Label("Accuracy (in meters): " + c.getAccuracy()));
        main.add(new Label("Altitude: "
            + (c.hasAltitude() ? c.getAltitude() : "[no value]")));
        main.add(new Label(
            "Altitude accuracy (in meters): "
                + (c.hasAltitudeAccuracy() ? c.getAltitudeAccuracy()
                    : "[no value]")));
        main.add(new Label("Heading: "
            + (c.hasHeading() ? c.getHeading() : "[no value]")));
        main.add(new Label("Speed: "
            + (c.hasSpeed() ? c.getSpeed() : "[no value]")));
      }
    }, PositionOptions.getPositionOptions(false, 15000, 30000));
  }
}
