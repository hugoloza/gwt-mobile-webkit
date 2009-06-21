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

package com.google.code.gwt.storage.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

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

  protected static final List<StorageEventHandler> storageEventHandlers = new ArrayList<StorageEventHandler>();
  protected static JavaScriptObject jsHandler;

  /**
   * This class can never be instantiated by itself.
   */
  protected StorageImpl() {
  }

  /**
   * Returns a Local Storage.
   * 
   * <p>
   * The returned storage is associated with the <a
   * href="http://www.w3.org/TR/html5/browsers.html#origin">origin</a> of the
   * Document.
   * </p>
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-localstorage">W3C Web
   *      Storage - localStorage</a>
   * @return the localStorage instance.
   */
  public native Storage getLocalStorage() /*-{
    return $wnd.localStorage;
  }-*/;

  /**
   * Returns a Session Storage.
   * 
   * <p>
   * The returned storage is associated with the current <a href=
   * "http://www.w3.org/TR/html5/browsers.html#top-level-browsing-context"
   * >top-level browsing context</a>.
   * </p>
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-sessionstorage">W3C Web
   *      Storage - sessionStorage</a>
   * @return the sessionStorage instance.
   */
  public native Storage getSessionStorage() /*-{
    return $wnd.sessionStorage;
  }-*/;

  /**
   * Registers an event handler for StorageEvents.
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#the-storage-event">W3C Web
   *      Storage - the storage event</a>
   * @param handler
   */
  public void addStorageEventHandler(StorageEventHandler handler) {
    storageEventHandlers.add(handler);
    if (storageEventHandlers.size() == 1) {
      addStorageEventHandler0();
    }
  }

  protected native void addStorageEventHandler0() /*-{
    @com.google.code.gwt.storage.client.StorageImpl::jsHandler = function(event) {
      @com.google.code.gwt.storage.client.StorageImpl::handleStorageEvent(Lcom/google/code/gwt/storage/client/StorageEvent;) (event);
    };
    $doc.body.addEventListener("storage", @com.google.code.gwt.storage.client.StorageImpl::jsHandler, false);
  }-*/;

  protected static final void handleStorageEvent(StorageEvent event) {
    for (int i = 0; i < storageEventHandlers.size(); i++) {
      StorageEventHandler handler = storageEventHandlers.get(i);
      handler.onStorageChange(event);
    }
  }

  public void removeStorageEventHandler(StorageEventHandler handler) {
    storageEventHandlers.remove(handler);
    if (storageEventHandlers.size() == 0) {
      removeStorageEventHandler0();
    }
  }

  protected native void removeStorageEventHandler0() /*-{
    $doc.body.removeEventListener("storage", @com.google.code.gwt.storage.client.StorageImpl::jsHandler, false);
  }-*/;

  /**
   * Returns the number of items in this Storage.
   * 
   * @return number of items in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-l">W3C Web
   *      Storage - Storage.length()</a>
   */
  public native int getLength(Storage storage) /*-{
    return storage.length;
  }-*/;

  /**
   * Returns the key at the specified index.
   * 
   * @param index the index of the key
   * @return the key at the specified index in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-key">W3C Web
   *      Storage - Storage.key(n)</a>
   */
  public native String key(Storage storage, int index) /*-{
    return storage.key(index);
  }-*/;

  /**
   * Returns the item in the Storage associated with the specified key.
   * 
   * @param key the key to a value in the Storage
   * @return the value associated with the given key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-getitem">W3C Web
   *      Storage - Storage.getItem(k)</a>
   */
  public native String getItem(Storage storage, String key) /*-{
    return storage.getItem(key);
  }-*/;

  /**
   * Sets the value in the Storage associated with the specified key to the
   * specified data.
   * 
   * @param key the key to a value in the Storage
   * @param data the value associated with the key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-setitem">W3C Web
   *      Storage - Storage.setItem(k,v)</a>
   */
  public native void setItem(Storage storage, String key, String data) /*-{
    storage.setItem(key, data);
  }-*/;

  /**
   * Removes the item in the Storage associated with the specified key.
   * 
   * @param key the key to a value in the Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-removeitem">W3C
   *      Web Storage - Storage.removeItem(k)</a>
   */
  public native void removeItem(Storage storage, String key) /*-{
    storage.removeItem(key);
  }-*/;

  /**
   * Removes all items in the Storage.
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-clear">W3C Web
   *      Storage - Storage.clear()</a>
   */
  public native void clear(Storage storage) /*-{
    storage.clear();
  }-*/;
}
