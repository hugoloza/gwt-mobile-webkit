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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Implements the HTML5 Storage interface.
 * 
 * <p>
 * You can obtain a Storage by either invoking {@link #getLocalStorage()} or
 * {@link #getSessionStorage()}.
 * </p>
 * 
 * <p>
 * If Web Storage is NOT supported in the browser, these methods return
 * <code>null</code>.
 * </p>
 * 
 * @see <a href="http://www.w3.org/TR/webstorage/#storage-0">W3C Web Storage -
 *      Storage</a>
 * @see <a
 *      href="http://devworld.apple.com/safari/library/documentation/iPhone/Conceptual/SafariJSDatabaseGuide/Name-ValueStorage/Name-ValueStorage.html">Safari
 *      Client-Side Storage and Offline Applications Programming Guide -
 *      Key-Value Storage</a>
 * @see <a href="http://quirksmode.org/dom/html5.html#t00">Quirksmode.org -
 *      HTML5 Compatibility - Storage</a>
 * @author bguijt
 */
public final class Storage extends JavaScriptObject {

  private static final StorageImpl impl = GWT.create(StorageImpl.class);

  /**
   * This class can never be instantiated by itself.
   */
  protected Storage() {
  }

  /**
   * Returns <code>true</code> if the Storage API is supported on the running
   * platform.
   */
  public boolean isSupported() {
    return impl.isSupported();
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
   * @return the localStorage instance, or <code>null</code> if Web Storage is
   *         NOT supported.
   */
  public static Storage getLocalStorage() {
    return impl.getLocalStorage();
  };

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
   * @return the sessionStorage instance, or <code>null</code> if Web Storage is
   *         NOT supported.
   */
  public static Storage getSessionStorage() {
    return impl.getSessionStorage();
  }

  /**
   * Registers an event handler for StorageEvents.
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#the-storage-event">W3C Web
   *      Storage - the storage event</a>
   * @param handler
   */
  public static void addStorageEventHandler(StorageEventHandler handler) {
    impl.addStorageEventHandler(handler);
  }

  /**
   * De-registers an event handler for StorageEvents.
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#the-storage-event">W3C Web
   *      Storage - the storage event</a>
   * @param handler
   */
  public static void removeStorageEventHandler(StorageEventHandler handler) {
    impl.removeStorageEventHandler(handler);
  }

  /**
   * Returns the number of items in this Storage.
   * 
   * @return number of items in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-l">W3C Web
   *      Storage - Storage.length()</a>
   */
  public int getLength() {
    return impl.getLength(this);
  }

  /**
   * Returns the key at the specified index.
   * 
   * @param index the index of the key
   * @return the key at the specified index in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-key">W3C Web
   *      Storage - Storage.key(n)</a>
   */
  public String key(int index) {
    return impl.key(this, index);
  }

  /**
   * Returns the item in the Storage associated with the specified key.
   * 
   * @param key the key to a value in the Storage
   * @return the value associated with the given key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-getitem">W3C Web
   *      Storage - Storage.getItem(k)</a>
   */
  public String getItem(String key) {
    return impl.getItem(this, key);
  }

  /**
   * Sets the value in the Storage associated with the specified key to the
   * specified data.
   * 
   * @param key the key to a value in the Storage
   * @param data the value associated with the key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-setitem">W3C Web
   *      Storage - Storage.setItem(k,v)</a>
   */
  public void setItem(String key, String data) {
    impl.setItem(this, key, data);
  }

  /**
   * Removes the item in the Storage associated with the specified key.
   * 
   * @param key the key to a value in the Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-removeitem">W3C
   *      Web Storage - Storage.removeItem(k)</a>
   */
  public void removeItem(String key) {
    impl.removeItem(this, key);
  };

  /**
   * Removes all items in the Storage.
   * 
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-clear">W3C Web
   *      Storage - Storage.clear()</a>
   */
  public void clear() {
    impl.clear(this);
  }
}
