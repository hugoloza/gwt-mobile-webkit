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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents options to configure the {@link Geolocation} operations.
 * 
 * @author bguijt
 * @see <a href="http://www.w3.org/TR/geolocation-API/#position-options">W3C
 *      Geolocation API - PositionOptions interface</a>
 */
public class PositionOptions extends JavaScriptObject {

  protected PositionOptions() {
  }

  /**
   * Creates a new PositionOptions object to use in the {@link Geolocation}
   * functions.
   * 
   * @param enableHighAccuracy a hint that the application would like to receive
   *          the best possible results
   * @param timeout denotes the maximum length of time (expressed in
   *          milliseconds) that is allowed to pass from the the call to
   *          getCurrentPosition() or watchPosition() until the corresponding
   *          PositionCallback is invoked
   * @param maximumAge indicates that the application is willing to accept a
   *          cached position whose age is no greater than the specified time in
   *          milliseconds
   * @return a new PositionOptions instance
   */
  public static final native PositionOptions getPositionOptions(
      boolean enableHighAccuracy, int timeout, int maximumAge) /*-{
    return {enableHighAccuracy: enableHighAccuracy,
      timeout: timeout,
      maximumAge: maximumAge};
  }-*/;

  /**
   * @return a hint that the application would like to receive the best possible
   *         results
   */
  public final native boolean isEnableHighAccuracy() /*-{
    return this.enableHighAccuracy;
  }-*/;

  /**
   * @return the maximum length of time (expressed in milliseconds) that is
   *         allowed to pass from the the call to getCurrentPosition() or
   *         watchPosition() until the corresponding PositionCallback is invoked
   */
  public final native int getTimeout() /*-{
    return this.timeout;
  }-*/;

  /**
   * @return the age in milliseconds of a cached position that the application
   *         is willing to accept
   */
  public final native int getMaximumAge() /*-{
    return this.maximumAge;
  }-*/;
}
