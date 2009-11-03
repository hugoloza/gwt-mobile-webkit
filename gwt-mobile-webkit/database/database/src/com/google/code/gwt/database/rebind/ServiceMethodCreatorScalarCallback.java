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

import com.google.code.gwt.database.client.service.callback.scalar.ScalarCallback;
import com.google.code.gwt.database.client.service.callback.scalar.StatementCallbackScalarCallback;
import com.google.code.gwt.database.client.service.callback.scalar.TransactionCallbackScalarCallback;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Represents a ServiceMethodCreator for the {@link ScalarCallback} type.
 * 
 * @author bguijt
 */
public class ServiceMethodCreatorScalarCallback extends ServiceMethodCreator {

  @Override
  public void generateOnTransactionStartBody() throws UnableToCompleteException {
    generateExecuteSqlStatement("new "
        + genUtils.getClassName(StatementCallbackScalarCallback.class) + "<"
        + genUtils.getTypeParameter(service, callback.getType()) + ">(this)");
  }

  @Override
  protected String getTransactionCallbackClassName()
      throws UnableToCompleteException {
    return genUtils.getClassName(TransactionCallbackScalarCallback.class) + "<"
        + genUtils.getTypeParameter(service, callback.getType()) + ">";
  }
}
