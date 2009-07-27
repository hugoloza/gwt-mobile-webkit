package com.google.code.gwt.appcache.linker;

/**
 * Represents a range in a String to use in search/replace operations.
 * 
 * @author bguijt
 */
public class StringRange implements Comparable<StringRange> {

  private int startIndex;
  private int length;
  
  public StringRange(int startIndex, int length) {
    this.startIndex = startIndex;
    this.length = length;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getLength() {
    return length;
  }

  public void adjustStartIndex(int positions) {
    startIndex += positions;
  }
  
  public void setLength(int length) {
    this.length = length;
  }
  
  public int compareTo(StringRange other) {
    return this.startIndex - other.startIndex;
  }
}
