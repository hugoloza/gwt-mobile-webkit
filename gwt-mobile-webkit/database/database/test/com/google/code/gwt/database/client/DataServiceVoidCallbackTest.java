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

import com.google.code.gwt.database.client.service.Connection;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.Update;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the {@link DataService} API with the {@link VoidCallback}.
 * 
 * @author bguijt
 */
public class DataServiceVoidCallbackTest extends GWTTestCase {

  @Connection(name="gh5dt", version="1.0",
      description="GwtHtml5DatabaseTest", maxsize=5000)
  public interface TestVoidCallbackDataService extends DataService {
    
    @Update("CREATE TABLE IF NOT EXISTS testtable ("
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "integervalue INTEGER, "
        + "textvalue TEXT, "
        + "numericvalue NUMERIC, "
        + "realvalue REAL, "
        + "nonevalue NONE)")
    void createOk(VoidCallback callback);
    
    @Update("DROP TABLE nonexistingtable")
    void dropFail(VoidCallback callback);
    
    @Update("DELETE FROM testtable")
    void emptyTable(VoidCallback callback);
    
    public enum TestEnum {
      A, B
    }
    /**
     * Just type testing here!
     */
    @Update(sql="SELECT * FROM clickcount WHERE clicked={i} OR clicked={e} "
    		+ "OR clicked={array1} OR clicked={array2} OR clicked={col1}",
    		foreach="col1")
    void testTypes(int i, TestEnum e, int[] array1, String[] array2,
        Collection<String> col1, VoidCallback callback);
  }
  
  private TestVoidCallbackDataService service = null;
  
  @Override
  public String getModuleName() {
    return "com.google.code.gwt.database.Html5Database";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    service = GWT.create(TestVoidCallbackDataService.class);
    assertNotNull("GWT.create() of a DataService may not return null!",
        service);
  }

  public void testCreateOk() throws Exception {
    delayTestFinish(3000);
    service.createOk(new VoidCallback() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess() {
        finishTest();
      }
    });
  }

  public void testDropFail() throws Exception {
    delayTestFinish(3000);
    service.dropFail(new VoidCallback() {
      public void onFailure(DataServiceException error) {
        assertTrue("Error must have message attribute!",
            error.getMessage() != null && error.getMessage().length() > 0);
        assertTrue("Error must have sql attribute!", error.getSql() != null
            && error.getSql().length() > 0);
        finishTest();
      }
      public void onSuccess() {
        fail("The statement drops a non existing table - it should fail!");
      }
    });
  }
  
  public void testEmptyTable() throws Exception {
    delayTestFinish(3000);
    service.emptyTable(new VoidCallback() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess() {
        finishTest();
      }
    });
  }
}
