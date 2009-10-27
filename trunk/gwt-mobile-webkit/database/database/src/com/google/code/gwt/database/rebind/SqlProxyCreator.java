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
import java.util.List;

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.DatabaseException;
import com.google.code.gwt.database.client.SQLTransaction;
import com.google.code.gwt.database.client.TransactionCallback;
import com.google.code.gwt.database.client.service.BaseDataService;
import com.google.code.gwt.database.client.service.Callback;
import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.DataServiceStatementCallbackListCallback;
import com.google.code.gwt.database.client.service.DataServiceStatementCallbackScalarCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackListCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackScalarCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackVoidCallback;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
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
import com.google.gwt.dev.util.Util;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Helper class for the {@link DataServiceGenerator}.
 * 
 * <p>
 * This class is specifically instantiated for a single DataService proxy.
 * </p>
 * 
 * @author bguijt
 */
public class SqlProxyCreator {

  private static final String PROXY_SUFFIX = "_SqlProxy";

  private static final String[] IMPORTED_CLASSES = new String[] {
      Database.class.getCanonicalName(),
      SQLTransaction.class.getCanonicalName(), BaseDataService.class.getName(),
      VoidCallback.class.getCanonicalName(),
      ListCallback.class.getCanonicalName(),
      ScalarCallback.class.getCanonicalName(),
      DataServiceStatementCallbackScalarCallback.class.getCanonicalName(),
      DataServiceStatementCallbackListCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackVoidCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackScalarCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackListCallback.class.getCanonicalName(),
      DatabaseException.class.getCanonicalName()};

  private TreeLogger logger;
  private GeneratorContext context;
  private JClassType dataService;
  private SourceWriter sw;

  /**
   * <code>true</code> if the dataService directly extends DataService,
   * <code>false</code> otherwise.
   */
  private boolean isBaseType;

  public SqlProxyCreator(TreeLogger logger, GeneratorContext context,
      JClassType dataService) {
    this.logger = logger;
    this.context = context;
    this.dataService = dataService;
  }

  public String create() throws UnableToCompleteException {
    sw = getSourceWriter();
    if (sw == null) {
      // No need to generate, it's already done. Return name of generated class.
      return getProxyQualifiedName();
    }

    generateProxyConstructor();
    if (isBaseType) {
      generateProxyOpenDatabaseMethod();
      generateProxyGetDatabaseDetailsMethod();
    }

    // Generate service methods for each defined interface method:
    for (JMethod method : dataService.getMethods()) {
      generateProxyServiceMethod(method);
    }

    sw.commit(logger);

    return getProxyQualifiedName();
  }

  /**
   * Generates the constructor.
   */
  private void generateProxyConstructor() {
    sw.println("public " + getProxySimpleName() + "() {");
    sw.indent();
    sw.println("// default empty constructor");
    sw.outdent();
    sw.println("}");
  }

  /**
   * Generates the {@link BaseDataService#openDatabase()} method
   */
  private void generateProxyOpenDatabaseMethod() {
    Connection con = dataService.getAnnotation(Connection.class);
    sw.beginJavaDocComment();
    sw.print("Opens the '" + con.name() + "' Database version " + con.version());
    sw.endJavaDocComment();
    sw.println("public final " + getClassName(Database.class)
        + " openDatabase() throws " + getClassName(DatabaseException.class)
        + " {");
    sw.indentln("return " + getClassName(Database.class) + ".openDatabase(\""
        + Generator.escape(con.name()) + "\", \""
        + Generator.escape(con.version()) + "\", \""
        + Generator.escape(con.description()) + "\", " + con.maxsize() + ");");
    sw.println("}");
  }

  /**
   * Generates the {@link BaseDataService#getDatabaseDetails()} method.
   */
  private void generateProxyGetDatabaseDetailsMethod() {
    Connection con = dataService.getAnnotation(Connection.class);
    String toReturn = "'" + Generator.escape(con.name()) + "' version "
        + Generator.escape(con.version());
    sw.beginJavaDocComment();
    sw.print("Returns the <code>" + toReturn + "</code> string.");
    sw.endJavaDocComment();
    sw.println("public final String getDatabaseDetails() {");
    sw.indentln("return \"" + toReturn + "\";");
    sw.println("}");
  }

