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

import java.util.Collection;
import java.util.Date;

import com.google.code.gwt.database.client.service.DataService;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.code.gwt.database.client.service.annotation.Connection;
import com.google.code.gwt.database.client.service.annotation.SQL;

/**
 * Demo database service to the 'ClckCnt' database.
 * 
 * @author bguijt
 */
@Connection(name="ClckCnt", version="1.0", description="Click Counter",
    maxsize=10000)
public interface ClickCountDataService extends DataService {

  /**
   * Makes sure that the 'clickcount' table exists in the Database.
   */
  @SQL(stmt="CREATE TABLE IF NOT EXISTS clickcount ("
      + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
      + "clicked INTEGER)")
  void initTable(VoidCallback callback);

  /**
   * Records a Click value, and obtains the list of all recorded clicks.
   */
  @SQL(stmt={
      "INSERT INTO clickcount (clicked) VALUES ({when.getTime()})",
      "SELECT clicked FROM clickcount"
      })
  void insertClick(Date when, ListCallback<ClickRow> callback);

  /**
   * Records a collection of click values
   */
  @SQL(stmt="INSERT INTO clickcount (clicked) VALUES ({#.getTime()})",
      foreach="clicks")
  void insertClicks(Collection<Date> clicks, VoidCallback callback);

  /**
   * Obtains the number of clicks recorded in the database.
   */
  @SQL(stmt="SELECT count(*) FROM clickcount")
  void getClickCount(ScalarCallback<Integer> callback);
}
