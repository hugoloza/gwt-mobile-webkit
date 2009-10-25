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

package com.google.code.gwt.database.client.service;

import com.google.code.gwt.database.client.SQLError;
import com.google.code.gwt.database.client.SQLTransaction;
import com.google.code.gwt.database.client.StatementCallback;
import com.google.code.gwt.database.rebind.DataServiceGenerator;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Used in the {@link DataServiceGenerator} to reduce generated boilerplate
 * code.
 * 
 * @author bguijt
 */
public abstract class DataServiceStatementCallback<T extends JavaScriptObject>
    implements StatementCallback<T> {

  /**
   * The onFailure method, once called, always signals the database to rollback
   * the transaction.
   * 
   * @return <code>true</code>
   */
  public boolean onFailure(SQLTransaction transaction, SQLError error) {
    return true;
  }

}
