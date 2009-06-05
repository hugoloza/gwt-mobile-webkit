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

package com.google.code.gwt.database.sample.hellodatabase.client;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author bguijt
 */
public class ClickRow extends JavaScriptObject {

  protected ClickRow() {}
  
  /**
   * @return the 'clicked' property (an integer) as a Java Date (SQLite does not support DATE types).
   */
  public final native Date getClicked() /*-{
    var dateClicked = new Date();
    dateClicked.setTime(this.clicked);
    return dateClicked;
  }-*/;
}
