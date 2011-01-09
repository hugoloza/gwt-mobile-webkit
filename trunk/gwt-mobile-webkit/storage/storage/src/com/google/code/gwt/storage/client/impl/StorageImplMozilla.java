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

package com.google.code.gwt.storage.client.impl;

import com.google.code.gwt.storage.client.StorageEvent;
import com.google.code.gwt.storage.client.StorageEventHandler;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Mozilla-specific implementation of a Storage.
 * 
 * <p>
 * Implementation of StorageEvents is incomplete for Mozilla. This class amends
 * the properties consistently with W3C's StorageEvent.
 * </p>
 * 
 * @author bguijt
 */
public class StorageImplMozilla extends StorageImpl {

  @Override
  public native void setItem(String storage, String key, String data) /*-{
    var oldValue = storage[key];
    $wnd[storage].setItem(key, data);
    @com.google.code.gwt.storage.client.impl.StorageImplMozilla::fireStorageEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;) (key, oldValue, data, storage);
  }-*/;

  @Override
  public native void removeItem(String storage, String key) /*-{
    var oldValue = storage[key];
    $wnd[storage].removeItem(key);
    @com.google.code.gwt.storage.client.impl.StorageImplMozilla::fireStorageEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;) (key, oldValue, null, storage);
  }-*/;

  @Override
  public native void clear(String storage) /*-{
    $wnd[storage].clear();
    @com.google.code.gwt.storage.client.impl.StorageImplMozilla::fireStorageEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;) ("", null, null, storage);
  }-*/;

  @SuppressWarnings("unused")
  private static final void fireStorageEvent(String key, String oldValue,
      String newValue, String storage) {
    if (hasStorageEventHandlers()) {
      StorageEvent se = createStorageEvent(key, oldValue, newValue, storage);
      handleStorageEvent(se);
    }
  }

  private static final native StorageEvent createStorageEvent(String key,
      String oldValue, String newValue, String storage) /*-{
    return {
        key: key,
        oldValue: oldValue,
        newValue: newValue,
        storageArea: $wnd[storage],
        source: $wnd,
        url: $wnd.location.href
      };
  }-*/;

  @Override
  public void addStorageEventHandler(StorageEventHandler handler) {
    getStorageEventHandlers().add(handler);
  }
  
  @Override
  public void removeStorageEventHandler(StorageEventHandler handler) {
    getStorageEventHandlers().remove(handler);
  }
}
