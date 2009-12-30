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
 * This enumeration is used to qualify distances between two
 * geo locations.
 *
 * @see GeographicalArea#GeographicalArea(double, double, int, UnitType)
 * 
 * @author bguijt
 */
public enum UnitType {

  METERS(6378137d),
  KILOMETERS(6378.137d),
  MILES((25000d / 360d) * 180d / Math.PI),
  NAUTICALMILES(60d * 180d / Math.PI);
  
  private double earthRadius;
  
  private UnitType(double earthRadius) {
    this.earthRadius = earthRadius;
  }

  /**
   * Returns the amount of units making up the (mean) radius of the earth
   */
  public double getEarthRadius() {
    return earthRadius;
  }
}
