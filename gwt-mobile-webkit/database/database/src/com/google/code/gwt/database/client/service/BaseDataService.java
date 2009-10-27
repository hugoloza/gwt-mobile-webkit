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

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.DatabaseException;

/**
 * Base class for all generated {@link DataService} implementations using the
 * {@link com.google.code.gwt.database.rebind.DataServiceGenerator}.
 * 
 * @author bguijt
 */
public abstract class BaseDataService implements DataService {

  private static final String ERR_MSG = "Unable to open Web Database ";

  private static Database database = null;

  /**
   * Returns the Database connection singleton.
   */
  public final Database getDatabase() {
    return getDatabase(null);
  }

  /**
   * Returns the Database connection singleton.
   * 
   * @param callback if not <code>null</code>, any initialization errors go to
   *          its {@link Callback#onFailure(DataServiceException)} callback
   *          method
   * @return a Database instance, or <code>null</code> if something went wrong.
   */
  protected final Database getDatabase(Callback callback) {
    if (database == null) {
      if (!Database.isSupported()) {
        callFailure(callback, "Web Database NOT supported");
        return null;
      }
      try {
        database = openDatabase();
        if (database == null) {
          callFailure(callback, ERR_MSG + getDatabaseDetails()
              + ": openDatabase() returned null");
        }
      } catch (DatabaseException e) {
        callFailure(callback, ERR_MSG + getDatabaseDetails() + ": "
            + e.getMessage());
        return null;
      }
    }
    return database;
  }

  /**
   * Opens the actual Web Database.
   * 
   * @return an instance of the Database, <code>null</code> if something went
   *         wrong.
   * 
   * @throws DatabaseException if opening the Database failed
   */
  protected abstract Database openDatabase() throws DatabaseException;

  /**
   * Returns a String identifying the Database for e.g. constructing an error
   * message.
   */
  protected abstract String getDatabaseDetails();

  private void callFailure(Callback callback, String msg) {
    if (callback != null) {
      callback.onFailure(new DataServiceException(msg));
    }
  }
}
