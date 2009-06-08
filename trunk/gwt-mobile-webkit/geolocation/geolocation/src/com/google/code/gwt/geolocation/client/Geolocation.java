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

package com.google.code.gwt.geolocation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;


/**
 * Represents a HTML5 Geolocation object.
 * 
 * @see <a href="http://www.w3.org/TR/geolocation-API/">W3C Geolocation</a>
 */
public class Geolocation extends JavaScriptObject {
  
  private final static GeolocationImpl impl = GWT.create(GeolocationImpl.class);
  
  protected Geolocation () {
  }
  
  /**
   * Returns the Geolocation object.
   */
  public final static Geolocation getGeolocation() {
    return impl.getGeolocation();
  }
  
  /**
   * Returns the current position.
   * 
   * @param callback invoked when a position is calculated, or when an error occurs
   * @see <a href="http://www.w3.org/TR/geolocation-API/#get-current-position">W3C Geolocation API - Geolocation.getCurrentPosition</a>
   */
  public final void getCurrentPosition(PositionCallback callback) {
    impl.getCurrentPosition(this, callback);
  }
  
  /**
   * Returns the current position with the specified options.
   * 
   * @param callback invoked when a position is calculated, or when an error occurs
   * @param options configures this operation
   * @see <a href="http://www.w3.org/TR/geolocation-API/#get-current-position">W3C Geolocation API - Geolocation.getCurrentPosition</a>
   */
  public final void getCurrentPosition(PositionCallback callback, PositionOptions options) {
    impl.getCurrentPosition(this, callback, options);
  }
  
  /**
   * Starts a Watch process invoking the specified callback whenever the position changes.
   * 
   * @param callback invoked when a new position is established, or when an error occurs
   * @return a watchId identifying the watch process
   * @see #clearWatch(int)
   * @see <a href="http://www.w3.org/TR/geolocation-API/#watch-position">W3C Geolocation API - Geolocation.watchPosition</a>
   */
  public final int watchPosition(PositionCallback callback) {
    return impl.watchPosition(this, callback);
  }
  
  /**
   * Starts a Watch process invoking the specified callback whenever the position changes.
   * 
   * @param callback invoked when a new position is established, or when an error occurs
   * @param options configures this operation
   * @return a watchId identifying the watch process
   * @see #clearWatch(int)
   * @see <a href="http://www.w3.org/TR/geolocation-API/#watch-position">W3C Geolocation API - Geolocation.watchPosition</a>
   */
  public final int watchPosition(PositionCallback callback, PositionOptions options) {
    return impl.watchPosition(this, callback, options);
  }
  
  /**
   * Stops the watch process identified by the specified watchId.
   * 
   * @param watchId the watch process ID to stop
   * @see <a href="http://www.w3.org/TR/geolocation-API/#clear-watch">W3C Geolocation API - Geolocation.clearWatch</a>
   */
  public final void clearWatch(int watchId) {
    impl.clearWatch(this, watchId);
  }
}
