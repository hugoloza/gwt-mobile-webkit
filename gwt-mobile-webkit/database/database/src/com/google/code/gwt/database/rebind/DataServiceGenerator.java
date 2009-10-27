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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.DatabaseException;
import com.google.code.gwt.database.client.SQLResultSet;
import com.google.code.gwt.database.client.SQLTransaction;
import com.google.code.gwt.database.client.TransactionCallback;
import com.google.code.gwt.database.client.service.BaseDataService;
import com.google.code.gwt.database.client.service.Callback;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.DataServiceStatementCallback;
import com.google.code.gwt.database.client.service.DataServiceStatementCallbackListCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackListCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackVoidCallback;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.ScalarRow;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.code.gwt.database.client.service.annotation.Connection;
import com.google.code.gwt.database.client.service.annotation.SQL;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates the proxy code for the {@link DataService} definitions.
 * 
 * @see DataService
 * 
 * @author bguijt
 */
public class DataServiceGenerator extends Generator {

  private static final String[] IMPORTED_CLASSES = new String[] {
      Date.class.getCanonicalName(), Database.class.getCanonicalName(),
      SQLResultSet.class.getCanonicalName(),
      SQLTransaction.class.getCanonicalName(), BaseDataService.class.getName(),
      ScalarRow.class.getCanonicalName(),
      VoidCallback.class.getCanonicalName(),
      ListCallback.class.getCanonicalName(),
      ScalarCallback.class.getCanonicalName(),
      DataServiceStatementCallback.class.getCanonicalName(),
      DataServiceStatementCallbackListCallback.class.getCanonicalName(),
      DataServiceTransactionCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackVoidCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackListCallback.class.getCanonicalName(),
      DatabaseException.class.getCanonicalName()};

  @Override
  public String generate(TreeLogger logger, GeneratorContext context,
      String requestedClass) throws UnableToCompleteException {

    // Assertions:

    TypeOracle typeOracle = context.getTypeOracle();
    assert (typeOracle != null);

    JClassType dataService = typeOracle.findType(requestedClass);
    if (dataService == null) {
      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
          + requestedClass + "'", null);
      throw new UnableToCompleteException();
    }

    if (dataService.isInterface() == null) {
      logger.log(TreeLogger.ERROR, dataService.getQualifiedSourceName()
          + " is not an interface", null);
      throw new UnableToCompleteException();
    }

    Connection conAnnotation = dataService.getAnnotation(Connection.class);
    if (conAnnotation == null) {
      logger.log(TreeLogger.ERROR, "DataService interface must be annotated"
          + " with @Connection to define database connection details");
      throw new UnableToCompleteException();
    }

    // All basic assertions checked: Generate the code!

