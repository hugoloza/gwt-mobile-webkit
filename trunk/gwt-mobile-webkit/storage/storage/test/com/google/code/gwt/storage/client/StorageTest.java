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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.code.gwt.storage.client.Storage;

/**
 * Tests the Storage class.
 */
public class StorageTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.code.gwt.storage.Html5Storage";
  }

  public void testStorage() {
    Storage t = new Storage();
  }
}
