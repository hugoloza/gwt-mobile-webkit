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

package com.google.code.gwt.database.client.service.impl;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.List;

import com.google.code.gwt.database.rebind.DataServiceGenerator;

/**
 * Contains utility methods which are used by the service methods generated by
 * the {@link DataServiceGenerator}.
 * 
 * @author bguijt
 */
public class DataServiceUtils {

  public static int addParameter(StringBuilder sql, Object[] params, int offset, boolean[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, byte[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, short[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, char[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, int[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, long[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, float[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, double[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, Object[] array) {
    for (int i=0; i<array.length; i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array[i];
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, AbstractSequentialList<?> array) {
    return addParameter(sql, params, offset, (Iterable<?>) array);
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, List<?> array) {
    for (int i=0; i<array.size(); i++) {
      if (i > 0) sql.append(",");
      sql.append("?");
      params[offset++] = array.get(i);
    }
    return offset;
  }

  public static int addParameter(StringBuilder sql, Object[] params, int offset, Iterable<?> array) {
    int i = offset;
    for (Object _ : array) {
      if (i > offset) sql.append(",");
      sql.append("?");
      params[i++] = _;
    }
    return i;
  }
  
  public static int getSize(Iterable<?> array) {
    if (array instanceof Collection<?>) {
      return ((Collection<?>) array).size();
    }
    int size = 0;
    for (@SuppressWarnings("unused") Object _ : array) {
      size++;
    }
    return size;
  }
}