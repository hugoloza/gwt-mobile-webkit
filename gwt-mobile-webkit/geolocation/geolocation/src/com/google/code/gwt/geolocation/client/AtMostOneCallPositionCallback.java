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
 * {@link PositionCallback} wrapper which makes sure that a call to either
 * {@link #onSuccess(Position)} or {@link #onFailure(PositionError)} is executed
 * at most once.
 * 
 * @see GeolocationImplHtml5AndFirefox
 * 
 * @author bguijt
 */
public class AtMostOneCallPositionCallback implements PositionCallback {

  private PositionCallback wrappedCallback;
  private int callCount = 0;

  AtMostOneCallPositionCallback(PositionCallback wrappedCallback) {
    this.wrappedCallback = wrappedCallback;
  }

  /**
   * Calls the wrapped <code>onFailure()</code> only if it is the first call
   */
  public void onFailure(PositionError error) {
    if (callCount == 0) {
      callCount++;
      wrappedCallback.onFailure(error);
    }
  }

  /**
   * Calls the wrapped <code>onSuccess()</code> only if it is the first call
   */
  public void onSuccess(Position position) {
    if (callCount == 0) {
      callCount++;
      wrappedCallback.onSuccess(position);
    }
  }
}
