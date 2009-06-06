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

/**
 * Tests the Storage class.
 */
public class StorageTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.code.gwt.storage.Html5Storage";
  }

  public void testStorage() {
    final Storage localStorage = Storage.getLocalStorage();
    final Storage sessionStorage = Storage.getSessionStorage();

    assertNotNull(
        "This browser seems not to support Web Storage: no localStorage found!",
        localStorage);
    assertNotNull(
        "This browser seems not to support Web Storage: no sessionStorage found!",
        sessionStorage);

    localStorage.clear();
    sessionStorage.clear();

    localStorage.setItem("name", "Bart");
    localStorage.setItem("lastName", "Guijt");
    localStorage.setItem("email", "bart@guijt.me");
    localStorage.setItem("country", "The Netherlands");

    sessionStorage.setItem("name", "Pepijn");

    assertEquals("There must be a name key in localStorage!", "Bart",
        localStorage.getItem("name"));
    assertEquals("There must be a name key in sessionStorage!", "Pepijn",
        sessionStorage.getItem("name"));
  }
  
  public void testStorageRetention() {
    final Storage localStorage = Storage.getLocalStorage();
    final Storage sessionStorage = Storage.getSessionStorage();
    
    assertEquals("There must be a name key in localStorage!", "Bart",
        localStorage.getItem("name"));
    assertEquals("There must be a name key in sessionStorage!", "Pepijn",
        sessionStorage.getItem("name"));
  }

  public void testStorageHandler() {
    final Storage localStorage = Storage.getLocalStorage();
    final Storage sessionStorage = Storage.getSessionStorage();

    StorageEventHandler handler = new StorageEventHandler() {
      public int sessionCount = 0;
      public int localCount = 0;

      public void onStorageChange(StorageEvent event) {
        assertNotNull("The event may NOT be null!", event);
        if (localStorage.equals(event.getStorageArea())) {
          localCount++;
          addCheckpoint("local event: #" + localCount);
        } else if (sessionStorage.equals(event.getStorageArea())) {
          sessionCount++;
          addCheckpoint("session event: #" + sessionCount);
        } else {
          fail("StorageEvent is from an alien Storage!");
        }
        checkCounts();
      }

      private void checkCounts() {
        if (localCount == 4 && sessionCount == 1) {
          finishTest();
        }
      }
    };

    assertNotNull(
        "This browser seems not to support Web Storage: no localStorage found!",
        localStorage);
    assertNotNull(
        "This browser seems not to support Web Storage: no sessionStorage found!",
        sessionStorage);

    localStorage.clear();
    sessionStorage.clear();

    sessionStorage.addStorageEventHandler(handler);
    localStorage.addStorageEventHandler(handler);

    addCheckpoint("Handlers added");

    localStorage.setItem("name", "Bart");
    localStorage.setItem("lastName", "Guijt");
    localStorage.setItem("email", "bart@guijt.me");
    localStorage.setItem("country", "The Netherlands");

    sessionStorage.setItem("name", "Pepijn");

    // wait for all event handlers to finish...
    addCheckpoint("Waiting for finish...");

    delayTestFinish(500);
  }
}
