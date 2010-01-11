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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

/**
 * Represents the HTML5 Geolocation implementation.
 * 
 * @author bguijt
 * @see <a href="http://www.w3.org/TR/geolocation-API/#geolocation">W3C
 *      Geolocation API - Geolocation interface</a>
 */
public class GeolocationImpl {

  protected GeolocationImpl() {
  }

  public native boolean isSupported() /*-{
    return !!$wnd.navigator.geolocation;
  }-*/;

  public String getProviderName() {
    return "HTML5 Geolocation API";
  }
  
  /**
   * Returns the Geolocation instance.
   * 
   * @return the Geolocation instance
   */
  public native Geolocation getGeolocation() /*-{
    return $wnd.navigator.geolocation;
  }-*/;

  /*
   * Helper method for the getCurrentPosition() / watchPosition() methods
   */
  protected static final void handleSuccess(PositionCallback callback,
      Position position) {
    UncaughtExceptionHandler ueh = GWT.getUncaughtExceptionHandler();
    if (ueh != null) {
      try {
        callback.onSuccess(position);
      } catch (Throwable t) {
        ueh.onUncaughtException(t);
      }
    } else {
      callback.onSuccess(position);
    }
  }

  /*
   * Helper method for the getCurrentPosition() / watchPosition() methods
   */
  protected static final void handleError(PositionCallback callback,
      PositionError error) {
    UncaughtExceptionHandler ueh = GWT.getUncaughtExceptionHandler();
    if (ueh != null) {
      try {
        callback.onFailure(error);
      } catch (Throwable t) {
        ueh.onUncaughtException(t);
      }
    } else {
      callback.onFailure(error);
    }
  }

  public void getCurrentPosition(Geolocation geo,
      PositionCallback callback) {
    getCurrentPosition(geo, callback, null);
  }

  public void getCurrentPosition(Geolocation geo,
      PositionCallback callback, PositionOptions options) {
    _getCurrentPosition(geo, callback, options);
  }
  
  protected native void _getCurrentPosition(Geolocation geo,
      PositionCallback callback, PositionOptions options) /*-{
    geo.getCurrentPosition(
      function(position) {
        @com.google.code.gwt.geolocation.client.GeolocationImpl::handleSuccess(Lcom/google/code/gwt/geolocation/client/PositionCallback;Lcom/google/code/gwt/geolocation/client/Position;) (callback, position);
      },
      function(error) {
        @com.google.code.gwt.geolocation.client.GeolocationImpl::handleError(Lcom/google/code/gwt/geolocation/client/PositionCallback;Lcom/google/code/gwt/geolocation/client/PositionError;) (callback, error);
      },
      options
    );
  }-*/;

  public int watchPosition(Geolocation geo, PositionCallback callback) {
    return watchPosition(geo, callback, null);
  }

  public int watchPosition(Geolocation geo, PositionCallback callback,
      PositionOptions options) {
    return _watchPosition(geo, callback, options);
  }
  
  protected native int _watchPosition(Geolocation geo, PositionCallback callback,
      PositionOptions options) /*-{
    return geo.watchPosition(
      function(position) {
        @com.google.code.gwt.geolocation.client.GeolocationImpl::handleSuccess(Lcom/google/code/gwt/geolocation/client/PositionCallback;Lcom/google/code/gwt/geolocation/client/Position;) (callback, position);
      },
      function(error) {
        @com.google.code.gwt.geolocation.client.GeolocationImpl::handleError(Lcom/google/code/gwt/geolocation/client/PositionCallback;Lcom/google/code/gwt/geolocation/client/PositionError;) (callback, error);
      },
      options
    );
  }-*/;

  public void clearWatch(Geolocation geo, int watchId) {
    _clearWatch(geo, watchId);
  }
  protected native void _clearWatch(Geolocation geo, int watchId) /*-{
    geo.clearWatch(watchId);
  }-*/;
}
