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

import com.google.code.gwt.database.client.service.Connection;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.Select;
import com.google.code.gwt.database.client.service.Update;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests the {@link DataService} API with the {@link ScalarCallback}.
 * 
 * @author bguijt
 */
public class DataServiceScalarCallbackTest extends GWTTestCase {

  @Connection(name="gh5dt", version="1.0", description="GwtHtml5DatabaseTest", maxsize=5000)
  public interface TestScalarCallbackDataService extends DataService {
    
    @Update("CREATE TABLE IF NOT EXISTS testtable ("
        + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
        + "integervalue INTEGER, "
        + "textvalue TEXT, "
        + "numericvalue NUMERIC, "
        + "realvalue REAL, "
        + "nonevalue NONE)")
    void create(VoidCallback callback);
    
    @Select("SELECT COUNT(*) FROM testtable")
    void selectCountAllInt(ScalarCallback<Integer> callback);
    
    @Select("SELECT COUNT(*) FROM testtable")
    void selectCountAllShort(ScalarCallback<Short> callback);
    
    @Select("SELECT COUNT(*) FROM testtable")
    void selectCountAllByte(ScalarCallback<Byte> callback);
    
    @Select("SELECT COUNT(*) FROM testtable")
    void selectCountAllFloat(ScalarCallback<Float> callback);
    
    @Select("SELECT COUNT(*) FROM testtable")
    void selectCountAllDouble(ScalarCallback<Double> callback);
    
    @Select("SELECT (COUNT(*) > 0) FROM testtable")
    void hasRecordsTrue(ScalarCallback<Boolean> callback);
    
    @Select("SELECT (COUNT(*) == 0) FROM testtable")
    void hasNoRecordsFalse(ScalarCallback<Boolean> callback);
    
    @Select("SELECT COUNT(*) FROM nonexistingtable")
    void selectCountAllFail(ScalarCallback<Integer> callback);
    
    @Select("SELECT textvalue FROM testtable WHERE LENGTH(textvalue) > 0")
    void selectLastText(ScalarCallback<String> callback);
  }
  
  private TestScalarCallbackDataService service = null;
  
  @Override
  public String getModuleName() {
    return "com.google.code.gwt.database.Html5Database";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    service = GWT.create(TestScalarCallbackDataService.class);
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
  
  public void testSelectCountAllInt() {
    delayTestFinish(3000);
    service.selectCountAllInt(new ScalarCallback<Integer>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Integer result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must be 0 or higher", result >= 0);
        finishTest();
      }
    });
  }
  
  public void testSelectCountAllShort() {
    delayTestFinish(3000);
    service.selectCountAllShort(new ScalarCallback<Short>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Short result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must be 0 or higher", result >= 0);
        finishTest();
      }
    });
  }
  
  public void testSelectCountAllByte() {
    delayTestFinish(3000);
    service.selectCountAllByte(new ScalarCallback<Byte>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Byte result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must be 0 or higher", result >= 0);
        finishTest();
      }
    });
  }
  
  public void testSelectCountAllFloat() {
    delayTestFinish(3000);
    service.selectCountAllFloat(new ScalarCallback<Float>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Float result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must be 0 or higher", result >= 0);
        finishTest();
      }
    });
  }
  
  public void testSelectCountAllDouble() {
    delayTestFinish(3000);
    service.selectCountAllFloat(new ScalarCallback<Float>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Float result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must be 0 or higher", result >= 0);
        finishTest();
      }
    });
  }
  
  public void testHasRecordsTrue() {
    delayTestFinish(3000);
    service.hasRecordsTrue(new ScalarCallback<Boolean>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Boolean result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must be true", result);
        finishTest();
      }
    });
  }
  
  public void testHasNoRecordsFalse() {
    delayTestFinish(3000);
    service.hasNoRecordsFalse(new ScalarCallback<Boolean>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(Boolean result) {
        assertNotNull("Result may not be null", result);
        assertFalse("Result must be false", result);
        finishTest();
      }
    });
  }
  
  public void testSelectCountAllFail() {
    delayTestFinish(3000);
    service.selectCountAllFail(new ScalarCallback<Integer>() {
      public void onFailure(DataServiceException error) {
        assertTrue("Error must have message attribute!", error.getMessage() != null && error.getMessage().length() > 0);
        assertTrue("Error must have sql attribute!", error.getSql() != null && error.getSql().length() > 0);
        finishTest();
      }
      public void onSuccess(Integer result) {
        fail("Querying nonexisting table should fail!");
      }
    });
  }
  
  public void testSelectLastText() {
    delayTestFinish(3000);
    service.selectLastText(new ScalarCallback<String>() {
      public void onFailure(DataServiceException error) {
        fail(error.toString());
      }
      public void onSuccess(String result) {
        assertNotNull("Result may not be null", result);
        assertTrue("Result must have a length > 0", result.length() >= 0);
        finishTest();
      }
    });
  }
}
