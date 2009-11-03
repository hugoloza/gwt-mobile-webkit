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

import com.google.code.gwt.database.client.service.callback.rowid.RowIdListCallback;
import com.google.code.gwt.database.client.service.callback.rowid.StatementCallbackRowIdListCallback;
import com.google.code.gwt.database.client.service.callback.rowid.TransactionCallbackRowIdListCallback;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Represents a ServiceMethodCreator for the {@link RowIdListCallback} type.
 * 
 * @author bguijt
 */
public class ServiceMethodCreatorRowIdListCallback extends ServiceMethodCreator {

  @Override
  public void generateOnTransactionStartBody() throws UnableToCompleteException {
    // Use a single instance for each tx.executeSql()
    // call in the iteration:
    String stmtCallbackName = genUtils.getClassName(StatementCallbackRowIdListCallback.class);

    // Holds the name of a pre-instantiated StatementCallback type:
    String callbackInstanceName = GeneratorUtils.getVariableName(
        "rowIdListCallback", service.getParameters());

    sw.println("final " + stmtCallbackName + " " + callbackInstanceName
        + " = new " + stmtCallbackName + "(this);");

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
      generateExecuteSqlStatement(callbackInstanceName);
      sw.outdent();
      sw.println("}");
    } else {
      generateExecuteSqlStatement(callbackInstanceName);
    }
  }

  @Override
  protected String getTransactionCallbackClassName()
      throws UnableToCompleteException {
    return genUtils.getClassName(TransactionCallbackRowIdListCallback.class);
  }
}
