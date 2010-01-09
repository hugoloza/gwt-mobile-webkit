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
  public boolean isSupported() {
    return true;
  }

  @Override
  public Geolocation getGeolocation() {
    return factory.createGeolocation();
  }

  @Override
  public String getProviderName() {
    return "Google Gears API " + factory.getVersion();
  }
}
