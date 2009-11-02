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
import com.google.code.gwt.database.client.service.DataServiceStatementCallbackRowIdListCallback;
import com.google.code.gwt.database.client.service.DataServiceStatementCallbackScalarCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackListCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackRowIdListCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackScalarCallback;
import com.google.code.gwt.database.client.service.DataServiceTransactionCallbackVoidCallback;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.RowIdListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.code.gwt.database.client.service.annotation.Connection;
import com.google.code.gwt.database.client.service.annotation.Select;
import com.google.code.gwt.database.client.service.annotation.Update;
import com.google.code.gwt.database.client.util.StringUtils;
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
      StringUtils.class.getCanonicalName(),
      VoidCallback.class.getCanonicalName(),
      ListCallback.class.getCanonicalName(),
      ScalarCallback.class.getCanonicalName(),
      RowIdListCallback.class.getCanonicalName(),
      DataServiceStatementCallbackScalarCallback.class.getCanonicalName(),
      DataServiceStatementCallbackListCallback.class.getCanonicalName(),
      DataServiceStatementCallbackRowIdListCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackVoidCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackScalarCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackListCallback.class.getCanonicalName(),
      DataServiceTransactionCallbackRowIdListCallback.class.getCanonicalName(),
      DatabaseException.class.getCanonicalName()};

  private static final String VARNAME_SQLTRANSACTION = "tx";
  private static final String VARNAME_ROWIDLISTCALLBACK = "rowIdListCallback";

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
    Select select = service.getAnnotation(Select.class);
    Update update = service.getAnnotation(Update.class);

    // Assertions:
    if (select == null && update == null) {
      logger.log(TreeLogger.ERROR, service.getName()
          + " has no @Select nor @Update annotation");
      throw new UnableToCompleteException();
    }
    if ((select == null || getSql(select).trim().length() == 0)
        && (update == null || getSql(update).trim().length() == 0)) {
      logger.log(TreeLogger.ERROR, service.getName()
          + ": @Select or @Update annotation has no SQL statement");
      throw new UnableToCompleteException();
    }
    JParameter[] params = service.getParameters();
    if (params.length == 0) {
      logger.log(TreeLogger.ERROR, "Method " + service.getName()
          + " must have at least one (callback) parameter");
      throw new UnableToCompleteException();
    }
    JParameter callback = params[params.length - 1];
    if (!isAssignableToType(callback.getType(), Callback.class)) {
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
    String txVarName = getVariableName(VARNAME_SQLTRANSACTION, params);

    sw.println(getTxMethod(service) + "(new "
        + getTransactionCallbackClassName(service, callback.getType()) + "("
        + callback.getName() + ") {");
    sw.indent();

    sw.println("public void onTransactionStart("
        + getClassName(SQLTransaction.class) + " " + txVarName + ") {");
    sw.indent();

    if (update != null) {
      generateOnTransactionStartBody(service, callback, update);
    }
    if (select != null) {
      generateOnTransactionStartBody(service, callback, select);
    }

    // ends onTransactionStart()
    sw.outdent();
    sw.println("}");

    // ends new TransactionCallback() and (read)transaction() call
    sw.outdent();
    sw.println("});");

    // ends service method
    sw.outdent();
    sw.println("}");
  }

  private void generateOnTransactionStartBody(JMethod service,
      JParameter callback, Update update) throws UnableToCompleteException {

    // Holds the name of a pre-instantiated StatementCallback type (if
    // applicable):
    String callbackInstanceName = null;

    if (isType(callback.getType(), RowIdListCallback.class)) {
      // RowIdListCallback used? Use a single instance for each tx.executeSql()
      // call in the iteration:
      String stmtCallbackName = getClassName(DataServiceStatementCallbackRowIdListCallback.class);
      callbackInstanceName = getVariableName(VARNAME_ROWIDLISTCALLBACK,
          service.getParameters());
      sw.println("final " + stmtCallbackName + " " + callbackInstanceName
          + " = new " + stmtCallbackName + "(this);");
    }

    if (update.foreach().trim().length() > 0) {
      // Generate code to loop over a collection to create a tx.executeSql()
      // call for each item.

      // Find the types, parameters, assert not-nulls, etc.:
      JType collection = findType(update.foreach(), service.getParameters());
      if (collection == null) {
        logger.log(TreeLogger.WARN, "The method " + service.getName()
            + " has no parameter named '" + update.foreach()
            + "'. Using Object as the type for the loop variable '_'");
      }
      String forEachType = collection != null ? getTypeParameter(service,
          collection) : null;
      if (forEachType == null) {
        forEachType = "Object";
      }

      sw.println("for (" + forEachType + " _ : " + update.foreach() + ") {");
      sw.indent();
      generateExecuteSqlStatement(service, callback, getSql(update),
          callbackInstanceName);
      sw.outdent();
      sw.println("}");
    } else {
      // Just create the 'static' tx.executeSql() call:
      generateExecuteSqlStatement(service, callback, getSql(update),
          callbackInstanceName);
    }
  }

  private void generateOnTransactionStartBody(JMethod service,
      JParameter callback, Select select) throws UnableToCompleteException {
    // Just create the 'static' tx.executeSql() call:
    generateExecuteSqlStatement(service, callback, getSql(select), null);
  }

  /**
   * Generates the Javadoc for the specified service method. The usefulness of
   * this code is arguable low :-)
   */
  private void generateProxyServiceMethodJavadoc(JMethod service)
      throws UnableToCompleteException {
    Select select = service.getAnnotation(Select.class);
    Update update = service.getAnnotation(Update.class);
    sw.beginJavaDocComment();
    String stmt = null;
    sw.println("Executes the following SQL "
        + (update != null ? "Update" : "Select") + " statement:");
    stmt = update != null ? getSql(update) : getSql(select);
    List<String> prepStmt = getPreparedStatementSql(stmt, service);
    String code = Util.escapeXml(prepStmt.get(0));
    for (int i = 1; i < prepStmt.size(); i++) {
      int index = code.indexOf('?');
      code = code.substring(0, index) + "<b>" + prepStmt.get(i) + "</b>"
          + code.substring(index + 1);
    }
    sw.print("<pre>" + code + "</pre>");
    sw.endJavaDocComment();
  }

  /**
   * Generates a <code>tx.executeSql(...);</code> call statement.
   * 
   * @param service
   * @param callback the callback defined for the service method
   * @param stmt the SQL statement to execute
   * @param callbackInstanceName the name of an already instantiated
   *          StatementCallback class - or <code>null</code> if the callback
   *          needs to be instantiated by the generated code
   * @throws UnableToCompleteException
   */
  private void generateExecuteSqlStatement(JMethod service,
      JParameter callback, String stmt, String callbackInstanceName)
      throws UnableToCompleteException {
    List<String> prepStmt = getPreparedStatementSql(stmt, service);
    sw.print(getVariableName(VARNAME_SQLTRANSACTION, service.getParameters())
        + ".executeSql(" + prepStmt.get(0) + ", ");
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

    // Predefined (instantiated) callback provided:
    if (callbackInstanceName != null) {
      sw.print(", " + callbackInstanceName);
    }

    // VoidCallback template:
    else if (isType(callback.getType(), VoidCallback.class)) {
      // No callback to write. Default behaviour is exactly what we need
      // (stop transaction at failures).
    }

    // ListCallback template:
    else if (isType(callback.getType(), ListCallback.class)) {
      sw.print(", new "
          + getClassName(DataServiceStatementCallbackListCallback.class) + "<"
          + getTypeParameter(service, callback.getType()) + ">(this)");
    }

    // ScalarCallback template:
    else if (isType(callback.getType(), ScalarCallback.class)) {
      sw.print(", new "
          + getClassName(DataServiceStatementCallbackScalarCallback.class)
          + "<" + getTypeParameter(service, callback.getType()) + ">(this)");
    }

    // RowIdsCallback template:
    else if (isType(callback.getType(), RowIdListCallback.class)) {
      sw.print(", new "
          + getClassName(DataServiceStatementCallbackRowIdListCallback.class)
          + "(this)");
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
    JType type = findType(expression, service.getParameters());
    boolean addMultiple = false;
    String typeParam = null;
    if (type != null) {
      if (isAssignableToType(type, Iterable.class)) {
        // OK, we've got our collection. Is the Type parameter 'suitable'?
        typeParam = getTypeParameter(service, type);
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
      sql.append("\" + ").append(getClassName(StringUtils.class)).append(".").append(
          joinMethodName).append("(").append(expression).append(", \",\") + \"");
    } else {
      result.add(expression);
      sql.append('?');
    }
  }

  /**
   * Returns either <code>readTransaction</code> or <code>transaction</code>
   * depending in the nature of the provided SQL statements.
   */
  private String getTxMethod(JMethod service) {
    return (service.getAnnotation(Update.class) == null) ? "readTransaction"
        : "transaction";
  }

  /**
   * Returns the {@link TransactionCallback} type to use for the specified
   * {@link Callback} type.
   * 
   * @param service the service this method applies to
   * @param callbackType a {@link Callback} (sub)type
   * @return the name of the TransactionCallback impl to use
   * @throws UnableToCompleteException
   */
  private String getTransactionCallbackClassName(JMethod service,
      JType callbackType) throws UnableToCompleteException {
    if (isType(callbackType, ListCallback.class)) {
      // TransactionCallback for the ListCallback:
      return getClassName(DataServiceTransactionCallbackListCallback.class)
          + "<" + getTypeParameter(service, callbackType) + ">";
    }
    if (isType(callbackType, VoidCallback.class)) {
      // TransactionCallback for the VoidCallback:
      return getClassName(DataServiceTransactionCallbackVoidCallback.class);
    }
    if (isType(callbackType, ScalarCallback.class)) {
      // TransactionCallback for the ScalarCallback:
      return getClassName(DataServiceTransactionCallbackScalarCallback.class)
          + "<" + getTypeParameter(service, callbackType) + ">";
    }
    // TransactionCallback for the RowIdsCallback:
    return getClassName(DataServiceTransactionCallbackRowIdListCallback.class);
  }

  private String getSql(Select select) {
    return (select.value() != null && select.value().trim().length() == 0)
        ? select.sql() : select.value();
  }

  private String getSql(Update update) {
    return (update.value() != null && update.value().trim().length() == 0)
        ? update.sql() : update.value();
  }

  /**
   * Returns the Type of the parameter with the specified name, or
   * <code>null</code> if not found.
   */
  private JType findType(String name, JParameter[] parameters) {
    for (JParameter param : parameters) {
      if (param.getName().equals(name)) {
        return param.getType();
      }
    }
    return null;
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
  private String getTypeParameter(JMethod service, JType type)
      throws UnableToCompleteException {
    JClassType[] typeArgs = type.isParameterized().getTypeArgs();
    if (typeArgs == null || typeArgs.length == 0) {
      logger.log(TreeLogger.ERROR, "Expected a type parameter on the type "
          + getClassName(type) + " used on service named '" + service.getName()
          + "'");
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
   * Returns <code>true</code> if the specified type is assignable to the
   * specified class <code>assignableTo</code>.
   */
  private boolean isAssignableToType(JType type, Class<?> assignableTo) {
    return type.isClassOrInterface().isAssignableTo(
        context.getTypeOracle().findType(assignableTo.getCanonicalName()));
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
