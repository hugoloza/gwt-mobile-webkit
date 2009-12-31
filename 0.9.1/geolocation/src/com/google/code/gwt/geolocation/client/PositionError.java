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
 * Represents an error encountered when asking the (current) {@link Position}.
 * 
 * @author bguijt
 * @see <a href="http://www.w3.org/TR/geolocation-API/#position-error">W3C
 *      Geolocation API - PositionError interface</a>
 */
public class PositionError extends JavaScriptObject {

  public static final int UNKNOWN_ERROR = 0;
  public static final int PERMISSION_DENIED = 1;
  public static final int POSITION_UNAVAILABLE = 2;
  public static final int TIMEOUT = 3;

  protected PositionError() {
  }

  /**
   * Returns the error code. Must be one of {@link #UNKNOWN_ERROR},
   * {@link #PERMISSION_DENIED}, {@link #POSITION_UNAVAILABLE} or
   * {@link #TIMEOUT}.
   * 
   * @return the error code
   * @see <a href="http://www.w3.org/TR/geolocation-API/#code">W3C Geolocation
   *      API - PositionError.code</a>
   */
  public final native int getCode() /*-{
    return this.code;
  }-*/;

  /**
   * Returns the message describing the details of the error encountered.
   * 
   * @return the error message
   * @see <a href="http://www.w3.org/TR/geolocation-API/#message">W3C
   *      Geolocation API - PositionError.message</a>
   */
  public final native String getMessage() /*-{
    return this.message;
  }-*/;
}