    return generateProxy(logger.branch(TreeLogger.DEBUG,
        "Generating proxy methods to database '" + conAnnotation.name()
            + "'..."), context, dataService);
  }

  /**
   * Generates the proxy source code.
   */
  private String generateProxy(TreeLogger logger, GeneratorContext context,
      JClassType dataService) throws UnableToCompleteException {

    SourceWriter srcWriter = getSourceWriter(logger, context, dataService);
    if (srcWriter == null) {
      // No need to generate, it's already done. Return name of generated class.
      return getProxyQualifiedName(dataService);
    }

    generateProxyConstructor(logger, context, dataService, srcWriter);

    generateProxyOpenDatabaseMethod(logger, context, dataService, srcWriter);

    generateProxyGetDatabaseDetailsMethod(logger, context, dataService,
        srcWriter);

    // Generate service methods for each defined interface method:
    for (JMethod method : dataService.getMethods()) {
      generateProxyServiceMethod(logger, context, dataService, method,
          srcWriter);
    }

    srcWriter.commit(logger);

    return getProxyQualifiedName(dataService);
  }

  /**
   * Generates the constructor.
   */
  private void generateProxyConstructor(TreeLogger logger,
      GeneratorContext context, JClassType dataService, SourceWriter srcWriter) {
    srcWriter.println("public " + getProxySimpleName(dataService) + "() {");
    srcWriter.indent();
    srcWriter.println("// default empty constructor");
    srcWriter.outdent();
    srcWriter.println("}");
  }

  /**
   * Generates the {@link BaseDataService#openDatabase()} method
   */
  private void generateProxyOpenDatabaseMethod(TreeLogger logger,
      GeneratorContext context, JClassType dataService, SourceWriter srcWriter) {
    Connection con = dataService.getAnnotation(Connection.class);
    srcWriter.beginJavaDocComment();
    srcWriter.print("Opens the '" + con.name() + "' Database version "
        + con.version());
    srcWriter.endJavaDocComment();
    srcWriter.println("public final " + getClassName(Database.class)
        + " openDatabase() throws " + getClassName(DatabaseException.class)
        + " {");
    srcWriter.indentln("return " + getClassName(Database.class)
        + ".openDatabase(\"" + escape(con.name()) + "\", \""
        + escape(con.version()) + "\", \"" + escape(con.description()) + "\", "
        + con.maxsize() + ");");
    srcWriter.println("}");
  }

  /**
   * Generates the {@link BaseDataService#getDatabaseDetails()} method.
   */
  private void generateProxyGetDatabaseDetailsMethod(TreeLogger logger,
      GeneratorContext context, JClassType dataService, SourceWriter srcWriter) {
    Connection con = dataService.getAnnotation(Connection.class);
    String toReturn = "'" + escape(con.name()) + "' version "
        + escape(con.version());
    srcWriter.beginJavaDocComment();
    srcWriter.print("Returns the <code>" + toReturn + "</code> string.");
    srcWriter.endJavaDocComment();
    srcWriter.println("public final String getDatabaseDetails() {");
    srcWriter.indentln("return \"" + toReturn + "\";");
    srcWriter.println("}");
  }

  /**
   * Generates the proxy method implementing the specified service.
   */
  private void generateProxyServiceMethod(TreeLogger logger,
      GeneratorContext context, JClassType dataService, JMethod service,
      SourceWriter srcWriter) throws UnableToCompleteException {
    SQL sql = service.getAnnotation(SQL.class);

    // Assertions:
    if (sql == null) {
      logger.log(TreeLogger.ERROR, service.getName()
          + " has no @SQL annotation");
      throw new UnableToCompleteException();
    }
    if (sql.value() == null || sql.value().length == 0) {
      logger.log(TreeLogger.ERROR, service.getName()
          + ": @SQL annotation has no SQL statement(s)");
      throw new UnableToCompleteException();
    }
    JParameter[] params = service.getParameters();
    if (params.length == 0) {
      logger.log(TreeLogger.ERROR, "Method " + service.getName()
          + " must have at least one (callback) parameter");
      throw new UnableToCompleteException();
    }
    JParameter callback = params[params.length - 1];
    if (!callback.getType().isClassOrInterface().isAssignableTo(
        context.getTypeOracle().findType(Callback.class.getCanonicalName()))) {
      logger.log(TreeLogger.ERROR, "The last parameter of method "
          + service.getName() + " is no valid Callback! Must be subtype of "
          + Callback.class.getCanonicalName());
      throw new UnableToCompleteException();
    }

    generateProxyServiceMethodJavadoc(logger, service, srcWriter);

    srcWriter.print("public final void " + service.getName() + "(");
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        srcWriter.print(", ");
      }
      srcWriter.print("final " + getClassName(params[i].getType()) + " "
          + params[i].getName());
    }
    srcWriter.println(") {");
    srcWriter.indent();

    // Determine unique variable names:
    String dbVarName = getVariableName("db", params);
    String txVarName = getVariableName("tx", params);
    String storeVarName = getVariableName("store", params);

    // We need to obtain a Database connection.
    srcWriter.println("final " + getClassName(Database.class) + " " + dbVarName
        + " = getDatabase(" + callback.getName() + ");");
    srcWriter.println("if (" + dbVarName + " != null) {");
    srcWriter.indent();

    srcWriter.println(dbVarName + "." + getTxMethod(sql) + "(new "
        + getTransactionCallbackClassName(logger, callback.getType()) + "("
        + callback.getName() + ") {");
    srcWriter.indent();

    // We need a temporary 'store' for the ScalarCallback here:
    if (isType(callback.getType(), ScalarCallback.class)) {
      String scalarType = getTypeParameter(logger, callback.getType());
      if (scalarType.indexOf('.') >= 0) {
        logger.log(TreeLogger.ERROR,
            "The ScalarCallback does not understand scalar type " + scalarType);
        throw new UnableToCompleteException();
      }
      srcWriter.println(scalarType + " " + storeVarName + " = null;");
    }

    srcWriter.println("public void onTransactionStart("
        + getClassName(SQLTransaction.class) + " " + txVarName + ") {");
    srcWriter.indent();
    // Write a tx.executeSql() call for each SQL statement:
    for (int i = 0; i < sql.value().length; i++) {
      generateExecuteSqlStatement(logger, service, srcWriter, callback,
          i == (sql.value().length - 1), sql.value()[i]);
    }
    srcWriter.outdent();
    srcWriter.println("}");

    if (isType(callback.getType(), ScalarCallback.class)) {
      srcWriter.println("public void onTransactionSuccess() {");
      srcWriter.indentln(callback.getName() + ".onSuccess(" + storeVarName
          + ");");
      srcWriter.println("}");
    }

    srcWriter.outdent();
    srcWriter.println("});");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.println("}");
  }

  /**
   * Generates the Javadoc for the generated service method. The usefulness of
   * this code is arguable low :-)
   */
  private void generateProxyServiceMethodJavadoc(TreeLogger logger,
      JMethod service, SourceWriter srcWriter) throws UnableToCompleteException {
    SQL sql = service.getAnnotation(SQL.class);
    srcWriter.beginJavaDocComment();
    srcWriter.println("Executes the following "
        + (sql.value().length == 1 ? "SQL statement" : sql.value().length
            + " SQL statements") + ":");
    srcWriter.println("<ul>");
    for (String s : sql.value()) {
      // Add a line for each SQL statement, including some nice markup:
      List<String> prepStmt = getPreparedStatementSql(logger, s);
      String code = Util.escapeXml(prepStmt.get(0));
      for (int i = 1; i < prepStmt.size(); i++) {
        int index = code.indexOf('?');
        code = code.substring(0, index) + "<b>" + prepStmt.get(i) + "</b>"
            + code.substring(index + 1);
      }
      srcWriter.println("<li><code>" + code + "</code></li>");
    }
    srcWriter.print("</ul>");
    srcWriter.endJavaDocComment();
  }

  /**
   * Generates a <code>tx.executeSql(...);</code> call statement.
   * 
   * @param logger
   * @param service
   * @param srcWriter
   * @param callback the callback defined for the service method
   * @param isLastStatement whether this is the last statement to execute (to
   *          determine callback type)
   * @param stmt the SQL statement to execute
   * @param txVarName the name of the SQLTransaction instance variable
   * @throws UnableToCompleteException
   */
  private void generateExecuteSqlStatement(TreeLogger logger, JMethod service,
      SourceWriter srcWriter, JParameter callback, boolean isLastStatement,
      String stmt) throws UnableToCompleteException {
    List<String> prepStmt = getPreparedStatementSql(logger, stmt);
    srcWriter.print(getVariableName("tx", service.getParameters())
        + ".executeSql(\"" + escape(prepStmt.get(0)) + "\", ");
    if (prepStmt.size() == 1) {
      srcWriter.print("null");
    } else {
      srcWriter.print("new Object[] {");
      for (int i = 1; i < prepStmt.size(); i++) {
        if (i > 1) {
          srcWriter.print(", ");
        }
        srcWriter.print(prepStmt.get(i));
      }
      srcWriter.print("}");
    }

    // VoidCallback template:
    if (!isLastStatement || isType(callback.getType(), VoidCallback.class)) {
      // No callback to write. Default behaviour is exactly what we need
      // (stop transaction at failures).
    }

    // ListCallback template:
    else if (isType(callback.getType(), ListCallback.class)) {
      String rowType = getTypeParameter(logger, callback.getType());
      srcWriter.print(", new "
          + getClassName(DataServiceStatementCallbackListCallback.class) + "<"
          + rowType + ">(this)");
    }

    // ScalarCallback template:
    else if (isType(callback.getType(), ScalarCallback.class)) {
      String scalarType = getTypeParameter(logger, callback.getType());
      generateStmtCallbackArgument(srcWriter, getClassName(ScalarRow.class),
          getVariableName("store", service.getParameters())
              + " = r.getRows().getItem(0).get" + scalarType + "();");
    }

    // No expected callback found:
    else {
      logger.log(TreeLogger.ERROR, "Unknown callback type found: "
          + callback.getType().getQualifiedSourceName());
      throw new UnableToCompleteException();
    }

    srcWriter.println(");");
  }

  /**
   * Generates a StatementCallback definition as part of a method call.
   * 
   * @param srcWriter
   * @param rowType the type which represents a row from the resultSet
   * @param onSuccessStmt the statement(s) to execute in the onSuccess body
   */
  private void generateStmtCallbackArgument(SourceWriter srcWriter,
      String rowType, String onSuccessStmt) {
    srcWriter.println(", new "
        + getClassName(DataServiceStatementCallback.class) + "<" + rowType
        + ">() {");
    srcWriter.indent();
    srcWriter.println("public void onSuccess("
        + getClassName(SQLTransaction.class) + " t, "
        + getClassName(SQLResultSet.class) + "<" + rowType + "> r) {");
    srcWriter.indentln(onSuccessStmt);
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.print("}");
  }

  /**
   * Fabricates the name of a variable for use in a method body.
   * 
   * <p>
   * This method ensures that the returned name is not used as parameter name.
   * </p>
   */
  private String getVariableName(String name, JParameter[] params) {
    for (JParameter param : params) {
      if (name.equals(param.getName())) {
        return getVariableName("_" + name, params);
      }
    }
    return name;
  }

  /**
   * Returns either <code>readTransaction</code> or <code>transaction</code>
   * depending in the nature of the provided SQL statements.
   */
  private String getTxMethod(SQL sql) {
    for (String s : sql.value()) {
      if (s.trim().toUpperCase().indexOf("SELECT") == -1) {
        return "transaction";
      }
    }
    return "readTransaction";
  }

  /**
   * Returns the name of the specified clazz, which can be safely emitted in the
   * generated sourcecode.
   */
  private String getClassName(Class<?> clazz) {
    return shortenName(clazz.getCanonicalName());
  }

  /**
   * Returns the name of the specified type, which can be safely emitted in the
   * generated sourcecode.
   */
  private String getClassName(JType type) {
    if (type.isPrimitive() != null) {
      return type.isPrimitive().getSimpleSourceName();
    }
    
    StringBuilder sb = new StringBuilder(shortenName(type.getQualifiedSourceName()));

    if (type.isParameterized() != null && type.isParameterized().getTypeArgs().length > 0) {
      sb.append('<');
      boolean needComma = false;
      for (JType typeArg : type.isParameterized().getTypeArgs()) {
        if (needComma) {
          sb.append(", ");
        } else {
          needComma = true;
        }
        sb.append(shortenName(typeArg.getParameterizedQualifiedSourceName()));
      }
      sb.append('>');
    }

    return sb.toString();
  }

  /**
   * Returns the shortest name of the specified className.
   * 
   * <p>
   * The package part is removed if it is either <code>java.lang</code> or if
   * the class is part of the import list (see {@link #IMPORTED_CLASSES}).
   * </p>
   */
  private String shortenName(String className) {
    int index = className.lastIndexOf('.');
    if (index == -1) {
      // No package name (primitive?)
      return className;
    }
    String packageName = className.substring(0, index);
    if ("java.lang".equals(packageName)) {
      return className.substring(index + 1);
    }
    for (String i : IMPORTED_CLASSES) {
      if (i.equals(className)) {
        return className.substring(index + 1);
      }
    }
    return className;
  }

  /**
   * Returns the {@link TransactionCallback} type to use for the specified
   * {@link Callback} type.
   * 
   * @param logger
   * @param callbackType a {@link Callback} (sub)type
   * @return the name of the TransactionCallback impl to use
   * @throws UnableToCompleteException
   */
  private String getTransactionCallbackClassName(TreeLogger logger,
      JType callbackType) throws UnableToCompleteException {
    if (isType(callbackType, ListCallback.class)) {
      return getClassName(DataServiceTransactionCallbackListCallback.class)
          + "<" + getTypeParameter(logger, callbackType) + ">";
    }
    if (isType(callbackType, VoidCallback.class)) {
      return getClassName(DataServiceTransactionCallbackVoidCallback.class);
    }
    return getClassName(DataServiceTransactionCallback.class);
  }

  /**
   * Returns the (first) Type parameter of the specified type.
   */
  private String getTypeParameter(TreeLogger logger, JType type)
      throws UnableToCompleteException {
    JClassType[] typeArgs = type.isParameterized().getTypeArgs();
    if (typeArgs == null || typeArgs.length == 0) {
      logger.log(TreeLogger.ERROR, "The " + getClassName(ListCallback.class)
          + " callback *must* have a type parameter!");
      throw new UnableToCompleteException();
    }
    return shortenName(typeArgs[0].getQualifiedSourceName());
  }

  /**
   * Returns <code>true</code> if the specified types are the same.
   */
  private boolean isType(JType type, Class<?> clazz) {
    return type.getQualifiedSourceName().equals(clazz.getCanonicalName());
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
   * <li><code>INSERT INTO clickcount (clicked) VALUES (?)</code></li>
   * <li><code>when.getTime()</code></li>
   * </ul>
   */
  private List<String> getPreparedStatementSql(TreeLogger logger, String stmt)
      throws UnableToCompleteException {
    List<String> result = new ArrayList<String>();
    StringBuilder sql = new StringBuilder();
    StringBuilder param = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < stmt.length(); i++) {
      char ch = stmt.charAt(i);
      switch (ch) {
        case '{':
          if (depth == 0) {
            // Start a parameter:
            param = new StringBuilder();
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
            result.add(s);
            sql.append('?');
          } else if (depth < 0) {
            logger.log(TreeLogger.ERROR,
                "Parameter expression in SQL statement '" + stmt
                    + "' is not closed correctly! Too many closing brace(s)");
            throw new UnableToCompleteException();
          }
          break;
        default:
          if (depth == 0) {
            sql.append(ch);
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
    result.add(0, sql.toString().trim());
    return result;
  }

  /**
   * Returns a SourceWriter which is prepared to write the class' body.
   */
  private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx,
      JClassType dataService) {
    JPackage serviceIntfPkg = dataService.getPackage();
    String packageName = serviceIntfPkg == null ? "" : serviceIntfPkg.getName();
    PrintWriter printWriter = ctx.tryCreate(logger, packageName,
        getProxySimpleName(dataService));

    if (printWriter == null) {
      // Proxy already exists.
      return null;
    }

    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, getProxySimpleName(dataService));

    for (String imp : IMPORTED_CLASSES) {
      composerFactory.addImport(imp);
    }

    composerFactory.setSuperclass(getClassName(BaseDataService.class));
    composerFactory.addImplementedInterface(getClassName(dataService));

    composerFactory.setJavaDocCommentForClass("Generated by {@link "
        + getClassName(getClass()) + "}");

    return composerFactory.createSourceWriter(ctx, printWriter);
  }

  /**
   * Returns the fully qualified name of the generated class.
   */
  private String getProxyQualifiedName(JClassType dataService) {
    return (dataService.getPackage() == null ? ""
        : dataService.getPackage().getName() + ".")
        + getProxySimpleName(dataService);
  }

  /**
   * Returns the name of the generated class.
   */
  private String getProxySimpleName(JClassType dataService) {
    return dataService.getName() + "_SqlProxy";
  }
}
