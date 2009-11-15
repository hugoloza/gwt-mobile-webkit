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

import junit.framework.Test;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * Suite for all Database tests.
 * 
 * @author bguijt
 */
public class DatabaseSuite {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test for HTML5 Database API");
    
    // $JUnit-BEGIN$
    suite.addTestSuite(DatabaseTest.class);
    suite.addTestSuite(DataServiceVoidCallbackTest.class);
    suite.addTestSuite(DataServiceRowIdListCallbackTest.class);
    suite.addTestSuite(DataServiceScalarCallbackTest.class);
    suite.addTestSuite(DataServiceListCallbackTest.class);
    // $JUnit-END$
    
    return suite;
  }
}