  /**
   * Generates the proxy method implementing the specified service.
   */
  private void generateProxyServiceMethod(JMethod service)
      throws UnableToCompleteException {
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

    generateProxyServiceMethodJavadoc(service);

    sw.print("public final void " + service.getName() + "(");
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        sw.print(", ");
      }
      sw.print("final " + getClassName(params[i].getType()) + " "
          + params[i].getName());
    }
    sw.println(") {");
    sw.indent();

    // Determine unique variable names:
    String dbVarName = getVariableName("db", params);
    String txVarName = getVariableName("tx", params);

    // We need to obtain a Database connection.
    sw.println("final " + getClassName(Database.class) + " " + dbVarName
        + " = getDatabase(" + callback.getName() + ");");
    sw.println("if (" + dbVarName + " != null) {");
    sw.indent();

    sw.println(dbVarName + "." + getTxMethod(sql) + "(new "
        + getTransactionCallbackClassName(callback.getType()) + "("
        + callback.getName() + ") {");
    sw.indent();

    sw.println("public void onTransactionStart("
        + getClassName(SQLTransaction.class) + " " + txVarName + ") {");
    sw.indent();
    // Write a tx.executeSql() call for each SQL statement:
    for (int i = 0; i < sql.value().length; i++) {
      generateExecuteSqlStatement(service, callback,
          i == (sql.value().length - 1), sql.value()[i]);
    }

    // ends onTransactionStart()
    sw.outdent();
    sw.println("}");

    // ends new TransactionCallback() and (read)transaction() call
    sw.outdent();
    sw.println("});");

    // ends if (db != null)
    sw.outdent();
    sw.println("}");

    // ends service method
    sw.outdent();
    sw.println("}");
  }

  /**
   * Generates the Javadoc for the specified service method. The usefulness of
   * this code is arguable low :-)
   */
  private void generateProxyServiceMethodJavadoc(JMethod service)
      throws UnableToCompleteException {
    SQL sql = service.getAnnotation(SQL.class);
    sw.beginJavaDocComment();
    sw.println("Executes the following "
        + (sql.value().length == 1 ? "SQL statement" : sql.value().length
            + " SQL statements") + ":");
    sw.println("<ul>");
    for (String s : sql.value()) {
      // Add a line for each SQL statement, including some nice markup:
      List<String> prepStmt = getPreparedStatementSql(s);
      String code = Util.escapeXml(prepStmt.get(0));
      for (int i = 1; i < prepStmt.size(); i++) {
        int index = code.indexOf('?');
        code = code.substring(0, index) + "<b>" + prepStmt.get(i) + "</b>"
            + code.substring(index + 1);
      }
      sw.println("<li><code>" + code + "</code></li>");
    }
    sw.print("</ul>");
    sw.endJavaDocComment();
  }

  /**
   * Generates a <code>tx.executeSql(...);</code> call statement.
   * 
   * @param service
   * @param callback the callback defined for the service method
   * @param isLastStatement whether this is the last statement to execute (to
   *          determine callback type)
   * @param stmt the SQL statement to execute
   * @throws UnableToCompleteException
   */
  private void generateExecuteSqlStatement(JMethod service,
      JParameter callback, boolean isLastStatement, String stmt)
      throws UnableToCompleteException {
    List<String> prepStmt = getPreparedStatementSql(stmt);
    sw.print(getVariableName("tx", service.getParameters()) + ".executeSql(\""
        + Generator.escape(prepStmt.get(0)) + "\", ");
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

    // VoidCallback template:
    if (!isLastStatement || isType(callback.getType(), VoidCallback.class)) {
      // No callback to write. Default behaviour is exactly what we need
      // (stop transaction at failures).
    }

    // ListCallback template:
    else if (isType(callback.getType(), ListCallback.class)) {
      String rowType = getTypeParameter(callback.getType());
      sw.print(", new "
          + getClassName(DataServiceStatementCallbackListCallback.class) + "<"
          + rowType + ">(this)");
    }

    // ScalarCallback template:
    else if (isType(callback.getType(), ScalarCallback.class)) {
      String scalarType = getTypeParameter(callback.getType());
      sw.print(", new "
          + getClassName(DataServiceStatementCallbackScalarCallback.class) + "<"
          + scalarType + ">(this)");
    }

    // No expected callback found:
    else {
      logger.log(TreeLogger.ERROR, "Unknown callback type found: "
          + callback.getType().getQualifiedSourceName());
      throw new UnableToCompleteException();
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
   * <li><code>INSERT INTO clickcount (clicked) VALUES (?)</code></li>
   * <li><code>when.getTime()</code></li>
   * </ul>
   */
  private List<String> getPreparedStatementSql(String stmt)
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
   * Returns the {@link TransactionCallback} type to use for the specified
   * {@link Callback} type.
   * 
   * @param callbackType a {@link Callback} (sub)type
   * @return the name of the TransactionCallback impl to use
   * @throws UnableToCompleteException
   */
  private String getTransactionCallbackClassName(JType callbackType)
      throws UnableToCompleteException {
    if (isType(callbackType, ListCallback.class)) {
      // TransactionCallback for the ListCallback:
      return getClassName(DataServiceTransactionCallbackListCallback.class)
          + "<" + getTypeParameter(callbackType) + ">";
    }
    if (isType(callbackType, VoidCallback.class)) {
      // TransactionCallback for the VoidCallback:
      return getClassName(DataServiceTransactionCallbackVoidCallback.class);
    }
    // TransactionCallback for the ScalarCallback:
    return getClassName(DataServiceTransactionCallbackScalarCallback.class)
        + "<" + getTypeParameter(callbackType) + ">";
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

    StringBuilder sb = new StringBuilder(
        shortenName(type.getQualifiedSourceName()));

    if (type.isParameterized() != null
        && type.isParameterized().getTypeArgs().length > 0) {
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
    if ("java.lang".equals(packageName)
        || (dataService.getPackage() != null && dataService.getPackage().getName().equals(
            packageName))) {
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
   * Returns the (first) Type parameter of the specified type.
   */
  private String getTypeParameter(JType type) throws UnableToCompleteException {
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
   * Returns a SourceWriter which is prepared to write the class' body.
   * 
   * @throws UnableToCompleteException
   */
  private SourceWriter getSourceWriter() throws UnableToCompleteException {
    JPackage serviceIntfPkg = dataService.getPackage();
    String packageName = serviceIntfPkg == null ? "" : serviceIntfPkg.getName();
    PrintWriter printWriter = context.tryCreate(logger, packageName,
        getProxySimpleName());

    if (printWriter == null) {
      // Proxy already exists.
      return null;
    }

    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, getProxySimpleName());

    for (String imp : IMPORTED_CLASSES) {
      composerFactory.addImport(imp);
    }

    JClassType superType = dataService.getImplementedInterfaces()[0];
    if (isType(superType, DataService.class)) {
      composerFactory.setSuperclass(getClassName(BaseDataService.class));
      isBaseType = true;
    } else {
      isBaseType = false;
      // the dataService inherits from a different interface.
      // Create another SqlProxyCreator to take care of that interface:
      SqlProxyCreator superClassCreator = new SqlProxyCreator(logger.branch(
          TreeLogger.DEBUG, "Generating proxy methods for superclass '"
              + superType.getQualifiedSourceName() + "'..."), context,
          superType);
      String className = superClassCreator.create();
      composerFactory.setSuperclass(shortenName(className));
    }
    composerFactory.addImplementedInterface(getClassName(dataService));

    composerFactory.setJavaDocCommentForClass("Generated by {@link "
        + getClassName(getClass()) + "}");

    return composerFactory.createSourceWriter(context, printWriter);
  }

  /**
   * Returns the fully qualified name of the generated class.
   */
  private String getProxyQualifiedName() {
    return (dataService.getPackage() == null ? ""
        : dataService.getPackage().getName() + ".")
        + getProxySimpleName();
  }

  /**
   * Returns the name of the generated class.
   */
  private String getProxySimpleName() {
    return dataService.getName() + PROXY_SUFFIX;
  }
}
