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

  public native boolean isSupported() /*-{
    return typeof $wnd.google != "undefined"
        && typeof $wnd.google.gears != "undefined"
        && typeof $wnd.google.gears.factory != "undefined";
  }-*/;

  public native Geolocation getGeolocation() /*-{
    return $wnd.google.gears.factory.create("beta.geolocation");
  }-*/;
}
