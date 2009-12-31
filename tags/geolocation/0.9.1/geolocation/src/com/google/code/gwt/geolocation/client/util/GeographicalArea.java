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

package com.google.code.gwt.geolocation.client.util;

/**
 * Represents a geographical area suitable for selection purposes.
 * 
 * @see <a href="http://www.zipcodeworld.com/docs/distance.pdf">Geographical
 *      Distance Calculations</a>
 * 
 * @author bguijt
 */
public class GeographicalArea {

  private double longitude;
  private double latitude;
  private int radius;
  private UnitType units;

  // Helper attributes:
  private double sinLatitude;
  private double cosLatitude;
  private double latitudeDiff;
  private double longitudeDiff;

  /**
   * Creates a new (circular) Geographical Area definition.
   * 
   * @param longitude the latitude of the center of the area
   * @param latitude the longitude of the center of the area
   * @param radius the radius of the area
   * @param units the unit type to apply to radius
   */
  public GeographicalArea(double longitude, double latitude, int radius,
      UnitType units) {
    this.longitude = longitude;
    this.latitude = latitude;
    this.radius = radius;
    this.units = units;

    // Set helper attributes:
    this.sinLatitude = Math.sin(Math.toRadians(latitude));
    this.cosLatitude = Math.cos(Math.toRadians(latitude));
    
    // Calculate long/lat square to speed up circular area selection:
    // See http://answers.google.com/answers/threadview?id=577262
    latitudeDiff = Math.PI * units.getEarthRadius() / radius * 180d;
    longitudeDiff = Math.cos(Math.toRadians(latitude)) * latitudeDiff;
  }

  /**
   * Returns the distance in terms of {@link #units} between the center of this
   * GeographicalArea and the specified long/lat location.
   * 
   * <p>
   * The distance is calculated using Great Circle Distance formula, using a
   * spherical interpretation of the shape of the Earth.
   * </p>
   * 
   * @param targetLongitude the longitude
   * @param targetLatitude the latitude
   * @return the distance in {@link #units}
   */
  public double getDistance(double targetLongitude, double targetLatitude) {
    return units.getEarthRadius()
        * Math.toDegrees(Math.acos(sinLatitude
            * Math.sin(Math.toRadians(targetLatitude)) + cosLatitude
            * Math.cos(Math.toRadians(targetLatitude))
            * Math.cos(Math.toRadians(targetLongitude - longitude))));
  }

  /**
   * Returns <code>true</code> if the specified long/lat location is within this
   * circular geographical area.
   * 
   * @param targetLongitude the longitude
   * @param targetLatitude the latitude
   * @return <code>true</code> if the location is in this area
   */
  public boolean inArea(double targetLongitude, double targetLatitude) {
    return getDistance(targetLongitude, targetLatitude) <= radius;
  }

  public double getMinLatitude() {
    return latitude - latitudeDiff;
  }
  
  public double getMaxLatitude() {
    return latitude + latitudeDiff;
  }
  
  public double getMinLongitude() {
    return longitude - longitudeDiff;
  }
  
  public double getMaxLongitude() {
    return longitude + longitudeDiff;
  }
}
