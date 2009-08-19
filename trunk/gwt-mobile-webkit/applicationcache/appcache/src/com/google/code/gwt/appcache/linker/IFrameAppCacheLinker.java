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
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.linker.IFrameLinker;
import com.google.gwt.dev.About;
import com.google.gwt.dev.util.DefaultTextOutput;

/**
 * Fixes the output of the GWT compiler to work with the HTML 5 Application
 * Cache feature.
 * 
 * @author bguijt
 */
@LinkerOrder(Order.PRIMARY)
public class IFrameAppCacheLinker extends IFrameLinker {

  private static final String MANIFEST = "appcache.nocache.manifest";
  private static final String TEMPLATE_RESOURCE = "/html5-cache-manifest.txt";
  private static final String CACHE_ENTRIES = "@CACHE_ENTRIES@";
  private static final String NETWORK_ENTRIES = "@NETWORK_ENTRIES@";

  @Override
  public String getDescription() {
    return "AppCache";
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context,
      ArtifactSet artifacts) throws UnableToCompleteException {
    ArtifactSet toReturn = super.link(logger, context, artifacts);

    // Create a cache-manifest and html wrapper for each permutation:
    SortedSet<CompilationResult> permutations = toReturn.find(CompilationResult.class);
    for (CompilationResult cr : permutations) {
      // Emit the permutation-specific artifacts:
      emitPermutationArtifacts(logger, context, cr, toReturn);
    }

    // Create the general cache-manifest resource for the landing page:
    toReturn.add(emitLandingPageCacheManifest(context, logger, toReturn));

    return toReturn;
  }

