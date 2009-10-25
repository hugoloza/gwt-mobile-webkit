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
import com.google.code.gwt.database.client.TransactionCallback;
import com.google.code.gwt.database.rebind.DataServiceGenerator;

/**
 * Used in the {@link DataServiceGenerator} to reduce generated boilerplate
 * code.
 * 
 * <p>
 * The Generator only cares for onTransactionStart() and onTransactionSuccess()
 * method bodies.
 * </p>
 * 
 * @author bguijt
 */
public abstract class DataServiceTransactionCallback implements
    TransactionCallback {

  private Callback callback;

  /**
   * Creates a new TransactionCallback with the specified DataService callback.
   */
  public DataServiceTransactionCallback(Callback callback) {
    this.callback = callback;
  }

  /**
   * Invokes the DataService' {@link Callback#onFailure(DataServiceException)}
   * callback method.
   */
  public void onTransactionFailure(SQLError error) {
    callback.onFailure(new DataServiceException(error));
  }

  /**
   * Returns the DataService callback associated with this transaction.
   */
  protected Callback getCallback() {
    return callback;
  }
}
