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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * Represent a Storage Event.
 * 
 * <p>
 * A Storage Event is fired when a storage area changes, as described in these
 * two sections (for <a
 * href="http://www.w3.org/TR/webstorage/#sessionStorageEvent">session
 * storage</a>, for <a
 * href="http://www.w3.org/TR/webstorage/#localStorageEvent">local storage</a>).
 * </p>
 * 
 * @author bguijt
 * @see StorageEventHandler
 * @see <a href="http://www.w3.org/TR/webstorage/#event-definition">W3C Web
 *      Storage - StorageEvent</a>
 * @see <a
 *      href="https://developer.apple.com/safari/library/documentation/AppleApplications/Reference/WebKitDOMRef/StorageEvent_idl/Classes/StorageEvent/index.html">Safari
 *      StorageEvent reference</a>
 */
public class StorageEvent extends JavaScriptObject {

  protected StorageEvent() {
  }

  /**
   * Returns the key being changed.
   * 
   * @return the key being changed
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-key">W3C
   *      Web Storage - StorageEvent.key</a>
   */
  public final native String getKey() /*-{
    return this.key;
  }-*/;

  /**
   * Returns the old value of the key being changed.
   * 
   * @return the old value of the key being changed
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-oldvalue">W3C
   *      Web Storage - StorageEvent.oldValue</a>
   */
  public final native String getOldValue() /*-{
    return this.oldValue;
  }-*/;

  /**
   * Returns the new value of the key being changed.
   * 
   * @return the new value of the key being changed
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-newvalue">W3C
   *      Web Storage - StorageEvent.newValue</a>
   */
  public final native String getNewValue() /*-{
    return this.newValue;
  }-*/;

  /**
   * Returns the address of the document whose key changed.
   * 
   * @return the address of the document whose key changed
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-url">W3C
   *      Web Storage - StorageEvent.url</a>
   */
  public final native String getUrl() /*-{
    return this.url;  // Mobile Safari: 'uri' ?
  }-*/;

  /**
   * Returns the WindowProxy object of the browsing context of the document
   * whose key changed.
   * 
   * @return the WindowProxy object of the browsing context of the document
   *         whose key changed
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-source">W3C
   *      Web Storage - StorageEvent.source</a>
   */
  public final native Window getSource() /*-{
    return this.source;
  }-*/;

  /**
   * Returns the {@link Storage} object that was affected.
   * 
   * @return the {@link Storage} object that was affected
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-storagearea">W3C
   *      Web Storage - StorageEvent.storageArea</a>
   */
  public final native Storage getStorageArea() /*-{
    return this.storageArea;
  }-*/;
}
