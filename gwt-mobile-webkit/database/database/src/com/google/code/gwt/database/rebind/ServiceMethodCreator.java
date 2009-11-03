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

package com.google.code.gwt.database.rebind;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.code.gwt.database.client.SQLTransaction;
import com.google.code.gwt.database.client.service.annotation.Update;
import com.google.code.gwt.database.client.service.callback.Callback;
import com.google.code.gwt.database.client.util.StringUtils;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Base class representing a tx.executeSql(..) call creator for each type of
 * {@link Callback}.
 * 
 * @author bguijt
 */
public abstract class ServiceMethodCreator {

  protected GeneratorContext context;
  protected TreeLogger logger;
  protected SourceWriter sw;
  protected JMethod service;
  protected String sql;
  protected String foreach;
  protected Annotation query;
  protected GeneratorUtils genUtils;

  protected JParameter callback;
  protected String txVarName;

  /**
   * Sets the context for generating the Transaction Callback.
   */
  public void setContext(GeneratorContext context, TreeLogger logger,
      SourceWriter sw, JMethod service, String sql, String foreach,
      Annotation query, GeneratorUtils genUtils) {
    this.context = context;
    this.logger = logger;
    this.sw = sw;
    this.service = service;
    this.sql = sql;
    this.foreach = foreach;
    this.query = query;
    this.genUtils = genUtils;
    this.callback = service.getParameters()[service.getParameters().length - 1];
    this.txVarName = GeneratorUtils.getVariableName("tx",
        service.getParameters());
  }

  /**
   * Generates the actual service method body.
   */
  public void generateServiceMethodBody() throws UnableToCompleteException {
    String txMethodName = query.annotationType().equals(Update.class)
        ? "transaction" : "readTransaction";
    sw.println(txMethodName + "(new " + getTransactionCallbackClassName() + "("
        + callback.getName() + ") {");
    sw.indent();

    sw.println("public void onTransactionStart("
        + genUtils.getClassName(SQLTransaction.class) + " " + txVarName + ") {");
    sw.indent();

    generateOnTransactionStartBody();

    // ends onTransactionStart()
    sw.outdent();
    sw.println("}");

    // ends new TransactionCallback() and (read)transaction() call
    sw.outdent();
    sw.println("});");
  }

  /**
   * Generates the body of an onTransactionStart() method.
   */
  protected abstract void generateOnTransactionStartBody()
      throws UnableToCompleteException;

  /**
   * Returns the name of the TransactionCallback implementation use for this
   * Service Method creator.
   */
  protected abstract String getTransactionCallbackClassName()
      throws UnableToCompleteException;

  /**
   * Generates an iterating <code>tx.executeSql(...);</code> call statement.
   * 
   * @param callbackExpression the expression for an instantiated
   *          StatementCallback class - or <code>null</code> if no callback
   *          applies
   * @throws UnableToCompleteException
   */
  protected void generateExecuteIteratedSqlStatements(String callbackExpression)
      throws UnableToCompleteException {
    if (foreach != null && foreach.trim().length() > 0) {
      // Generate code to loop over a collection to create a tx.executeSql()
      // call for each item.

      // Find the types, parameters, assert not-nulls, etc.:
      JType collection = GeneratorUtils.findType(foreach,
          service.getParameters());
      if (collection == null) {
        logger.log(TreeLogger.WARN, "The method " + service.getName()
            + " has no parameter named '" + foreach
            + "'. Using Object as the type for the loop variable '_'");
      }
      String forEachType = collection != null ? genUtils.getTypeParameter(
          service, collection) : null;
      if (forEachType == null) {
        forEachType = "Object";
      }

      sw.println("for (" + forEachType + " _ : " + foreach + ") {");
      sw.indent();
      generateExecuteSqlStatement(callbackExpression);
      sw.outdent();
      sw.println("}");
    }
  }

  /**
   * Generates a <code>tx.executeSql(...);</code> call statement.
   * 
   * @param callbackExpression the expression for an instantiated
   *          StatementCallback class - or <code>null</code> if no callback
   *          applies
   * @throws UnableToCompleteException
   */
  protected void generateExecuteSqlStatement(String callbackExpression)
      throws UnableToCompleteException {
    List<String> prepStmt = getPreparedStatementSql(sql, service);
    sw.print(txVarName + ".executeSql(" + prepStmt.get(0) + ", ");
    if (prepStmt.size() == 1) {
      sw.print("null");
    } else {
      sw.print("new Object[] {");
      for (int i = 1; i < prepStmt.size(); i++) {
        if (i > 1) {
          sw.print(", ");
        }
        sw.print(prepStmt.get(i));
      }
      sw.print("}");
    }

    // Callback provided:
    if (callbackExpression != null) {
      sw.print(", " + callbackExpression);
    }

    sw.println(");");
  }

