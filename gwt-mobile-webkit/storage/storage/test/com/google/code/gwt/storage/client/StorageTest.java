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
 * Tests the {@link Storage} class.
 */
public class StorageTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.code.gwt.storage.Html5Storage";
  }

  public void testStorageSupported() {
    assertTrue("Storage API is NOT supported! User agent: " + getUserAgent(),
        Storage.isLocalStorageSupported() || Storage.isSessionStorageSupported());
  }

  public void testLocalStorage() {
    doStorage(Storage.getLocalStorage());
  }

  public void testSessionStorage() {
    doStorage(Storage.getSessionStorage());
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
    // Wait for 10 seconds to time-out the test:
    delayTestFinish(10000);

    final Storage localStorage = Storage.getLocalStorage();
    final Storage sessionStorage = Storage.getSessionStorage();

    StorageEventHandler handler = new StorageEventHandler() {
      public int sessionCount = 0;
      public int localCount = 0;

      public void onStorageChange(StorageEvent event) {
        assertNotNull("The event may NOT be null!", event);
        if (localStorage.equals(event.getStorageArea())) {
          localCount++;
        } else if (sessionStorage.equals(event.getStorageArea())) {
          sessionCount++;
        } else {
          fail("StorageEvent is neither from the SessionStorage"
              + " nor from the LocalStorage!");
        }

        System.out.println("#localStorage events: " + localCount);
        System.out.println("#sessionStorage events: " + sessionCount);

        if (localCount == 4 && sessionCount == 1) {
          finishTest();
        }
      }
    };

    localStorage.clear();
    sessionStorage.clear();

    Storage.addStorageEventHandler(handler);

    localStorage.setItem("name", "Bart");
    localStorage.setItem("lastName", "Guijt");
    localStorage.setItem("email", "bart@guijt.me");
    localStorage.setItem("country", "The Netherlands");

    sessionStorage.setItem("name", "Pepijn");
  }

  public void doStorage(Storage s) {
    assertNotNull("No support for Web Storage!", s);

    s.clear();
    assertEquals("There should be NO items in the Storage after clear()!", 0, s.getLength());
    
    s.setItem("name", "Bart");
    s.setItem("lastName", "Guijt");
    s.setItem("email", "bart@guijt.me");
    s.setItem("country", "The Netherlands");

    assertEquals("There should be 4 items in the Storage!", 4, s.getLength());
    assertEquals("There must be a name key in localStorage!", "Bart",
        s.getItem("name"));
  }
  
  private final static native String getUserAgent() /*-{
    return navigator.userAgent;
  }-*/;
}
