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
import java.util.List;

import com.google.code.gwt.database.client.service.Connection;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.Select;
import com.google.code.gwt.database.client.service.Update;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the {@link DataService} API with the {@link ListCallback}.
 * 
 * @author bguijt
 */
public class DataServiceListCallbackTest extends GWTTestCase {

  /**
   * Used to obtain a list of ROWID's
   */
  public static class IdRow extends JavaScriptObject {
    protected IdRow() {}
    public final native int getId() /*-{
      return this.id;
    }-*/;
  }
  
  @Connection(name="gh5dt", version="1.0",
      description="GwtHtml5DatabaseTest", maxsize=5000)
  public interface TestListCallbackDataService extends DataService {
    
    @Update("CREATE TABLE IF NOT EXISTS testtable ("
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "integervalue INTEGER, "
        + "textvalue TEXT, "
        + "numericvalue NUMERIC, "
        + "realvalue REAL, "
        + "nonevalue NONE)")
    void create(VoidCallback callback);

    @Select("SELECT id FROM testtable")
    void getIds(ListCallback<IdRow> callback);

    @Select("SELECT integervalue, textvalue, numericvalue, realvalue, "
        + "nonevalue FROM testtable WHERE id IN ({ids})")
    void getRecords(Iterable<Integer> ids, ListCallback<GenericRow> callback);
  }

  private TestListCallbackDataService service = null;
  
  @Override
  public String getModuleName() {
    return "com.google.code.gwt.database.Html5Database";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    service  = GWT.create(TestListCallbackDataService.class);
    assertNotNull("GWT.create() of a DataService may not return null!", service);
  }

  public void testCreate() throws Exception {
    delayTestFinish(10000);
    service.create(new VoidCallback() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess() {
        finishTest();
      }
    });
  }
  
  public void testGetIdsRemoveFromResultset() throws Exception {
    delayTestFinish(3000);
    service.getIds(new ListCallback<IdRow>() {
      public void onFailure(DataServiceException error) {
        fail("Failed to obtain ID's! " + error);
      }
      public void onSuccess(List<IdRow> result) {
        assertNotNull("Resultset may never be null!", result);
        assertTrue("Number of IDs must be larger than 0 for this test!",
            result.size() > 0);
        try {
          result.remove(0);
          fail("Removing an item from resultset should fail!");
        } catch (Exception e) {
          assertEquals("Exception must be of right type!",
              UnsupportedOperationException.class, e.getClass());
          finishTest();
        }
      }
    });
  }
  
  public void testGetIds() throws Exception {
    delayTestFinish(3000);
    service.getIds(new ListCallback<IdRow>() {
      public void onFailure(DataServiceException error) {
        fail("Failed to obtain ID's! " + error);
      }
      public void onSuccess(List<IdRow> result) {
        assertNotNull("Resultset may never be null!", result);
        assertTrue("Number of IDs must be larger than 0 for this test!",
            result.size() > 0);
        final List<Integer> ids = new ArrayList<Integer>();
        for (IdRow id : result) {
          ids.add(Integer.valueOf(id.getId()));
        }
        service.getRecords(ids, new ListCallback<GenericRow>() {
          public void onSuccess(List<GenericRow> records) {
            assertNotNull("Resultset may never be null!", records);
            assertTrue("Number of records must be larger than 0 for this test!",
                records.size() > 0);
            assertEquals("Number of records must equal number of IDs!",
                ids.size(), records.size());
            finishTest();
          }
          public void onFailure(DataServiceException error) {
            fail("Failed to obtain records! " + error);
          }
        });
      }
    });
  }
}
