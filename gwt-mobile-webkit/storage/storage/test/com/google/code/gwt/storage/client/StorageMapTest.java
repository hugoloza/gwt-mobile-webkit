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

package com.google.code.gwt.storage.client;

import java.util.Map;


/**
 * Tests the {@link StorageMap} class.
 * 
 * @author bguijt
 */
public class StorageMapTest extends MapInterfaceTest<String, String> {

  private boolean createSessionStorageMap = false;
  
  public StorageMapTest() {
    super(false, false, true, true, true);
  }

  /* (non-Javadoc)
   * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
   */
  @Override
  public String getModuleName() {
    return "com.google.code.gwt.storage.Html5Storage";
  }

  @Override
  protected String getKeyNotInPopulatedMap()
      throws UnsupportedOperationException {
    return "nonExistingKey";
  }

  @Override
  protected String getValueNotInPopulatedMap()
      throws UnsupportedOperationException {
    return "nonExistingValue";
  }

  @Override
  protected Map<String, String> makeEmptyMap()
      throws UnsupportedOperationException {
    Storage s = createStorage();
    s.clear();
    return new StorageMap(s);
  }

  @Override
  protected Map<String, String> makePopulatedMap()
      throws UnsupportedOperationException {
    Storage s = createStorage();
    s.clear();
    s.setItem("one", "January");
    s.setItem("two", "February");
    s.setItem("three", "March");
    s.setItem("four", "April");
    s.setItem("five", "May");
    return new StorageMap(s);
  }

  private Storage createStorage() {
    // Weird huh?
    // Needed to thwart some tests which need different Maps.
    createSessionStorageMap = !createSessionStorageMap;
    if (createSessionStorageMap) {
      return Storage.getSessionStorage();
    }
    return Storage.getLocalStorage();
  }
}
