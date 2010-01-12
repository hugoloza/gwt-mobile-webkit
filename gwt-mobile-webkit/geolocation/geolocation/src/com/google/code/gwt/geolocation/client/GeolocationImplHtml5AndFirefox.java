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
 * Special GeolocationImpl for Firefox 3.5/HTML5.
 * 
 * <p>
 * Sometimes, a Geolocation callback is fired *twice*. This class is designed to
 * mitigate that flaw.
 * </p>
 * 
 * @see <a
 *      href="http://code.google.com/p/gwt-mobile-webkit/issues/detail?id=10">Issue
 *      #10: Firefox calls Geolocation callbacks twice (sometimes)</a>
 * 
 * @author bguijt
 */
public class GeolocationImplHtml5AndFirefox extends GeolocationImpl {

  /**
   * Wraps the specified <code>callback</code> in an
   * {@link AtMostOneCallPositionCallback} instance, and passes that to the
   * getCurrentPosition() call.
   */
  @Override
  public void getCurrentPosition(Geolocation geo, PositionCallback callback,
      PositionOptions options) {
    super.getCurrentPosition(geo, new AtMostOneCallPositionCallback(callback),
        options);
  }
}
