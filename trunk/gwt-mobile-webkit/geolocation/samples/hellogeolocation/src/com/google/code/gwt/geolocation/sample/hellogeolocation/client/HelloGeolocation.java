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
package com.google.code.gwt.geolocation.sample.hellogeolocation.client;

import com.google.code.gwt.geolocation.client.Coordinates;
import com.google.code.gwt.geolocation.client.Geolocation;
import com.google.code.gwt.geolocation.client.Position;
import com.google.code.gwt.geolocation.client.PositionCallback;
import com.google.code.gwt.geolocation.client.PositionError;
import com.google.gwt.core.client.EntryPoint;
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
    final VerticalPanel main = new VerticalPanel();
    RootPanel.get().add(main);

    Label l1 = new Label("Obtaining Geolocation...");
    main.add(l1);
    if (!Geolocation.isSupported()) {
      l1.setText("Obtaining Geolocation... FAILED! Geolocation API is not supported.");
      return;
    }
    Geolocation geo = Geolocation.getGeolocation();
    if (geo == null) {
      l1.setText("Obtaining Geolocation... FAILED! Object is null.");
      return;
    }
    l1.setText("Obtaining Geolocation... DONE!");

    final Label l2 = new Label("Obtaining position...");
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
        l2.setText("Obtaining position... FAILED! Message: '"
            + error.getMessage() + "', code: " + error.getCode() + " ("
            + message + ")");
      }

      public void onSuccess(Position position) {
        l2.setText("Obtaining position... DONE:");
        Coordinates c = position.getCoords();
        main.add(new Label("lat, lon: " + c.getLatitude() + ", "
            + c.getLongitude()));
        main.add(new Label("Accuracy (in meters): " + c.getAccuracy()));
        main.add(new Label("Height: " + c.getAltitude()));
        main.add(new Label("Height accuracy (in meters): "
            + c.getAltitudeAccuracy()));
      }
    });
  }
}
