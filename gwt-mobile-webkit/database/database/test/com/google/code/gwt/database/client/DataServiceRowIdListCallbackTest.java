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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.code.gwt.database.client.service.Connection;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.RowIdListCallback;
import com.google.code.gwt.database.client.service.Select;
import com.google.code.gwt.database.client.service.Update;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the {@link DataService} API with the {@link RowIdListCallback}.
 * 
 * @author bguijt
 */
public class DataServiceRowIdListCallbackTest extends GWTTestCase {

  /**
   * Class to help insert records over a collection
   */
  public class TestRecord {
    private int i;
    private String text;
    private Number number;
    private Double real;
    private Object none;
    public TestRecord(int i, String text, Number number, Double real,
        Object none) {
      this.i = i;
      this.text = text;
      this.number = number;
      this.real = real;
      this.none = none;
    }
    public int getI() {
      return i;
    }
    public String getText() {
      return text;
    }
    public Number getNumber() {
      return number;
    }
    public Double getReal() {
      return real;
    }
    public Object getNone() {
      return none;
    }
  }
  
  @Connection(name = "gh5dt", version = "1.0", description = "GwtHtml5DatabaseTest", maxsize = 5000)
  public interface TestRowIdListCallbackDataService extends DataService {

    @Update("CREATE TABLE IF NOT EXISTS testtable ("
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "integervalue INTEGER, " + "textvalue TEXT, "
        + "numericvalue NUMERIC, " + "realvalue REAL, " + "nonevalue NONE)")
    void create(VoidCallback callback);

    @Update("INSERT INTO testtable (integervalue, textvalue, numericvalue, realvalue, nonevalue) VALUES ({i}, {t}, {n}, {d}, {o})")
    void insertRecord(int i, String t, Number n, Double d, Object o,
        RowIdListCallback callback);

    @Update("INSERT INTO nonexistingtable (integervalue, textvalue, numericvalue, realvalue, nonevalue) VALUES ({i}, {t}, {n}, {d}, {o})")
    void insertRecordFail(int i, String t, Number n, Double d, Object o,
        RowIdListCallback callback);

    @Update(sql="INSERT INTO testtable (integervalue, textvalue, numericvalue, realvalue, nonevalue) VALUES ({_.getI()}, {_.getText()}, {_.getNumber()}, {_.getReal()}, {_.getNone()})",
            foreach="records")
    void insertRecords(Collection<TestRecord> records,
        RowIdListCallback callback);
    
    @Select("SELECT integervalue, textvalue, numericvalue, realvalue, nonevalue FROM testtable WHERE id IN ({ids})")
    void getRecords(Collection<Integer> ids, ListCallback<GenericRow> callback);
  }

  private TestRowIdListCallbackDataService service = null;

  @Override
  public String getModuleName() {
    return "com.google.code.gwt.database.Html5Database";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    service = GWT.create(TestRowIdListCallbackDataService.class);
    assertNotNull("GWT.create() of a DataService may not return null!", service);
  }

  public void testCreate() throws Exception {
    delayTestFinish(3000);
    service.create(new VoidCallback() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }

      public void onSuccess() {
        finishTest();
      }
    });
  }

  public void testInsertRecordResultSet() throws Exception {
    delayTestFinish(3000);
    service.insertRecord(100, "Testing string values", 1f, 1d, "",
        new RowIdListCallback() {
          public void onFailure(DataServiceException error) {
            fail(error.toString());
          }

          public void onSuccess(List<Integer> rowIds) {
            assertNotNull("resultset may not be null!", rowIds);
            assertEquals("Length of resultset must match!", 1, rowIds.size());
            assertTrue("ROWID must be > 0!", rowIds.get(0) > 0);
            finishTest();
          }
        });
  }

  public void testInsertRecordFail() throws Exception {
    delayTestFinish(3000);
    service.insertRecordFail(100, "", 1f, 1d, "", new RowIdListCallback() {
      public void onFailure(DataServiceException error) {
        assertTrue("Error must have message attribute!",
            error.getMessage() != null && error.getMessage().length() > 0);
        assertTrue("Error must have sql attribute!", error.getSql() != null
            && error.getSql().length() > 0);
        finishTest();
      }

      public void onSuccess(List<Integer> rowIds) {
        fail("This SQL should fail!");
      }
    });
  }

  public void testInsertRecordTypesMax() throws Exception {
    delayTestFinish(3000);
    service.insertRecord(Integer.MAX_VALUE, "Testing string values",
        Float.MAX_VALUE, Double.MAX_VALUE, Math.PI, new RowIdListCallback() {
          public void onFailure(DataServiceException error) {
            fail(error.toString());
          }

          public void onSuccess(List<Integer> rowIds) {
            assertNotNull("resultset may not be null!", rowIds);
            assertEquals("Length of resultset must match!", 1, rowIds.size());
            assertTrue("ROWID must be > 0!", rowIds.get(0) > 0);
            finishTest();
          }
        });
  }

  public void testInsertRecordTypesMin() throws Exception {
    delayTestFinish(3000);
    service.insertRecord(Integer.MIN_VALUE, "", Float.MIN_VALUE,
        Double.MIN_VALUE, null, new RowIdListCallback() {
          public void onFailure(DataServiceException error) {
            fail(error.toString());
          }

          public void onSuccess(List<Integer> rowIds) {
            assertNotNull("resultset may not be null!", rowIds);
            assertEquals("Length of resultset must match!", 1, rowIds.size());
            assertTrue("ROWID must be > 0!", rowIds.get(0) > 0);
            finishTest();
          }
        });
  }
  
  public void insertRecords() throws Exception {
    delayTestFinish(3000);
    final List<TestRecord> inserts = new ArrayList<TestRecord>();
    inserts.add(new TestRecord(1000, "record 1", 1, 1d, null));
    inserts.add(new TestRecord(1001, "record 2 ma\u00f1ana with slash-u-00f1", 2, 2d, new Date()));
    inserts.add(new TestRecord(1002, "record 3 ma–ana with literal", 3, 3d, "none"));
    service.insertRecords(inserts, new RowIdListCallback() {
      public void onFailure(DataServiceException error) {
        fail("Failed to insert records! " + error.toString());
      }
      public void onSuccess(final List<Integer> rowIds) {
        // Test returned rowIds:
        assertNotNull("resultset may not be null!", rowIds);
        assertEquals("Length of resultset must match!", 3, rowIds.size());
        service.getRecords(rowIds, new ListCallback<GenericRow>() {
          public void onFailure(DataServiceException error) {
            fail("Failed to obtain records! " + error.toString());
          }
          public void onSuccess(List<GenericRow> result) {
            assertNotNull("Resultset may not be null!", result);
            assertTrue("Length of resultset must be bigger than 0!", rowIds.size() > 0);
            assertEquals("ID list and result list must be of same size!", rowIds.size(), result.size());
            for (int i=0; i<rowIds.size(); i++) {
              assertEquals("int column must match!", inserts.get(i).getI(), result.get(i).getInt("integervalue"));
              assertEquals("text column must match!", inserts.get(i).getText(), result.get(i).getString("textvalue"));
            }
            finishTest();
          }
        });
      }
    });
  }
}
