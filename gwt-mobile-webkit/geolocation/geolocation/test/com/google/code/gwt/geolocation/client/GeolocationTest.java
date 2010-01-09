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
package com.google.code.gwt.geolocation.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.code.gwt.geolocation.client.Geolocation;

/**
 * Tests the Geolocation class.
 */
public class GeolocationTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.code.gwt.geolocation.Html5Geolocation";
  }

  public void testGeolocationSupported() {
    assertTrue("Geolocation API should be supported! User agent: "
        + getUserAgent() + ", provider: " + Geolocation.getProviderName(),
        Geolocation.isSupported());
  }

  public void testGetPosition() {
    delayTestFinish(40000); // It might take up to 30 seconds to get a GPS fix!
    Geolocation g = Geolocation.getGeolocation();
    g.getCurrentPosition(new PositionCallback() {
      public void onSuccess(Position position) {
        assertNotNull("Position may not be null!", position);
        assertNotNull("Position coordinates may not be null!",
            position.getCoords());
        assertTrue("Latitude may not be null!",
            position.getCoords().getLatitude() > 0.00001
                || position.getCoords().getLatitude() < -0.00001);
        assertTrue("Longitude may not be null!",
            position.getCoords().getLongitude() > 0.00001
                || position.getCoords().getLongitude() < -0.00001);
        finishTest();
      }

      public void onFailure(PositionError error) {
        fail("Could not get position! code=" + error.getCode() + ", msg="
            + error.getMessage());
      }
    }, PositionOptions.getPositionOptions(false, 10000, 30000));
  }

  private final static native String getUserAgent() /*-{
    return navigator.userAgent;
  }-*/;
}