  /**
   * Returns the specified SQL expression in at least one part.
   * 
   * <p>
   * The first part represents an SQL expression where each {} parameter is
   * substituted for a '?' character; the other parts represent the substituted
   * expressions.
   * </p>
   * 
   * <p>
   * This means that the following stmt:
   * </p>
   * 
   * <pre>INSERT INTO clickcount (clicked) VALUES ({when.getTime()})</pre>
   * <p>
   * will be translated to the following List:
   * </p>
   * <ul>
   * <li><code>"INSERT INTO clickcount (clicked) VALUES (?)"</code></li>
   * <li><code>when.getTime()</code></li>
   * </ul>
   */
  private List<String> getPreparedStatementSql(String stmt, JMethod service)
      throws UnableToCompleteException {
    List<String> result = new ArrayList<String>();
    StringBuilder sql = new StringBuilder("\"");
    StringBuilder param = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < stmt.length(); i++) {
      char ch = stmt.charAt(i);
      switch (ch) {
        case '{':
          if (depth == 0) {
            // Start a parameter:
            param = new StringBuilder();
          } else {
            param.append(ch);
          }
          depth++;
          break;
        case '}':
          depth--;
          if (depth == 0) {
            // End a parameter:
            String s = param.toString().trim();
            if (s.length() == 0) {
              logger.log(TreeLogger.ERROR,
                  "Parameter expression in SQL statement '" + stmt
                      + "' is empty!");
              throw new UnableToCompleteException();
            }

            appendParameter(sql, service, s, result);
          } else if (depth < 0) {
            logger.log(TreeLogger.ERROR,
                "Parameter expression in SQL statement '" + stmt
                    + "' is not closed correctly! Too many closing brace(s)");
            throw new UnableToCompleteException();
          } else {
            param.append(ch);
          }
          break;
        default:
          if (depth == 0) {
            StringUtils.appendEscapedChar(sql, ch);
          } else {
            param.append(ch);
          }
          break;
      }
    }
    if (depth > 0) {
      logger.log(TreeLogger.ERROR, "Parameter expression(s) in SQL statement '"
          + stmt + "' is not closed correctly! Missing " + depth
          + " closing brace(s)");
      throw new UnableToCompleteException();
    }
    result.add(0, sql.toString().trim() + "\"");
    return result;
  }

  /**
   * Appends a parameter to the sql String.
   * 
   * <p>
   * Depending on whether the specified <code>expression</code> is an
   * <code>{@link Iterable}&lt;? extends {@link Number}&gt;</code> or
   * <code>{@link Iterable}&lt;? extends {@link String}&gt;</code>, the SQL
   * string is amended with '?' (for a single oparameter), a call to
   * {@link StringUtils#joinCollectionNumber(Iterable, String)} or a call to
   * {@link StringUtils#joinEscapedCollectionString(Iterable, String)}.
   * </p>
   */
  private void appendParameter(StringBuilder sql, JMethod service,
      String expression, List<String> result) throws UnableToCompleteException {
    JType type = GeneratorUtils.findType(expression, service.getParameters());
    boolean addMultiple = false;
    String typeParam = null;
    if (type != null) {
      if (genUtils.isAssignableToType(type, Iterable.class)) {
        // OK, we've got our collection. Is the Type parameter 'suitable'?
        typeParam = genUtils.getTypeParameter(service, type);
        for (String t : new String[] {"String", "Integer", "Number", "Long"}) {
          if (typeParam.equals(t)) {
            addMultiple = true;
            break;
          }
        }
        if (!addMultiple) {
          logger.log(TreeLogger.ERROR, "Service method named '"
              + service.getName()
              + "' has a parameter defined in the SQL statement named '"
              + expression + "' which is defined as an Iterable, but its type "
              + "parameter is NOT one of String, Long, Integer, Short, Number");
          throw new UnableToCompleteException();
        }
      }
    }
    if (addMultiple) {
      String joinMethodName = typeParam.equals("String")
          ? "joinEscapedCollectionString" : "joinCollectionNumber";
      sql.append("\" + ").append(genUtils.getClassName(StringUtils.class)).append(
          ".").append(joinMethodName).append("(").append(expression).append(
          ", \",\") + \"");
    } else {
      result.add(expression);
      sql.append('?');
    }
  }
}
