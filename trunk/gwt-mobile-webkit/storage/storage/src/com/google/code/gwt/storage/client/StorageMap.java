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

/**
 * Exposes the local/session {@link Storage} as a standard mutable {@link Map}.
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

  public boolean containsKey(Object key) {
    return storage.getItem(String.valueOf(key)) != null;
  }

  public boolean containsValue(Object value) {
    for (int i = 0; i < size(); i++) {
      if (storage.getItem(storage.key(i)).equals(value)) {
        return true;
      }
    }
    return false;
  }

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
    return storage.getItem(String.valueOf(key));
  }

  /**
   * adds (or overwrites) a new key/value pair in the Storage.
   * 
   * @param key the key identifying the value
   * @param value the value associated with the key
   * @see Storage#setItem(String, String)
   */
  public String put(String key, String value) {
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
    String k = String.valueOf(key);
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

  /*
   * Represents a Map.Entry to a Storage item
   */
  private class StorageEntry implements Map.Entry<String, String> {
    private int index;

    public StorageEntry(int index) {
      this.index = index;
    }

    public String getKey() {
      return storage.key(index);
    }

    public String getValue() {
      return storage.getItem(getKey());
    }

    public String setValue(String value) {
      String key = getKey();
      String oldValue = storage.getItem(key);
      storage.setItem(key, value);
      return oldValue;
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
        return new StorageEntry(index);
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
      }
      throw new IllegalStateException("Cannot remove() Entry - index=" + index
          + ", size=" + size());
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
      Map.Entry e = getEntry(o);
      if (e == null) {
        return false;
      }
      Object key = e.getKey();
      return containsKey(key) && eq(get(key), e.getValue());
    }

    @SuppressWarnings("unchecked")
    private Map.Entry getEntry(Object o) {
      if (!(o instanceof Map.Entry)) {
        return null;
      }
      return (Map.Entry) o;
    }

    public Iterator<Map.Entry<String, String>> iterator() {
      return new StorageEntryIterator();
    }

    public boolean remove(Object o) {
      return StorageMap.this.remove(o) != null;
    }

    public int size() {
      return StorageMap.this.size();
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
  }
}
