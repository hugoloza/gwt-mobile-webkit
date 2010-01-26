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

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
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

  private transient volatile Set<String> keySet = null;
  private transient volatile Collection<String> values = null;

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

  public Set<String> keySet() {
    if (keySet == null) {
      keySet = new AbstractSet<String>() {
        public Iterator<String> iterator() {
          return new Iterator<String>() {
            private Iterator<Entry<String, String>> i = entrySet().iterator();

            public boolean hasNext() {
              return i.hasNext();
            }

            public String next() {
              return i.next().getKey();
            }

            public void remove() {
              i.remove();
            }
          };
        }

        public int size() {
          return StorageMap.this.size();
        }

        public boolean contains(Object k) {
          return StorageMap.this.containsKey(k);
        }

        public void clear() {
          StorageMap.this.clear();
        }
      };
    }
    return keySet;
  }

  public Collection<String> values() {
    if (values == null) {
      values = new AbstractCollection<String>() {
        public Iterator<String> iterator() {
          return new Iterator<String>() {
            private Iterator<Entry<String, String>> i = entrySet().iterator();

            public boolean hasNext() {
              return i.hasNext();
            }

            public String next() {
              return i.next().getValue();
            }

            public void remove() {
              i.remove();
            }
          };
        }

        public int size() {
          return StorageMap.this.size();
        }

        public boolean contains(Object v) {
          return StorageMap.this.containsValue(v);
        }

        public void clear() {
          StorageMap.this.clear();
        }
      };
    }
    return values;
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

      String value = getValue();

      Map.Entry e = (Map.Entry) obj;
      Object oKey = e.getKey();
      Object oValue = e.getValue();

      return eq(key, oKey) && eq(value, oValue);
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
      return containsKey(key) && eq(get(key), e.getValue());
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
      String value = storage.getItem(e.getKey().toString());
      if (eq(value, e.getValue())) {
        return StorageMap.this.remove(e.getKey()) != null;
      }
      return false;
    }

    public int size() {
      return StorageMap.this.size();
    }
  }
}
