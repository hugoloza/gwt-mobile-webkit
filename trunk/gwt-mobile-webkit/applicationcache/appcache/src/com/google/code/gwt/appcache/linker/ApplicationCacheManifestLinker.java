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

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.code.gwt.appcache.rebind.NetworkSectionArtifact;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.linker.IFrameLinker;

/**
 * Creates an Application Cache Manifest file <code>cache-manifest.txt</code> as
 * per HTML5 spec.
 * 
 * @see http://www.whatwg.org/specs/web-apps/current-work/multipage/offline.html
 * 
 * @author bguijt
 */
@LinkerOrder(Order.POST)
public class ApplicationCacheManifestLinker extends AbstractLinker {

  /**
   * Used to calculate the paths for CompilationResults.
   */
  private MySelectionScriptLinker ssl = new MySelectionScriptLinker();

  @Override
  public String getDescription() {
    return "HTML5 Application Cache Manifest";
  }

  @SuppressWarnings("unchecked")
  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context,
      ArtifactSet artifacts) throws UnableToCompleteException {
    // Create a new ArtifactSet to return:
    ArtifactSet artifactSet = new ArtifactSet(artifacts);

    // Create a list of cacheable resources:
    SortedSet<String> cachePaths = new TreeSet<String>();
    SortedSet<String> networkPaths = new TreeSet<String>();

    StringBuilder sb = new StringBuilder("CACHE MANIFEST\n\n");

    for (Artifact artifact : artifacts) {
      if (artifact instanceof CompilationResult) {
        final CompilationResult compilationResult = (CompilationResult) artifact;
        // The path for this compilation result.
        String artifactPath = ssl.getPath(logger, context, compilationResult);
        checkCacheable(artifactPath, cachePaths);
      } else if (artifact instanceof EmittedArtifact) {
        EmittedArtifact ea = (EmittedArtifact) artifact;
        checkCacheable(ea.getPartialPath(), cachePaths);
      } else if (artifact instanceof NetworkSectionArtifact) {
        NetworkSectionArtifact nsa = (NetworkSectionArtifact) artifact;
        networkPaths.add(nsa.getUrl());
      }
    }

    // Add CACHE entries:
    emitPaths(sb, "CACHE", cachePaths);

    // Add NETWORK entries:
    emitPaths(sb, "NETWORK", networkPaths);

    // Add the manifest as a new artifact to the set that we're returning.
    artifactSet.add(emitString(logger, sb.toString(),
        "cache-manifest.nocache.txt"));

    return artifactSet;
  }

  private void emitPaths(StringBuilder sb, String sectionName,
      SortedSet<String> paths) {
    if (paths.size() > 0) {
      sb.append("\n").append(sectionName).append(":\n");
      for (String p : paths) {
        sb.append(p).append('\n');
      }
    }
  }

  private void checkCacheable(String path, SortedSet<String> cachePaths) {
    if (path.indexOf(".cache.") > 30) {
      cachePaths.add(path);
    }
  }

  /**
   * An extension of IFrameLinker that provides a method to get the path that a
   * compilation result should be emitted under.
   */
  private static class MySelectionScriptLinker extends IFrameLinker {
    private String getPath(TreeLogger treeLogger, LinkerContext context,
        CompilationResult compilationResult) throws UnableToCompleteException {
      EmittedArtifact ea = doEmitCompilation(treeLogger, context,
          compilationResult);
      return ea.getPartialPath();
    }
  }
}
