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
import com.google.code.gwt.database.client.service.RowIdListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.code.gwt.database.client.service.annotation.Connection;
import com.google.code.gwt.database.client.service.annotation.Select;
import com.google.code.gwt.database.client.service.annotation.Update;

/**
 * Demo database service to the 'ClckCnt' database.
 * 
 * @author bguijt
 */
@Connection(name="ClckCnt", version="1.0",
    description="Click Counter", maxsize=10000)
public interface ClickCountDataService extends DataService {

  /**
   * Makes sure that the 'clickcount' table exists in the Database.
   */
  @Update("CREATE TABLE IF NOT EXISTS clickcount ("
      + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
      + "clicked INTEGER)")
  void initTable(VoidCallback callback);

  /**
   * Records a Click value, and obtains the ID of the inserted record.
   */
  @Update("INSERT INTO clickcount (clicked) VALUES ({when.getTime()})")
  void insertClick(Date when, RowIdListCallback callback);

  /**
   * Returns the clicks with the specified IDs.
   */
  @Select("SELECT clicked FROM clickcount WHERE id IN ({ids})")
  void getClicksWithIds(Collection<Integer> ids, ListCallback<ClickRow> callback);
  
  /**
   * Records a collection of click values
   */
  @Update(sql="INSERT INTO clickcount (clicked) VALUES ({_.getTime()})",
          foreach="clicks")
  void insertClicks(Collection<Date> clicks, RowIdListCallback callback);

  /**
   * Returns all clicks.
   */
  @Select("SELECT clicked FROM clickcount")
  void getClicks(ListCallback<ClickRow> callback);
  
  /**
   * Obtains the number of clicks recorded in the database.
   */
  @Select("SELECT count(*) FROM clickcount")
  void getClickCount(ScalarCallback<Integer> callback);
}
