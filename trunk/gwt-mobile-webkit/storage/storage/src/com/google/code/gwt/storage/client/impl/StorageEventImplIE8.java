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

/**
 * IE8-specific implementation of a StorageEvent.
 * 
 * @author bguijt
 */
public class StorageEventImplIE8 extends StorageEventImpl {

  @Override
  public String getKey(StorageEvent se) {
    return StorageImplIE8.eventKey;
  }

  @Override
  public String getOldValue(StorageEvent se) {
    return StorageImplIE8.eventOldValue;
  }

  @Override
  public String getNewValue(StorageEvent se) {
    return StorageImplIE8.eventNewValue;
  }

  @Override
  public Storage getStorageArea(StorageEvent se) {
    return StorageImplIE8.eventSource;
  }

  @Override
  public String getUrl(StorageEvent se) {
    return StorageImplIE8.eventUrl;
  }
}
