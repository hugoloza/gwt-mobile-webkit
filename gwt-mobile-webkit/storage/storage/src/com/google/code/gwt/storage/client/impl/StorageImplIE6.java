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

package com.google.code.gwt.storage.client.impl;

import com.google.code.gwt.storage.client.StorageEventHandler;
import com.google.gwt.user.client.DOM;

/**
 * IE6-specific implementation of the Web Storage using the userData Behavior.
 * 
 * @author coneill
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/ms531424%28VS.85%29.aspx">userData
 *      Behavior</a>
 */
public class StorageImplIE6 extends StorageImpl {

  protected static String id;
  
  /**
   * Constructor
   */
  public StorageImplIE6() {
    initJSON();

    if (id == null) {
      id = DOM.createUniqueId();
      init(id);
    }
  }

  /**
   * Generate the userData behavior storage object
   * 
   * @return
   */
  private native void init(String id) /*-{
    var el = $wnd.document.createElement('div');

    // set element properties
    el.id = id;
    el.style.display = 'none';
    el.addBehavior('#default#userData');

    // append element to body
    $wnd.document.body.appendChild(el);
    $wnd.ieLocalStorageEl = el;

    this.@com.google.code.gwt.storage.client.impl.StorageImplIE6::load()();

    // Since userData does not have clear() and getKey(index) methods, we have to 
    // store a cached index of stored keys that we can reference if needed.
    var indexId = id + '_localIndex';
    $wnd.ieLocalStorageEl.indexId = indexId;
    var index = $wnd.ieLocalStorageEl.getAttribute(indexId);
    if (index == null || index == "") {
      $wnd.ieLocalStorageEl.index = {}; // create new copy
    } else {
      // get the existing index as a string and decode to a javascript object
      $wnd.ieLocalStorageEl.index = json_decode(index);
    }
  }-*/;

  /**
   * Local storage is provided through userData Behavior
   */
  @Override
  public native boolean isLocalStorageSupported() /*-{
    return typeof $wnd.ActiveXObject != "undefined";
  }-*/;

  /**
   * Session storage is not supported in IE6/IE7
   */
  @Override
  public boolean isSessionStorageSupported() {
    return false;
  }

  /**
   * In userData getItem(key) is re-mapped to <a
   * href="http://msdn.microsoft.com/en-us/library/ms531348%28v=VS.85%29.aspx"
   * >getAttribute(key)</a>
   */
  @Override
  public native String getItem(String storage, String key) /*-{
    // clean key
    var cleanKey = $wnd.Storage.esc(key);
    this.@com.google.code.gwt.storage.client.impl.StorageImplIE6::load()();
    return storage.getAttribute(cleanKey);
  }-*/;

  /**
   * Returns the number of items in this Storage.
   * 
   * @return number of items in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-l">W3C Web
   *      Storage - Storage.length()</a>
   */
  @Override
  public native int getLength(String storage) /*-{
    this.@com.google.code.gwt.storage.client.impl.StorageImplIE6::load()();

    if ($wnd.ieLocalStorageEl.index == null)
      return 0;
    else
      return $wnd.Storage.size($wnd.ieLocalStorageEl.index);
  }-*/;

  /**
   * In userData removeItem(key) is re-mapped to <a
   * href="http://msdn.microsoft.com/en-us/library/ms531401%28v=VS.85%29.aspx"
   * >removeAttribute(key)</a>
   */
  @Override
  public native void removeItem(String storage, String key) /*-{
    // clean key
    var cleanKey = $wnd.Storage.esc(key);

    $wnd.ieLocalStorageEl.removeAttribute(cleanKey);
    delete $wnd.ieLocalStorageEl.index[cleanKey];
    this.@com.google.code.gwt.storage.client.impl.StorageImplIE6::save()();
  }-*/;

  /**
   * Key is not natively supported in userData so we have to get all of the keys
   * from our cached index
   */
  @Override
  public native String key(String storage, int index) /*-{
    return $wnd.Storage.getObjectKeys($wnd.ieLocalStorageEl.index)[index];
  }-*/;

  /**
   * In userData setItem(key, value) is re-mapped to <a
   * href="http://msdn.microsoft.com/en-us/library/ms531404%28v=VS.85%29.aspx"
   * >setAttribute(key, value)</a>
   */
  @Override
  public native void setItem(String storage, String key, String data) /*-{
    // clean key
    var cleanKey = $wnd.Storage.esc(key);

    $wnd.ieLocalStorageEl.setAttribute(cleanKey, data);
    $wnd.ieLocalStorageEl.index[cleanKey] = data;
    this.@com.google.code.gwt.storage.client.impl.StorageImplIE6::save()();
  }-*/;

  /**
   * TODO: IE6 does not support storage events
   */
  @Override
  public void addStorageEventHandler(StorageEventHandler handler) {
  }

  /**
   * The load method reads information from the userData store.
   * http://msdn.microsoft.com/en-us/library/ms531395%28v=VS.85%29.aspx
   */
  protected native void load() /*-{
    $wnd.ieLocalStorageEl.load($wnd.ieLocalStorageEl.id);
  }-*/;

  /**
   * Saves an object participating in userData persistence to a UserData store.
   * http://msdn.microsoft.com/en-us/library/ms531403%28v=VS.85%29.aspx
   */
  protected native void save() /*-{
    // before we save we need to persist the storage index
    var jsonVal = json_encode($wnd.ieLocalStorageEl.index);
    $wnd.ieLocalStorageEl.setAttribute($wnd.ieLocalStorageEl.indexId, jsonVal);

    // now save
    $wnd.ieLocalStorageEl.save($wnd.ieLocalStorageEl.id);
  }-*/;

