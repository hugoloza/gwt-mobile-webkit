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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the {@link Database} class.
 * 
 * @author bguijt
 */
public class DatabaseTest extends GWTTestCase {

  Database db = null;

  @Override
  public String getModuleName() {
    return "com.google.code.gwt.database.Html5Database";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    db = Database.openDatabase("gh5dt", "1.0", "GwtHtml5DatabaseTest", 5000);
    assertTrue(
        "Database is null, or Database is not supported! Database supported="
            + Database.isSupported() + ", UserAgent=" + getUserAgent(),
        db != null && Database.isSupported());
  }

  private final static native String getUserAgent() /*-{
    return navigator.userAgent;
  }-*/;

  public void testCreateTable() throws Exception {
    delayTestFinish(3000);
    db.transaction(new TransactionCallback() {
      public void onTransactionStart(SQLTransaction transaction) {
        transaction.executeSql("DROP TABLE IF EXISTS test", null);
        transaction.executeSql(
            "CREATE TABLE test ("
                + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "name VARCHAR(30) NOT NULL, length REAL DEFAULT 0,"
                + "dob DATE)", null);
        transaction.executeSql(
            "INSERT INTO test (name, length, dob) VALUES (?, ?, ?);",
            new Object[] {
                "Bart Guijt", 2.12,
                DateTimeFormat.getFormat("dd-MM-yyyy").parse("24-05-1974")});
        transaction.executeSql(
            "INSERT INTO test (name, length, dob) VALUES (?, ?, ?)",
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

  public void testTxStepsSequenceOk() throws Exception {
    delayTestFinish(3000);
    final List<Integer> steps = new Vector<Integer>();
    steps.add(0);
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        fail("Unexpected Exception caught! Performed steps: "
            + joinCollection(steps, ", ") + ", exception: " + e);
      }
    });
    db.transaction(new TransactionCallback() {
      public void onTransactionStart(SQLTransaction tx) {
        steps.add(2);
        tx.executeSql("DROP TABLE IF EXISTS test", null,
            new StatementCallback<GenericRow>() {
              public boolean onFailure(SQLTransaction transaction,
                  SQLError error) {
                fail("Database returned error at step #5! code="
                    + error.getCode() + ", msg=" + error.getMessage());
                return true;
              }

              public void onSuccess(SQLTransaction transaction,
                  SQLResultSet<GenericRow> resultSet) {
                steps.add(5);
              }
            });
        steps.add(3);
        tx.executeSql(
            "CREATE TABLE test ("
                + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "name VARCHAR(30) NOT NULL, length REAL DEFAULT 0,"
                + "dob DATE)", null, new StatementCallback<GenericRow>() {
              public boolean onFailure(SQLTransaction transaction,
                  SQLError error) {
                fail("Database returned error at step #6! code="
                    + error.getCode() + ", msg=" + error.getMessage());
                return true;
              }

              public void onSuccess(SQLTransaction transaction,
                  SQLResultSet<GenericRow> resultSet) {
                steps.add(6);
                transaction.executeSql("SELECT COUNT(*) FROM test", null,
                    new StatementCallback<GenericRow>() {
                      public boolean onFailure(SQLTransaction transaction,
                          SQLError error) {
                        fail("Database returned error at step #7! code="
                            + error.getCode() + ", msg=" + error.getMessage());
                        return true;
                      }

                      public void onSuccess(SQLTransaction transaction,
                          SQLResultSet<GenericRow> resultSet) {
                        steps.add(7);
                      }
                    });
              }
            });
        steps.add(4);
      }

      public void onTransactionFailure(SQLError error) {
        fail("Database returned error at step #8! code=" + error.getCode()
            + ", msg=" + error.getMessage());
      }

      public void onTransactionSuccess() {
        steps.add(8);
        // Check the sequence of the steps:
        assertEquals("Expecting 9 steps in the step sequence!",
            "0, 1, 2, 3, 4, 5, 6, 7, 8", joinCollection(steps, ", "));
        finishTest();
      }
    });
    steps.add(1);
  }

  public void testTxStepsSequenceWithFailure() throws Exception {
    delayTestFinish(3000);
    final List<Integer> steps = new Vector<Integer>();
    steps.add(0);
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        fail("Unexpected Exception caught! Performed steps: "
            + joinCollection(steps, ", ") + ", exception: " + e);
      }
    });
    db.transaction(new TransactionCallback() {
      public void onTransactionStart(SQLTransaction tx) {
        steps.add(2);
        tx.executeSql("DROP TABLE IF EXISTS test", null,
            new StatementCallback<GenericRow>() {
              public boolean onFailure(SQLTransaction transaction,
                  SQLError error) {
                fail("Database returned error at step #5! code="
                    + error.getCode() + ", msg=" + error.getMessage());
                return true;
              }

              public void onSuccess(SQLTransaction transaction,
                  SQLResultSet<GenericRow> resultSet) {
                steps.add(5);
              }
            });
        steps.add(3);
        // This will fail with a syntax error:
        tx.executeSql(
            "CREATE TABLE test ("
                + "id INTEGER NOT N*LL PRIMARY KEY AUTOINCREMENT,"
                + "name VARCHAR(30) NOT NULL, length REAL DEFAULT 0,"
                + "dob DATE)", null, new StatementCallback<GenericRow>() {
              public boolean onFailure(SQLTransaction transaction,
                  SQLError error) {
                // Expected!
                steps.add(6);
                return true;
              }

              public void onSuccess(SQLTransaction transaction,
                  SQLResultSet<GenericRow> resultSet) {
                transaction.executeSql("SELECT COUNT(*) FROM test", null,
                    new StatementCallback<GenericRow>() {
                      public boolean onFailure(SQLTransaction transaction,
                          SQLError error) {
                        fail("Not expected to run this "
                            + "SELECT COUNT(*) failure callback! code="
                            + error.getCode() + ", msg=" + error.getMessage());
                        return true;
                      }

                      public void onSuccess(SQLTransaction transaction,
                          SQLResultSet<GenericRow> resultSet) {
                        fail("Not expected to run this "
                            + "SELECT COUNT(*) success callback!");
                      }
                    });
              }
            });
        steps.add(4);
      }

      public void onTransactionFailure(SQLError error) {
        steps.add(7);
        // Check the sequence of the steps:
        assertEquals("Expecting 8 steps in the step sequence!",
            "0, 1, 2, 3, 4, 5, 6, 7", joinCollection(steps, ", "));
        System.out.println("Database execution steps: "
            + joinCollection(steps, ", "));
        finishTest();
      }

      public void onTransactionSuccess() {
        fail("Not expected to finish transaction successfully! Executed steps: "
            + joinCollection(steps, ", "));
      }
    });
    steps.add(1);
  }

  private String joinCollection(Collection<?> col, String join) {
    StringBuilder sb = new StringBuilder();
    for (Object o : col) {
      if (sb.length() > 0)
        sb.append(join);
      sb.append(o);
    }
    return sb.toString();
  }
}
