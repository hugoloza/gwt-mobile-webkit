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
 * Represents a transaction, in either read or read/write mode, as started by a
 * {@link Database} instance.
 * 
 * <p>
 * You can use this class to invoke SQL statements by means of
 * {@link #executeSql(String, Object[])}. Instances of this class also mark the
 * boundaries of a transaction.
 * </p>
 * 
 * @see <a href="http://www.w3.org/TR/webstorage/#sqltransaction">W3C Web
 *      Storage - SQLTransaction</a>
 * @author bguijt
 */
public class SQLTransaction extends JavaScriptObject {

  protected SQLTransaction() {
  }

  /*
   * Helper method to bind the JS function callback to the Java
   * SQLStatementCallback interface.
   */
  @SuppressWarnings( {"unused", "unchecked"})
  private static final void handleStatement(StatementCallback callback,
      SQLTransaction transaction, SQLResultSet resultSet) {
    callback.onSuccess(transaction, resultSet);
  }

  /*
   * Helper method to bind the JS function callback to the Java
   * SQLStatementErrorCallback interface.
   */
  @SuppressWarnings("unused")
  private static final void handleError(
      StatementCallback<? extends JavaScriptObject> callback,
      SQLTransaction transaction, SQLError error) {
    callback.onFailure(transaction, error);
  }

  /**
   * Executes the provided <code>sqlStatement</code> with the specified
   * <code>arguments</code>.
   * 
   * <p>
   * The SQL is executed in a SQLite3 database. Please see the <a
   * href="http://www.sqlite.org/lang.html">SQL Language Reference</a> for its
   * capabilities.
   * </p>
   * 
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-sqltransaction-executesql">W3C
   *      Web Storage - SQLTransaction - executeSql</a>
   * @see <a href="http://www.sqlite.org/lang.html">SQLite3 SQL Language
   *      Reference</a>
   * @param sqlStatement the SQL statement to execute, containing
   *          <code>"?"</code> placeholders for the <code>arguments</code>
   * @param arguments the arguments to fit in the placeholders of the
   *          <code>sqlStatement</code> (could be <code>null</code>)
   */
  public final native void executeSql(String sqlStatement, Object[] arguments) /*-{
    this.executeSql(sqlStatement, arguments);
  }-*/;

  /**
   * Executes the provided <code>sqlStatement</code> with the specified
   * <code>arguments</code>.
   * 
   * <p>
   * The SQL is executed in a SQLite3 database. Please see the <a
   * href="http://www.sqlite.org/lang.html">SQL Language Reference</a> for its
   * capabilities.
   * </p>
   * 
   * @see <a
   *      href="http://www.w3.org/TR/webstorage/#dom-sqltransaction-executesql">W3C
   *      Web Storage - SQLTransaction - executeSql</a>
   * @see <a href="http://www.sqlite.org/lang.html">SQLite3 SQL Language
   *      Reference</a>
   * @param sqlStatement the SQL statement to execute, containing
   *          <code>"?"</code> placeholders for the <code>arguments</code>
   * @param arguments the arguments to fit in the placeholders of the
   *          <code>sqlStatement</code> (could be <code>null</code>)
   * @param callback the callback for forwarding the result of the SQL statement
   */
  @SuppressWarnings("unchecked")
  public final native void executeSql(String sqlStatement, Object[] arguments,
      StatementCallback callback) /*-{
    this.executeSql(
      sqlStatement,
      arguments,
      function(transaction, resultSet) {
        @com.google.code.gwt.database.client.SQLTransaction::handleStatement(Lcom/google/code/gwt/database/client/StatementCallback;Lcom/google/code/gwt/database/client/SQLTransaction;Lcom/google/code/gwt/database/client/SQLResultSet;) (callback, transaction, resultSet);
      },
      function(transaction, error) {
        return @com.google.code.gwt.database.client.SQLTransaction::handleError(Lcom/google/code/gwt/database/client/StatementCallback;Lcom/google/code/gwt/database/client/SQLTransaction;Lcom/google/code/gwt/database/client/SQLError;) (callback, transaction, error);
      }
    );
  }-*/;
}
