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

import com.google.gwt.core.client.JavaScriptException;

/**
 * gears-specific implementation of Geolocation API.
 * 
 * @author bguijt
 * 
 * @see <a
 *      href="http://code.google.com/intl/ja/apis/gears/api_geolocation.html">Gears
 *      API - Geolocation</a>
 */
public class GeolocationImplGears extends GeolocationImpl {

  private static final GearsFactory factory = GearsFactory.getInstance();

  @Override
  public native boolean isSupported() /*-{
    return typeof google.gears.factory != undefined
        && google.gears.factory.version != undefined
  }-*/;

  @Override
  public Geolocation getGeolocation() {
    return factory.createGeolocation();
  }

  @Override
  public String getProviderName() {
    return "Google Gears API " + factory.getVersion();
  }
  
  @Override
  public void getCurrentPosition(Geolocation geo,
      PositionCallback callback, PositionOptions options) {
    try {
      _getCurrentPosition(geo, callback, options);
    } catch (JavaScriptException e) {
      handleGearsError(callback, e);
    }
  }

  @Override
  public int watchPosition(Geolocation geo, PositionCallback callback,
      PositionOptions options) {
    try {
      return _watchPosition(geo, callback, options);
    } catch (JavaScriptException e) {
      handleGearsError(callback, e);
      return 0;
    }
  }

  private void handleGearsError(PositionCallback callback, JavaScriptException jse) {
    String message = jse.getDescription();
    // Very annoying bug in Gears: Sometimes the Gears call fails with a
    // message "Null or undefined passed for required argument 1."
    boolean gearsBug = (message != null && message.indexOf("Null or undefined passed") == 0);
    handleError(callback, PositionError.create(gearsBug ? 0 : 1, message));
    if (gearsBug) {
      logMessage("Stumbled upon a Gears bug - please report to GWT Mobile WebKit project team! " + jse.getMessage());
    }
  }

  private native void logMessage(String message) /*-{
    if ((typeof console != "undefined") && (typeof console.log == "function")) {
      console.log(message);
    }
  }-*/;
}
