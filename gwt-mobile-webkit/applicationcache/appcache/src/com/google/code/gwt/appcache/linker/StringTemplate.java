package com.google.code.gwt.appcache.linker;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a Template in which a (set of) simple string replace
 * operations can be performed.
 * 
 * @author bguijt
 */
public class StringTemplate {

  private String template;
  private SortedSet<StringRange> replaceRanges = new TreeSet<StringRange>();
  
  public StringTemplate(StringBuilder template, SortedSet<StringRange> replaceRanges) {
    this.template = template.toString();
    this.replaceRanges = replaceRanges;
  }
  
  public void addRange(StringRange range) {
    replaceRanges.add(range);
  }
  
  public void replace(StringRange range, String replaceStr) {
    template = template.substring(0, range.getStartIndex())
        + replaceStr
        + template.substring(range.getStartIndex() + range.getLength());
    for (StringRange sr : replaceRanges.tailSet(range)) {
      if (!sr.equals(range)) {
        sr.adjustStartIndex(replaceStr.length() - range.getLength());
      }
    }
    range.setLength(replaceStr.length());
  }
}
