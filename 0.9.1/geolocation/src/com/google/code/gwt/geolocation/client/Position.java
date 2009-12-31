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
 * Represents a Geo position.
 * 
 * @author bguijt
 * @see <a href="http://www.w3.org/TR/geolocation-API/#position">W3C Geolocation
 *      API - Position interface</a>
 */
public class Position extends JavaScriptObject {

  protected Position() {
  }

  /**
   * Returns a set of geographic coordinates together with their associated
   * accuracy, as well as a set of other optional attributes such as altitude
   * and speed.
   * 
   * @return the coordinates
   * @see <a href="http://www.w3.org/TR/geolocation-API/#coords">W3C Geolocation
   *      API - Position.coords</a>
   */
  public final native Coordinates getCoords() /*-{
    return this.coords;
  }-*/;

  /**
   * Returns the time when the Position object was acquired.
   * 
   * @return the time when the Position object was acquired
   * @see <a href="http://www.w3.org/TR/geolocation-API/#timestamp">W3C
   *      Geolocation API - Position.timestamp</a>
   */
  public final native int getTimestamp() /*-{
    return this.timestamp;
  }-*/;
}
