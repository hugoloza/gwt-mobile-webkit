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

package com.google.code.gwt.storage.client;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptException;

/**
 * Exposes the local/session {@link Storage} as a standard {@link Map
 * Map&lt;String, String&gt;}.
 * 
 * <p>
 * The following characteristics are associated with this Map:
 * </p>
 * <ol>
 * <li><em>Mutable</em> - All 'write' methods ({@link #put(String, String)},
 * {@link #putAll(Map)}, {@link #remove(Object)}, {@link #clear()}) operate as
 * intended;</li>
 * <li><em>remove() on Iterators</em> - All remove() operations on available
 * Iterators (from {@link #keySet()}, {@link #entrySet()} and {@link #values()})
 * operate as intended;</li>
 * <li><em>No <code>null</code> values and keys</em> - The Storage doesn't
 * accept keys or values which are <code>null</code>;</li>
 * <li><em>JavaScriptException instead of NullPointerException</em> - Some Map
 * (or other Collection) methods mandate the use of a
 * {@link NullPointerException} if some argument is <code>null</code> (e.g.
 * {@link #remove(Object)} remove(null)). this Map emits
 * {@link JavaScriptException}s instead;</li>
 * <li><em>String values and keys</em> - All keys and values in this Map are
 * String types.</li>
 * </ol>
 * 
 * @author bguijt
 */
public class StorageMap extends AbstractMap<String, String> {

  private Storage storage;
  private StorageEntrySet entrySet;

  /**
   * Creates the Map with the specified Storage as data provider.
   * 
   * @param storage a local/session Storage instance obtained by either
   *          {@link Storage#getLocalStorage()} or
   *          {@link Storage#getSessionStorage()}.
   */
  public StorageMap(Storage storage) {
    this.storage = storage;
  }

  /**
   * Removes all items from the Storage.
   * 
   * @see Storage#clear()
   */
  public void clear() {
    storage.clear();
  }

  /**
   * Returns <code>true</code> if the Storage contains the specified key,
   * <code>false</code> otherwise.
   */
  public boolean containsKey(Object key) {
    return storage.getItem(key.toString()) != null;
  }

  /**
   * Returns <code>true</code> if the Storage contains the specified value,
   * <code>false</code> otherwise (or if the specified key is <code>null</code>
   * ).
   */
  public boolean containsValue(Object value) {
    int s = size();
    for (int i = 0; i < s; i++) {
      if (value.equals(storage.getItem(storage.key(i)))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a Set containing all entries of the Storage.
   */
  public Set<Map.Entry<String, String>> entrySet() {
    if (entrySet == null) {
      entrySet = new StorageEntrySet();
    }
    return entrySet;
  }

  /**
   * Returns the value associated with the specified key in the Storage.
   * 
   * @param key the key identifying the value
   * @see Storage#getItem(String)
   */
  public String get(Object key) {
    if (key == null) {
      return null;
    }
    return storage.getItem(key.toString());
  }

  /**
   * adds (or overwrites) a new key/value pair in the Storage.
   * 
   * @param key the key identifying the value (not <code>null</code>)
   * @param value the value associated with the key (not <code>null</code>)
   * @see Storage#setItem(String, String)
   */
  public String put(String key, String value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException("Key and/or value cannot be null!");
    }
    String old = storage.getItem(key);
    storage.setItem(key, value);
    return old;
  }

  /**
   * Removes the key/value pair from the Storage.
   * 
   * @param key the key identifying the item to remove
   * @return the value associated with the key - <code>null</code> if the key
   *         was not present in the Storage
   * @see Storage#removeItem(String)
   */
  public String remove(Object key) {
    String k = key.toString();
    String old = storage.getItem(k);
    storage.removeItem(k);
    return old;
  }

  /**
   * Returns the number of items in the Storage.
   * 
   * @return the number of items
   * @see Storage#getLength()
   */
  public int size() {
    return storage.getLength();
  }

  private boolean eq(Object a, Object b) {
    if (a == b) {
      return true;
    }
    if (a == null) {
      return false;
    }
    return a.equals(b);
  }

  /*
   * Represents a Map.Entry to a Storage item
   */
  private class StorageEntry implements Map.Entry<String, String> {
    private String key;

    public StorageEntry(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return storage.getItem(key);
    }

    public String setValue(String value) {
      String oldValue = storage.getItem(key);
      storage.setItem(key, value);
      return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (obj == this)
        return true;
      if (!(obj instanceof Map.Entry))
        return false;

      Map.Entry e = (Map.Entry) obj;

      return eq(key, e.getKey()) && eq(getValue(), e.getValue());
    }

    @Override
    public int hashCode() {
      String value = getValue();
      return (key == null ? 0 : key.hashCode())
          ^ (value == null ? 0 : value.hashCode());
    }
  }

  /*
   * Represents an Iterator over all Storage items
   */
  private class StorageEntryIterator implements
      Iterator<Map.Entry<String, String>> {
    private int index = -1;
    private boolean removed = false;

    public boolean hasNext() {
      return index < size() - 1;
    }

    public Map.Entry<String, String> next() {
      if (hasNext()) {
        index++;
        removed = false;
        return new StorageEntry(storage.key(index));
      }
      throw new NoSuchElementException();
    }

    public void remove() {
      if (index >= 0 && index < size()) {
        if (removed) {
          throw new IllegalStateException(
              "Cannot remove() Entry - already removed!");
        }
        storage.removeItem(storage.key(index));
        removed = true;
        index--;
      } else {
        throw new IllegalStateException("Cannot remove() Entry - index="
            + index + ", size=" + size());
      }
    }
  }

  /*
   * Represents a Set<Map.Entry> over all Storage items
   */
  private class StorageEntrySet extends AbstractSet<Map.Entry<String, String>> {
    public void clear() {
      StorageMap.this.clear();
    }

    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
      if (o == null || !(o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry e = (Map.Entry) o;
      Object key = e.getKey();
      return key != null && containsKey(key) && eq(get(key), e.getValue());
    }

    public Iterator<Map.Entry<String, String>> iterator() {
      return new StorageEntryIterator();
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
      if (o == null || !(o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry e = (Map.Entry) o;
      if (e.getKey() == null) {
        return false;
      }
      String key = e.getKey().toString();
      String value = storage.getItem(key);
      if (eq(value, e.getValue())) {
        return StorageMap.this.remove(key) != null;
      }
      return false;
    }

    public int size() {
      return StorageMap.this.size();
    }
  }
}
