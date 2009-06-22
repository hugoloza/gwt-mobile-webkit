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

/**
 * Safari-specific implementation of StorageEvent.
 * 
 * Difference with W3 spec: uses 'uri' attribute instead of 'url'.
 * 
 * @see <a href="http://quirksmode.org/dom/html5.html#t111">Quirksmode.org -
 *      HTML5 Storage</a>
 * 
 * @author bguijt
 */
public class StorageEventImplSafari extends StorageEventImpl {

  @Override
  public native String getUrl(StorageEvent se) /*-{
    return se.uri;
  }-*/;
}
