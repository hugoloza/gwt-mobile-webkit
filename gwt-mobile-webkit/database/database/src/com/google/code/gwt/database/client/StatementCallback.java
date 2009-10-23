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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Provides the result of an executed SQL Statement.
 * 
 * <p>
 * This interface specifies the object type you expect to return in the
 * resultset rows by means of the Java Generics. A generic row type is
 * {@link GenericRow}, which provides access to all row attributes dynamically.
 * Use {@link GenericRow} like follows:
 * </p>
 * 
 * <pre>
 * public class MyStmtCallback implements StatementCallback&lt;GenericRow&gt; {
 *     public void onSuccess(SQLTransaction tx, SQLResultSet&lt;GenericRow&gt; rs) {
 *         for (GenericRow row : rs.getRows()) {
 *             row.getString("name");  // returns the attribute 'name' from the row as a String
 *             row.getInt("age");  // returns the attribute 'age' from the row as an int
 *         }
 *     }
 *     public boolean onFailure(SQLTransaction transaction, SQLError error) {
 *         // You might want to do something here...
 *         return false;
 *     }
 * }
 * </pre>
 * 
 * @param <T> is the type which is eventually returned in the
 *          {@link SQLResultSet} at the
 *          {@link #onSuccess(SQLTransaction, SQLResultSet)} call. You can use
 *          {@link GenericRow} as a safe default type.
 * 
 * @see GenericRow
 * @see SQLTransaction
 * @see <a href="http://www.w3.org/TR/webstorage/#sqlstatementcallback">W3C Web
 *      Storage - SQLStatementCallback</a>
 * @see <a href="http://www.w3.org/TR/webstorage/#sqlstatementerrorcallback">W3C
 *      Web Storage - SQLStatementErrorCallback</a>
 * @author bguijt
 */
public interface StatementCallback<T extends JavaScriptObject> {

  /**
   * This callback method is invoked with the result of an executed SQL
   * statement (be it SELECT, CREATE, UPDATE or anything else).
   * 
   * @param transaction the transaction context
   * @param resultSet the result of the statement
   */
  void onSuccess(SQLTransaction transaction, SQLResultSet<T> resultSet);

  /**
   * This callback method is invoked if the SQL statement fails.
   * 
   * @param transaction the transaction we're running now
   * @param error the SQL error causing the failure
   * @return <code>true</code> if the specified <code>transaction</code> must be
   *         rolled-back, <code>false</code> otherwise.
   */
  boolean onFailure(SQLTransaction transaction, SQLError error);
}
