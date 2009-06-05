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

package com.google.code.gwt.database.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the {@link Database} class.
 * 
 * @author bguijt
 */
public class DatabaseTest extends GWTTestCase {

  Database db = null;

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
   */
  @Override
  public String getModuleName() {
    return "com.google.code.gwt.database.Html5Database";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    db = Database.openDatabase("gwtdb", "1.0", "GWT DB", 5000);
    assertTrue(
        "Database is null, or openDatabase is not supported! openDatabase() exists="
            + hasOpenDatabase() + ", UserAgent=" + getUserAgent(), db != null
            && hasOpenDatabase());
  }

  private final static native String getUserAgent() /*-{
    return navigator.userAgent;
  }-*/;

  private final static native boolean hasOpenDatabase() /*-{
    return $wnd.openDatabase ? true : false;
  }-*/;

  public void testCreateTable() throws Exception {
    delayTestFinish(3000);
    db.transaction(new TransactionCallback() {
      public void onTransactionStart(SQLTransaction transaction) {
        transaction.executeSql("DROP TABLE IF EXISTS test;", null);
        transaction.executeSql("CREATE TABLE test ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "name VARCHAR(30) NOT NULL," + "length REAL DEFAULT 0,"
            + "dob DATE);", null);
        transaction.executeSql(
            "INSERT INTO test (name, length, dob) VALUES (?, ?, ?);",
            new Object[] {
                "Bart Guijt", 2.12,
                DateTimeFormat.getFormat("dd-MM-yyyy").parse("24-05-1974")});
        transaction.executeSql(
            "INSERT INTO test (name, length, dob) VALUES (?, ?, ?);",
            new Object[] {
                "Pioneer Kuro 50\"", 50 * 2.54,
                DateTimeFormat.getFormat("dd-MM-yyyy").parse("12-01-2009")});
      }
      public void onTransactionFailure(SQLError error) {
        fail(error.getMessage());
      }
      public void onTransactionSuccess() {
        finishTest();
      }
    });
  }
}
