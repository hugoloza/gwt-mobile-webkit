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

package com.google.code.gwt.database.client.util;

import com.google.code.gwt.database.rebind.DataServiceGenerator;

/**
 * Provides several String manipulation routines used by the
 * {@link DataServiceGenerator} and some client classes.
 * 
 * @author bguijt
 */
public class StringUtils {

  /**
   * Concatenates the Numbers of the specified iterable with the specified join
   * in between.
   */
  public static String joinCollectionNumber(
      Iterable<? extends Number> iterable, String join) {
    StringBuilder sb = new StringBuilder();
    for (Number n : iterable) {
      if (sb.length() > 0) {
        sb.append(join);
      }
      sb.append(n);
    }
    return sb.toString();
  }

  /**
   * Concatenates the Strings of the specified iterable with the specified join
   * in between, and escapes the Strings as String literals.
   * 
   * <p>
   * This means the output looks like:
   * <code>"Pierre", "Restaurant \"La Place\""</code>
   * </p>
   */
  public static String joinEscapedCollectionString(Iterable<String> iterable,
      String join) {
    StringBuilder sb = new StringBuilder();
    for (String s : iterable) {
      if (sb.length() > 0) {
        sb.append(join);
      }
      appendEscaped(sb, s);
    }
    return sb.toString();
  }

  /**
   * Returns the String literal of the specified String s
   */
  public static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    appendEscaped(sb, s);
    return sb.toString();
  }

  /**
   * Appends the String literal of the specified String s to the specified
   * StringBuilder sb
   */
  public static void appendEscaped(StringBuilder sb, String s) {
    sb.append("\"");
    for (int i = 0; i < s.length(); i++) {
      appendEscapedChar(sb, s.charAt(i));
    }
    sb.append("\"");
  }

  /**
   * Appends the specified char ch as a String literal to the specified
   * StringBuilder sb
   */
  public static void appendEscapedChar(StringBuilder sb, char ch) {
    switch (ch) {
      case '"':
        sb.append("\\\"");
        break;
      case '\\':
        sb.append("\\\\");
        break;
      case '\n':
        sb.append("\\\n");
        break;
      case '\r':
        sb.append("\\\r");
        break;
      case '\t':
        sb.append("\\\t");
        break;
      default:
        sb.append(ch);
    }
  }
}
