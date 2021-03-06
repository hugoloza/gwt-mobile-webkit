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

package com.google.code.gwt.appcache.linker;

import com.google.code.gwt.appcache.rebind.ApplicationCacheNetworkSectionGenerator;
import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.linker.Artifact;

/**
 * Represents a simple NETWORK: entry in the cache manifest.
 * 
 * <p>If multiple NetworkSectionArtifacts are created with the same URL,
 * only one is added to the Linker.</p>
 * 
 * @author bguijt
 * @see ApplicationCacheNetworkSectionGenerator
 * @see ApplicationCacheManifestLinker
 * @see <a href="http://www.w3.org/TR/html5/offline.html">W3C HTML5 - Offline Web Applications</a>
 */
public class NetworkSectionArtifact extends Artifact<NetworkSectionArtifact> {

  private static final long serialVersionUID = 7853420166235727586L;
  
  private String url;

  public NetworkSectionArtifact(Class<? extends Linker> linker, String url) {
    super(linker);
    this.url = url;
  }

  @Override
  protected int compareToComparableArtifact(NetworkSectionArtifact o) {
    return url.compareTo(o.url);
  }

  @Override
  protected final Class<NetworkSectionArtifact> getComparableArtifactType() {
    return NetworkSectionArtifact.class;
  }

  @Override
  public int hashCode() {
    return url.hashCode();
  }
  
  @Override
  public String toString() {
    return "NETWORK entry: " + url;
  }

  /**
   * Returns the (partial) URL to be put in the NETWORK: section of the cache
   * manifest.
   */
  public String getUrl() {
    return url;
  }
}
