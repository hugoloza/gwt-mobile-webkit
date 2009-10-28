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

package com.google.code.gwt.database.client.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Provides the SQL statement(s) to be executed when the annotated method is
 * called.
 * 
 * <p>
 * Provide any statement parameters between curly braces, e.g.:
 * </p>
 * 
 * <pre>
 * &#x40;SQL("INSERT INTO mytable (when, name) VALUES(<b>{when.getTime()}</b>, <b>{name}</b>)")
 * void insertData(Date <b>when</b>, String <b>name</b>, VoidCallback callback);
 * </pre>
 * 
 * <p>
 * Up to now (oct. 2009) all HTML5 Database implementations use SQLite, which
 * has its own <a href="http://www.sqlite.org/lang.html">SQL flavor</a>.
 * </p>
 * 
 * @see <a href="http://www.sqlite.org/lang.html">SQLite3 SQL Language
 *      Reference</a>
 * @author bguijt
 */
@Documented
@Target(ElementType.METHOD)
public @interface SQL {

  /**
   * Provides one or more SQL statements (of any <code>SELECT</code>,
   * <code>INSERT</code>, <code>UPDATE</code> etc. type) to be executed whenever
   * the annotated method is called.
   */
  String[] value();
}
