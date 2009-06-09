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
 * Represents a position Coordinates object.
 * 
 * @author bguijt
 * @see <a href="http://www.w3.org/TR/geolocation-API/#coordinates">W3C
 *      Geolocation API - Coordinates interface</a>
 */
public class Coordinates extends JavaScriptObject {

  protected Coordinates() {
  }

  /**
   * Returns the latitude in decimal degrees.
   * 
   * @return the latitude in decimal degrees
   * @see <a href="http://www.w3.org/TR/geolocation-API/#lat">W3C Geolocation
   *      API - Coordinates.latitude</a>
   */
  public final native double getLatitude() /*-{
    return this.latitude;
  }-*/;

  /**
   * Returns the longitude in decimal degrees.
   * 
   * @return the longitude in decimal degrees
   * @see <a href="http://www.w3.org/TR/geolocation-API/#lon">W3C Geolocation
   *      API - Coordinates.longitude</a>
   */
  public final native double getLongitude() /*-{
    return this.longitude;
  }-*/;

  /**
   * Returns the height of the position specified in meters.
   * 
   * @return the height of the position specified in meters
   * @see <a href="http://www.w3.org/TR/geolocation-API/#altitude">W3C
   *      Geolocation API - Coordinates.altitude</a>
   */
  public final native double getAltitude() /*-{
    return this.altitude;
  }-*/;

  /**
   * Returns the accuracy level of the position specified in meters.
   * 
   * @return the accuracy level of the position specified in meters
   * @see <a href="http://www.w3.org/TR/geolocation-API/#accuracy">W3C
   *      Geolocation API - Coordinates.accuracy</a>
   */
  public final native double getAccuracy() /*-{
    return this.accuracy;
  }-*/;

  /**
   * Returns the accuracy level of the altitude specified in meters.
   * 
   * @return the accuracy level of the altitude specified in meters. If no value
   *         can be calculated, returns 0.
   * @see <a href="http://www.w3.org/TR/geolocation-API/#altitude-accuracy">W3C
   *      Geolocation API - Coordinates.altitudeAccuracy</a>
   */
  public final native double getAltitudeAccuracy() /*-{
    return this.altitudeAccuracy;
  }-*/;

  /**
   * Returns the heading specified in degrees counting clockwise relative to the
   * true north.
   * 
   * @return the heading specified in degrees counting clockwise relative to the
   *         true north. If no value can be calculated, returns 0.
   * @see <a href="http://www.w3.org/TR/geolocation-API/#heading">W3C
   *      Geolocation API - Coordinates.heading</a>
   */
  public final native double getHeading() /*-{
    return this.heading;
  }-*/;

  /**
   * Returns the speed of the hosting device in meters per second.
   * 
   * @return the speed of the hosting device in meters per second. If no value
   *         can be calculated, returns 0.
   * @see <a href="http://www.w3.org/TR/geolocation-API/#speed">W3C Geolocation
   *      API - Coordinates.speed</a>
   */
  public final native double getSpeed() /*-{
    return this.speed;
  }-*/;
}
