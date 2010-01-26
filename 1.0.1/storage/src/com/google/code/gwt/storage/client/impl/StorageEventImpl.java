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

import com.google.code.gwt.storage.client.Storage;
import com.google.code.gwt.storage.client.StorageEvent;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Sefault implementation of a StorageEvent according to W3C spec.
 * 
 * @author bguijt
 * 
 * @see <a href="http://quirksmode.org/dom/html5.html#t10">Quirksmode.org -
 *      HTML5 Storage</a>
 */
public class StorageEventImpl {

  protected StorageEventImpl() {
  }

  /**
   * Returns the key being changed.
   * 
   * @return the key being changed
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-key">W3C
   *      Web Storage - StorageEvent.key</a>
   */
  public native String getKey(StorageEvent se) /*-{
    return se.key;
  }-*/;

  /**
   * Returns the old value of the key being changed.
   * 
   * @return the old value of the key being changed
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-oldvalue">W3C
   *      Web Storage - StorageEvent.oldValue</a>
   */
  public native String getOldValue(StorageEvent se) /*-{
    return se.oldValue;
  }-*/;

  /**
   * Returns the new value of the key being changed.
   * 
   * @return the new value of the key being changed
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-newvalue">W3C
   *      Web Storage - StorageEvent.newValue</a>
   */
  public native String getNewValue(StorageEvent se) /*-{
    return se.newValue;
  }-*/;

  /**
   * Returns the address of the document whose key changed.
   * 
   * @return the address of the document whose key changed
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-url">W3C
   *      Web Storage - StorageEvent.url</a>
   */
  public native String getUrl(StorageEvent se) /*-{
    return se.url;
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
  public native JavaScriptObject getSource(StorageEvent se) /*-{
    return se.source;
  }-*/;

  /**
   * Returns the {@link Storage} object that was affected.
   * 
   * @return the {@link Storage} object that was affected
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-storagearea">W3C
   *      Web Storage - StorageEvent.storageArea</a>
   */
  public native Storage getStorageArea(StorageEvent se) /*-{
    return se.storageArea;
  }-*/;
}
