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

import java.util.ArrayList;
import java.util.List;

import com.google.code.gwt.storage.client.Storage;
import com.google.code.gwt.storage.client.StorageEvent;
import com.google.code.gwt.storage.client.StorageEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

/**
 * This is the HTML5 Storage implementation according to the <a
 * href="http://www.w3.org/TR/webstorage/#storage-0">standard
 * recommendation</a>.
 * 
 * <p>
 * Never use this class directly, instead use {@link Storage}.
 * </p>
 * 
 * @see <a href="http://www.w3.org/TR/webstorage/#storage-0">W3C Web Storage -
 *      Storage</a>
 * 
 * @author bguijt
 */
public class StorageImpl {

  public static final String LOCAL_STORAGE = "localStorage";
  public static final String SESSION_STORAGE = "sessionStorage";
  
  protected static List<StorageEventHandler> storageEventHandlers;
  protected static JavaScriptObject jsHandler;

  /**
   * This class can never be instantiated by itself.
   */
  protected StorageImpl() {
  }

  /**
   * Returns <code>true</code> if localStorage is supported
   */
  public native boolean isLocalStorageSupported() /*-{
    return typeof $wnd.localStorage != "undefined";
  }-*/;

  /**
   * Returns <code>true</code> if sessionStorage is supported
   */
  public native boolean isSessionStorageSupported() /*-{
    return typeof $wnd.sessionStorage != "undefined";
  }-*/;

  /**
   * Registers an event handler for StorageEvents.
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#the-storage-event">W3C Web
   *      Storage - the storage event</a>
   * @param handler
   */
  public void addStorageEventHandler(StorageEventHandler handler) {
    getStorageEventHandlers().add(handler);
    if (storageEventHandlers.size() == 1) {
      addStorageEventHandler0();
    }
  }
  
  /**
   * Returns <code>true</code> if at least one StorageEvent handler is registered,
   * <code>false</code> otherwise.
   */
  protected static boolean hasStorageEventHandlers() {
    return storageEventHandlers != null && !storageEventHandlers.isEmpty();
  }
  
  /**
   * Returns the {@link List} of {@link StorageEventHandler}s registered,
   * which is never <code>null</code>.
   */
  protected List<StorageEventHandler> getStorageEventHandlers() {
    if (storageEventHandlers == null) {
      storageEventHandlers = new ArrayList<StorageEventHandler>();
    }
    return storageEventHandlers;
  }

  protected native void addStorageEventHandler0() /*-{
    @com.google.code.gwt.storage.client.impl.StorageImpl::jsHandler = function(event) {
      @com.google.code.gwt.storage.client.impl.StorageImpl::handleStorageEvent(Lcom/google/code/gwt/storage/client/StorageEvent;)(event);
    };
    $wnd.addEventListener("storage", @com.google.code.gwt.storage.client.impl.StorageImpl::jsHandler, false);
  }-*/;

  protected static final void handleStorageEvent(StorageEvent event) {
    if (!hasStorageEventHandlers()) {
      return;
    }
    UncaughtExceptionHandler ueh = GWT.getUncaughtExceptionHandler();
    for (StorageEventHandler handler : storageEventHandlers) {
      if (ueh != null) {
        try {
          handler.onStorageChange(event);
        } catch (Throwable t) {
          ueh.onUncaughtException(t);
        }
      } else {
        handler.onStorageChange(event);
      }
    }
  }

  public void removeStorageEventHandler(StorageEventHandler handler) {
    getStorageEventHandlers().remove(handler);
    if (storageEventHandlers.isEmpty()) {
      removeStorageEventHandler0();
    }
  }

  protected native void removeStorageEventHandler0() /*-{
    $wnd.removeEventListener("storage", @com.google.code.gwt.storage.client.impl.StorageImpl::jsHandler, false);
  }-*/;

  /**
   * Returns the number of items in this Storage.
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @return number of items in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-l">W3C Web
   *      Storage - Storage.length()</a>
   */
  public native int getLength(String storage) /*-{
    return $wnd[storage].length;
  }-*/;

  /**
   * Returns the key at the specified index.
   * 
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param index the index of the key
   * @return the key at the specified index in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-key">W3C Web
   *      Storage - Storage.key(n)</a>
   */
  public native String key(String storage, int index) /*-{
    return $wnd[storage].key(index);
  }-*/;

  /**
   * Returns the item in the Storage associated with the specified key.
   * 
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param key the key to a value in the Storage
   * @return the value associated with the given key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-getitem">W3C Web
   *      Storage - Storage.getItem(k)</a>
   */
  public native String getItem(String storage, String key) /*-{
    return $wnd[storage].getItem(key);
  }-*/;

  /**
   * Sets the value in the Storage associated with the specified key to the
   * specified data.
   * 
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param key the key to a value in the Storage
   * @param data the value associated with the key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-setitem">W3C Web
   *      Storage - Storage.setItem(k,v)</a>
   */
  public native void setItem(String storage, String key, String data) /*-{
    $wnd[storage].setItem(key, data);
  }-*/;

  /**
   * Removes the item in the Storage associated with the specified key.
   * 
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param key the key to a value in the Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-removeitem">W3C
   *      Web Storage - Storage.removeItem(k)</a>
   */
  public native void removeItem(String storage, String key) /*-{
    $wnd[storage].removeItem(key);
  }-*/;

  /**
   * Removes all items in the Storage.
   * 
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-clear">W3C Web
   *      Storage - Storage.clear()</a>
   */
  public native void clear(String storage) /*-{
    $wnd[storage].clear();
  }-*/;
}