  /**
   * Clear out the storage by iterating over the keys. userData does not provide
   * a native method for this.
   */
  @Override
  public native void clear(String storage) /*-{
    var keys = $wnd.Storage.getObjectKeys($wnd.ieLocalStorageEl.index);
    for (var i = 0; i < keys.length; i++) {
      var key = keys[i];

      if (key != null) {
        // first delete the object from storage
        $wnd.ieLocalStorageEl.removeAttribute(key);

        // now delete the key from the cached index
        delete $wnd.ieLocalStorageEl.index[key];
      }
    }

    // persist the changes
    this.@com.google.code.gwt.storage.client.impl.StorageImplIE6::save()();
  }-*/;

  /**
   * We need to include some json utility methods to help with the encoding of
   * the stored index for the datastore
   * 
   * Code is taken from
   * http://code.google.com/p/jquery-json/source/browse/trunk/jquery.json.js
   */
  public native void initJSON() /*-{
    function toIntegersAtLease(n) { 
      // Format integers to have at least two digits.
      return n < 10 ? '0' + n : n;
    }

    Date.prototype.toJSON = function(date) {
      // Yes, it polutes the Date namespace, but we'll allow it here, as
      // it's damned useful.
      return date.getUTCFullYear()   + '-' +
           toIntegersAtLease(date.getUTCMonth() + 1) + '-' +
           toIntegersAtLease(date.getUTCDate());
    };

    var escapeable = /["\\\x00-\x1f\x7f-\x9f]/g;
    var meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        }

    if ($wnd.Storage == undefined)
      $wnd.Storage = {};

    $wnd.Storage.quoteString = function(string) {
      // Places quotes around a string, intelligently.
      // If the string contains no control characters, no quote characters, and no
      // backslash characters, then we can safely slap some quotes around it.
      // Otherwise we must also replace the offending characters with safe escape
      // sequences.
      if (escapeable.test(string)) {
        return '"' + string.replace(escapeable, function (a) {
          var c = meta[a];
          if (typeof c === 'string') {
            return c;
          }
          c = a.charCodeAt();
          return '\\u00' + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
        }) + '"'
      }
      return '"' + string + '"';
    }

    $wnd.Storage.toJSON = function(o, compact) {
      var type = typeof(o);

      if (type == "undefined")
        return "undefined";
      else if (type == "number" || type == "boolean")
        return o + "";
      else if (o === null)
        return "null";

      // Is it a string?
      if (type == "string") {
        return $wnd.Storage.quoteString(o);
      }

      // Does it have a .toJSON function?
      if (type == "object" && typeof o.toJSON == "function") 
        return o.toJSON(compact);

      // Is it an array?
      if (type != "function" && typeof(o.length) == "number") {
        var ret = [];
        for (var i = 0; i < o.length; i++) {
          ret.push( $wnd.Storage.toJSON(o[i], compact) );
        }
        if (compact)
          return "[" + ret.join(",") + "]";
        else
          return "[" + ret.join(", ") + "]";
      }

      // If it's a function, we have to warn somebody!
      if (type == "function") {
        throw new TypeError("Unable to convert object of type 'function' to json.");
      }

      // It's probably an object, then.
      ret = [];
      for (var k in o) {
        var name;
        var type = typeof(k);

        if (type == "number")
          name = '"' + k + '"';
        else if (type == "string")
          name = $wnd.Storage.quoteString(k);
        else
          continue;  //skip non-string or number keys

        val = $wnd.Storage.toJSON(o[k], compact);
        if (typeof(val) != "string") {
          // skip non-serializable values
          continue;
        }

        if (compact)
          ret.push(name + ":" + val);
        else
          ret.push(name + ": " + val);
      }
      return "{" + ret.join(", ") + "}";
    }

    $wnd.Storage.compactJSON = function(o) {
      return Storage.toJSON(o, true);
    }

    $wnd.Storage.evalJSON = function(src) {
      // Evals JSON that we know to be safe.
      return eval("(" + src + ")");
    }

    // Evals JSON in a way that is *more* secure.
    $wnd.Storage.secureEvalJSON = function(src) {
      var filtered = src;
      filtered = filtered.replace(/\\["\\\/bfnrtu]/g, '@');
      filtered = filtered.replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']');
      filtered = filtered.replace(/(?:^|:|,)(?:\s*\[)+/g, '');

      if (/^[\],:{}\s]*$/.test(filtered))
        return eval("(" + src + ")");
      else
        throw new SyntaxError("Error parsing JSON, source is not valid.");
    }

    // function to encode objects to JSON strings
    json_encode = $wnd.Storage.toJSON || Object.toJSON || (window.JSON && (JSON.encode || JSON.stringify)),

    // function to decode objects from JSON strings
    json_decode = $wnd.Storage.secureEvalJSON || (window.JSON && (JSON.decode || JSON.parse));

    // function to remove a value from an array
    $wnd.Storage.size = function(el) {
      if (el == null)
        return 0;
  
      var count = 0;
      for (var k in el) {
        if (el.hasOwnProperty(k)) {
          ++count;
        }
      }
  
      return count;
    }

    //function to return the object methods as an array
    $wnd.Storage.getObjectKeys = function(el) {
      if (el == null)
        return [];
 
      var keys = [];
      for (var key in el) {
        keys.push(key);
      }
      return keys;
    }

    // replace invalid characters
    $wnd.Storage.esc = function(str) {
      return str.replace(/_/g, '__').replace(/ /g, '_s').replace("'", "");
    };
  }-*/;
}
