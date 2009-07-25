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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.PublicResource;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;

/**
 * Creates an Application Cache Manifest file
 * <code>cache-manifest.nocache.txt</code> as per HTML5 spec. Additionally, it
 * adds a <a
 * href="http://www.w3.org/TR/html5/semantics.html#attr-html-manifest">manifest
 * attribute</a> to the html tag in the root HTML file.
 * 
 * <p>
 * If you need to add your own entries, ensure you have a template manifest in
 * your classpath. The template resource should be
 * <code>html5-cache-manifest.txt</code> and have the following contents:
 * </p>
 * 
 * <pre>
 * CACHE MANIFEST
 * 
 * # You can add any comment on lines starting with a hash symbol
 * # anywhere in the template.
 * 
 * CACHE:
 * # Use @CACHE_ENTRIES@ as a placeholder for generated cachable URLs:
 * @CACHE_ENTRIES@
 * # Your additional URLs:
 * <i>myResource.txt</i>
 * <i>myImage.png</i>
 * 
 * NETWORK:
 * # Use @NETWORK_ENTRIES@ as a placeholder for generated network URLs:
 * @NETWORK_ENTRIES@
 * # Your additional URLs:
 * <i>cgi-bin</i>
 * <i>messagebroker/amf</i>
 * </pre>
 * 
 * <p>
 * This linker will pick this resource (if it exists) and replaces the
 * <code>@.._ENTRIES@</code> placeholders with their respective generated URL
 * entries.
 * </p>
 * 
 * @see <a href="http://www.w3.org/TR/html5/offline.html#appcache">HTML5
 *      Application caches</a>
 * 
 * @author bguijt
 */
@LinkerOrder(Order.POST)
public class ApplicationCacheManifestLinker extends AbstractLinker {

  private static final String MANIFEST = "cache-manifest.nocache.txt";
  private static final String TEMPLATE_RESOURCE = "/html5-cache-manifest.txt";
  private static final String CACHE_ENTRIES = "@CACHE_ENTRIES@";
  private static final String NETWORK_ENTRIES = "@NETWORK_ENTRIES@";

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

    // Find the root HTML file:
    String htmlName = context.getModuleName();
    int index = htmlName.lastIndexOf('.');
    if (index > 0) {
      htmlName = htmlName.substring(index + 1);
    }
    htmlName += ".html";

    logger.log(TreeLogger.INFO, "Trying to find " + htmlName
        + " and add cache-manifest to it...");

    // Create a list of cacheable resources:
    SortedSet<String> cachePaths = new TreeSet<String>();
    SortedSet<String> networkPaths = new TreeSet<String>();

    // Iterate over all emitted artifacts, and collect all
    // cacheable- and networked artifacts:
    boolean htmlFound = false;
    for (Artifact artifact : artifacts) {
      logger.log(TreeLogger.INFO, "Checking artifact "
          + artifact.getClass().getName() + " created by "
          + artifact.getLinker().getName() + ": " + artifact.toString() + "...");
      if (artifact instanceof EmittedArtifact) {
        EmittedArtifact ea = (EmittedArtifact) artifact;
        checkCacheable(ea.getPartialPath(), cachePaths);
      } else if (artifact instanceof NetworkSectionArtifact) {
        NetworkSectionArtifact nsa = (NetworkSectionArtifact) artifact;
        networkPaths.add(nsa.getUrl());
      }
      // We try to 'fix' the module's HTML file with a manifest attribute:
      if (artifact instanceof PublicResource) {
        PublicResource pr = (PublicResource) artifact;
        if (pr.getPartialPath().equals(htmlName)) {
          // module's html file found:
          StringBuilder htmlSb = readResource(pr.getContents(logger), logger);
          if (htmlSb != null && htmlSb.length() > 0) {
            if (searchAndReplace(htmlSb, "<html", "<html manifest=\""
                + MANIFEST + "\"")) {
              logger.log(TreeLogger.INFO, "manifest='" + MANIFEST
                  + "' attribute successfully added to HTML tag of resource '"
                  + pr.getPartialPath() + "'.");
              artifactSet.remove(pr);
              artifactSet.add(emitString(logger, htmlSb.toString(), htmlName,
                  pr.getLastModified()));
              htmlFound = true;
            }
          }
        }
      }
    }

    if (!htmlFound) {
      // Failed to 'fix' HTML tag:
      logger.log(TreeLogger.WARN, "HTML file named '" + htmlName
          + "' could NOT be fixed to add 'manifest' attribute"
          + " to root HTML tag!");
      logger.log(TreeLogger.WARN, "If you use an HTML file from your main"
          + " module, please review its HTML code.");
      logger.log(TreeLogger.WARN, "Or, add the following"
          + " attribute to your landing page's <html> tag: manifest=\""
          + context.getModuleFunctionName() + "/" + MANIFEST + "\"");
    }
    String cacheEntries = emitPaths(CACHE_ENTRIES, cachePaths);
    String networkEntries = emitPaths(NETWORK_ENTRIES, networkPaths);

    InputStream templateInput = getClass().getResourceAsStream(
        TEMPLATE_RESOURCE);
    StringBuilder tb = new StringBuilder();
    if (templateInput != null) {
      logger.log(TreeLogger.INFO, "HTML5 cache-manifest resource '"
          + TEMPLATE_RESOURCE + "' found - using that resource as template.");
      // Read template into a StringBuilder:
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          templateInput));
      try {
        String line = null;
        while ((line = reader.readLine()) != null) {
          if (!line.trim().startsWith("#")) {
            line = searchAndReplace(line, CACHE_ENTRIES, cacheEntries);
            line = searchAndReplace(line, NETWORK_ENTRIES, networkEntries);
          }
          tb.append(line).append('\n');
        }
        reader.close();
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Failed to read resource '"
            + TEMPLATE_RESOURCE + "'!", e);
      }
    }
    if (tb.length() == 0) {
      logger.log(TreeLogger.INFO, "HTML5 cache-manifest resource '"
          + TEMPLATE_RESOURCE
          + "' not found (or empty or whatever) - generating default manifest.");
      tb.append("CACHE MANIFEST:\n\nCACHE:\n").append(cacheEntries).append(
          "\n\nNETWORK:\n").append(networkEntries);
    }

    // Add the manifest as a new artifact to the set that we're returning.
    artifactSet.add(emitString(logger, tb.toString(), MANIFEST));

    return artifactSet;
  }

  private StringBuilder readResource(InputStream in, TreeLogger logger) {
    // Read template into a StringBuilder:
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append('\n');
      }
      reader.close();
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Failed to read input!", e);
    }
    return sb;
  }

  private String searchAndReplace(String str, String search, String replace) {
    int index = str.indexOf(search);
    if (index >= 0) {
      return str.substring(0, index) + replace
          + str.substring(index + search.length());
    }
    return str;
  }

  private boolean searchAndReplace(StringBuilder str, String search,
      String replace) {
    int index = str.indexOf(search);
    if (index >= 0) {
      str.replace(index, index + search.length(), replace);
      return true;
    }
    return false;
  }

  private String emitPaths(String sectionName, SortedSet<String> paths) {
    StringBuilder sb = new StringBuilder();
    for (String p : paths) {
      if (sb.length() > 0) {
        sb.append('\n');
      }
      sb.append(p);
    }
    return sb.toString();
  }

  private void checkCacheable(String path, SortedSet<String> cachePaths) {
    // if (path.indexOf(".cache.") > 30) {
    cachePaths.add(path);
    // }
  }
}
