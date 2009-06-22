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
 * Represents an Event handler for {@link StorageEvent}s.
 * 
 * <p>
 * Apply your StorageEventHandler using
 * {@link Storage#addStorageEventHandler(StorageEventHandler)}.
 * </p>
 * 
 * @author bguijt
 * @see StorageEvent
 */
public interface StorageEventHandler {

  /**
   * Invoked when a StorageEvent is fired.
   * 
   * @param event the fired StorageEvent
   * @see <a href="http://www.w3.org/TR/webstorage/#event-storage">W3C Web
   *      Storage - Storage Event</a>
   */
  void onStorageChange(StorageEvent event);
}