  /**
   * Creates the cache-manifest resource specific for the landing page.
   */
  private Artifact<?> emitLandingPageCacheManifest(LinkerContext context,
      TreeLogger logger, ArtifactSet artifacts)
      throws UnableToCompleteException {
    // Create a list of cacheable resources:
    SortedSet<String> cachePaths = new TreeSet<String>();
    SortedSet<String> networkPaths = new TreeSet<String>();

    // Iterate over all emitted artifacts, and collect all
    // cacheable- and networked artifacts:
    for (Artifact artifact : artifacts) {
      // logger.log(TreeLogger.INFO, "Checking artifact "
      // + artifact.getClass().getName() + " created by "
      // + artifact.getLinker().getName() + ": " + artifact.toString() + "...");
      if (artifact instanceof EmittedArtifact) {
        EmittedArtifact ea = (EmittedArtifact) artifact;
        if (ea.getLinker().equals(getClass())) {
          if (ea.getPartialPath().endsWith(".cache.html")
              || ea.getPartialPath().endsWith(".nocache.js")) {
            cachePaths.add(ea.getPartialPath());
          }
        } else {
          cachePaths.add(ea.getPartialPath());
        }
      } else if (artifact instanceof NetworkSectionArtifact) {
        NetworkSectionArtifact nsa = (NetworkSectionArtifact) artifact;
        networkPaths.add(nsa.getUrl());
      }
    }

    String cacheEntries = concatPaths(cachePaths);
    String networkEntries = concatPaths(networkPaths);

    InputStream templateInput = getClass().getResourceAsStream(
        TEMPLATE_RESOURCE);
    StringBuilder tb = new StringBuilder();
    if (templateInput != null) {
      logger.log(TreeLogger.INFO, "HTML 5 cache-manifest resource '"
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
      logger.log(TreeLogger.INFO, "HTML 5 cache-manifest resource '"
          + TEMPLATE_RESOURCE
          + "' not found (or empty or whatever) - generating default manifest.");
      tb.append("CACHE MANIFEST\n\nCACHE:\n").append(cacheEntries).append(
          "\n\nNETWORK:\n").append(networkEntries);
    }

    logger.log(TreeLogger.INFO, "Make sure you have the following"
        + " attribute added to your landing page's <html> tag: manifest=\""
        + context.getModuleFunctionName() + "/" + MANIFEST + "\"");

    // Create the manifest as a new artifact and return it:
    return emitString(logger, tb.toString(), MANIFEST);
  }

  /**
   * Searches in the specified <code>str</code> for occurrences of the specified
   * <code>search</code> and replaces them with the specified
   * <code>replace</code> value.
   */
  private String searchAndReplace(String str, String search, String replace) {
    int index = str.indexOf(search);
    if (index >= 0) {
      return str.substring(0, index) + replace
          + str.substring(index + search.length());
    }
    return str;
  }

  /**
   * Concatenates all entries of the specified String collection separated by a
   * newline character.
   */
  private String concatPaths(Collection<String> paths) {
    StringBuilder sb = new StringBuilder();
    for (String p : paths) {
      if (sb.length() > 0) {
        sb.append('\n');
      }
      sb.append(p);
    }
    return sb.toString();
  }

  /**
   * Emits the cache-manifest resource specific for a permutation.
   */
  private SyntheticArtifact emitPermutationCacheManifest(TreeLogger logger,
      LinkerContext context, String jsStrongName)
      throws UnableToCompleteException {
    String manifestContent = "CACHE MANIFEST\n\nCACHE:\n" + jsStrongName + "\n";
    // Manifest resource: "4564DAF4567BE674.nocache.manifest"
    String manifestStrongName = jsStrongName.substring(0, jsStrongName.length()
        - "cache.js".length())
        + "nocache.manifest";
    return emitString(logger, manifestContent, manifestStrongName);
  }

  /**
   * Emits the wrapper HTML resource which loads the permutation javascript in
   * an external JS resource.
   */
  private void emitPermutationArtifacts(TreeLogger logger,
      LinkerContext context, CompilationResult cr, ArtifactSet toReturn) {
    // Define the strongName(s) and replace the compilation artifact:
    String htmlStrongName = null;
    String jsStrongName = null;
    String manifestStrongName = null;
    try {
      // Compute the strongName of the permutation artifact:
      EmittedArtifact htmlArtifact = doEmitCompilation(logger, context, cr);
      htmlStrongName = htmlArtifact.getPartialPath();
      // Now remove the '.cache.html' permutation artifact from the 'toReturn'
      // ArtifactSet, and replace it with a '.cache.js' artifact:
      toReturn.remove(htmlArtifact);
      // Compute the new 'strongName' for the cache.js artifact:
      jsStrongName = htmlStrongName.substring(0, htmlStrongName.length() - 4)
          + "js";
      SyntheticArtifact jsArtifact = emitInputStream(logger,
          htmlArtifact.getContents(logger), jsStrongName);
      toReturn.add(jsArtifact);
      // Emit the cache manifest:
      EmittedArtifact cacheManifestArtifact = emitPermutationCacheManifest(
          logger, context, jsStrongName);
      toReturn.add(cacheManifestArtifact);
      manifestStrongName = cacheManifestArtifact.getPartialPath();
    } catch (UnableToCompleteException e) {
      logger.log(TreeLogger.ERROR, "Failed to emit compilation!", e);
    }

    DefaultTextOutput out = new DefaultTextOutput(context.isOutputCompact());
    out.print("<html manifest=\"" + manifestStrongName + "\">");
    out.newlineOpt();

    // Setup the well-known variables.
    //
    out.print("<head><script>");
    out.newlineOpt();
    out.print("var $gwt_version = \"" + About.GWT_VERSION_NUM + "\";");
    out.newlineOpt();
    out.print("var $wnd = parent;");
    out.newlineOpt();
    out.print("var $doc = $wnd.document;");
    out.newlineOpt();
    out.print("var $moduleName, $moduleBase;");
    out.newlineOpt();
    out.print("var $stats = $wnd.__gwtStatsEvent ? function(a) {return $wnd.__gwtStatsEvent(a);} : null;");
    out.newlineOpt();
    out.print("$stats && $stats({moduleName:'" + context.getModuleName()
        + "',subSystem:'startup',evtGroup:'moduleStartup'"
        + ",millis:(new Date()).getTime(),type:'moduleEvalStart'});");
    out.newlineOpt();
    out.print("</script></head>");
    out.newlineOpt();
    out.print("<body>");
    out.newlineOpt();

    // Output the JS strongName to the HTML wrapper:
    out.print("<script type=\"text/javascript\" src=\"" + jsStrongName
        + "\"></script></body></html>");
    out.newlineOpt();

    try {
      toReturn.add(emitString(logger, out.toString(), htmlStrongName));
    } catch (UnableToCompleteException e) {
      logger.log(TreeLogger.ERROR, "Failed to emit wrapper HTML!", e);
    }
  }

  /**
   * To compute the so-called 'strongName's, we need the '.cache.html'
   * extension. However, in the end the permutation JS artifact *must* be stored
   * using the '.cache.js' extension which is loaded by the HTML wrapper.
   */
  @Override
  protected String getCompilationExtension(TreeLogger logger,
      LinkerContext context) {
    return ".cache.html";
  }

  /**
   * Outputs a piece of Javascript which is prepended to each Permutation. In
   * this case: <code>""</code>.
   */
  @Override
  protected String getModulePrefix(TreeLogger logger, LinkerContext context) {
    return "";
  }

  /**
   * Outputs a piece of Javascript which is appended to each Permutation.
   */
  @Override
  protected String getModuleSuffix(TreeLogger logger, LinkerContext context) {
    DefaultTextOutput out = new DefaultTextOutput(context.isOutputCompact());

    out.print("$stats && $stats({moduleName:'" + context.getModuleName()
        + "',subSystem:'startup',evtGroup:'moduleStartup'"
        + ",millis:(new Date()).getTime(),type:'moduleEvalEnd'});");

    // Generate the call to tell the bootstrap code that we're ready to go.
    out.newlineOpt();
    out.print("if ($wnd." + context.getModuleFunctionName() + ") $wnd."
        + context.getModuleFunctionName() + ".onScriptLoad();");
    out.newline();

    return out.toString();
  }
}
