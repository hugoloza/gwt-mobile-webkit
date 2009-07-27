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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.PublicResource;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.impl.SelectionScriptLinker;
import com.google.gwt.dev.About;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.Util;

/**
 * This Linker redirects the user to the page which is specific for a single Permutation.
 * 
 * <p>The page to redirect to is a copy of the landing page (as found as a Module html file, or
 * a welcome-file in web.xml) with a few modifications:</p>
 * <ul>
 * <li>The &lt;html&gt; tag gets an attribute named <code>manifest</code> which contains a URL
 * to a cache-manifest resource;</li>
 * <li>Additional &lt;script&gt; tags are included which contain the code for the page's specific Permutation.</li>
 * </ul>
 * <p>The source landing page will be replaced with a small html page containing the appropriate redirect code.</p>
 * 
 * @author bguijt
 */
@LinkerOrder(Order.PRIMARY)
public class RedirectLinker extends SelectionScriptLinker {

  private String landingPageName;
  
  @Override
  public String getDescription() {
    return "Redirect";
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context,
      ArtifactSet artifacts) throws UnableToCompleteException {
    ArtifactSet toReturn = super.link(logger, context, artifacts);
    
    StringBuilder landingPage = loadLandingPage(logger, context, artifacts);
    // Locate ranges in landing page for substitution:
    StringRange scriptLocation = findScriptRange(logger, context, landingPage);
    // Prepare the landing page *Template*:
    SortedSet<StringRange> scriptLocations = new TreeSet<StringRange>();
    scriptLocations.add(scriptLocation);
    StringTemplate landingPageTemplate = new StringTemplate(landingPage, scriptLocations);
    
    // Find permutations:
    SortedSet<CompilationResult> permutations = toReturn.find(CompilationResult.class);
    for (CompilationResult cr : permutations) {
      
    }
    return toReturn;
  }

  private StringRange findScriptRange(TreeLogger logger, LinkerContext context, StringBuilder landingPage) throws UnableToCompleteException {
    // We want the position *after* the 'regular' module JS include:
    String jsSrc = context.getModuleName() + ".nocache.js";
    int i = landingPage.indexOf(jsSrc);
    if (i < 0) {
      // Not found? Weird!
      logger.log(TreeLogger.ERROR, "The HTML landing page '" + landingPageName + "' has no <script> reference");
      logger.log(TreeLogger.ERROR, "to '" + jsSrc + "'.");
      logger.log(TreeLogger.ERROR, "It is not able to bootstrap the GWT application. Please fix this.");
      throw new UnableToCompleteException();
    }
    // Advance i after </script> tag:
    i += jsSrc.length();
    i = landingPage.indexOf("</", i);
    if (i > 0) {
      i = landingPage.indexOf(">", i);
      if (i > 0) {
        return new StringRange(i, 0);
      }
    }
    // </...> not found...?
    logger.log(TreeLogger.ERROR, "The HTML landing page '" + landingPageName + "' has a reference");
    logger.log(TreeLogger.ERROR, "to '" + jsSrc + "', but, so it seems, not in a <script> reference.");
    logger.log(TreeLogger.ERROR, "It is not able to bootstrap the GWT application. Please fix this.");
    throw new UnableToCompleteException();
  }

  private String createKey(CompilationResult compilationResult) {
    StringBuilder sb = new StringBuilder();
    for (SortedMap<SelectionProperty, String> map : compilationResult.getPropertyMap()) {
      for (SelectionProperty sp : map.keySet()) {
        if (sb.length() > 0) {
          sb.append("-");
        }
        sb.append(map.get(sp));
      }
    }
    //return sb.toString();
    return Util.computeStrongName(sb.toString().getBytes());
  }
  
  /**
   * Either finds an HTML landing page, or throws an UnableToCompleteException.
   * 
   * @param logger
   * @param context
   * @param artifacts
   * @return
   * @throws UnableToCompleteException
   */
  private StringBuilder loadLandingPage(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
    // First, search in the module XML:
    
    String htmlName = context.getModuleName();
    int index = htmlName.lastIndexOf('.');
    if (index > 0) {
      htmlName = htmlName.substring(index + 1);
    }
    htmlName += ".html";
    
    logger.log(TreeLogger.INFO, "Trying to find HTML landing page. First try: '" + htmlName + "' in /public/ path of module...");
    
    for (PublicResource pr : artifacts.find(PublicResource.class)) {
      if (pr.getPartialPath().equals(htmlName)) {
        // module's html file found:
        logger.log(TreeLogger.INFO, "HTML landing page '" + htmlName + "' found in /public/ path of module.");
        return loadResource(logger, pr.getContents(logger));
      }
    }

    logger.log(TreeLogger.INFO, "HTML landing page '" + htmlName + "' not found in /public/ path of module. Trying web.xml's welcome-file-list...");
    
    // Next, search in the web.xml for a *.htm(l) welcome-file:
    String webxml = null;
    for (ConfigurationProperty cp : context.getConfigurationProperties()) {
      if (cp.getName().equals("webxml")) {
        webxml = cp.getValue();
        logger.log(TreeLogger.INFO, "A web.xml location is configured at '" + webxml + "'...");
        break;
      }
    }
    
    if (webxml == null) {
      logger.log(TreeLogger.ERROR, "Cannot find any HTML landing page! Searched in module and searched for 'webxml' config.");
      logger.log(TreeLogger.ERROR, "Please add the following XML element:");
      logger.log(TreeLogger.ERROR, "    <set-configuration-property name=\"webxml\" value=\".../web.xml\" />");
      logger.log(TreeLogger.ERROR, "to your module XML config and specify the (relative) location of the web.xml file.");
      try {
        logger.log(TreeLogger.ERROR, "Current location: " + new File(".").getCanonicalPath());
      } catch (IOException e) {
        // ignore for now
      }
      throw new UnableToCompleteException();
    }
    
    File webxmlFile = new File(webxml);
    if (!webxmlFile.exists()) {
      logger.log(TreeLogger.ERROR, "web.xml file '" + webxmlFile.toString() + "' does not exist!");
      throw new UnableToCompleteException();
    }
    
    try {
      // Locate webapp directory:
      File webinfDir = webxmlFile.getParentFile();
      if (webinfDir == null) {
        logger.log(TreeLogger.ERROR, "web.xml file '" + webxmlFile.toString() + "' has no parent (WEB-INF) directory!");
        throw new UnableToCompleteException();
      }
      File webappDir = webinfDir.getParentFile();
      if (webappDir == null) {
        logger.log(TreeLogger.ERROR, "web.xml file '" + webxmlFile.toString() + "' has no parent (webapp/WEB-INF) directory!");
        throw new UnableToCompleteException();
      }
      
      Set<String> triedFiles = new HashSet<String>();
      
      // Parse web.xml:
      Document webxmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webxmlFile);
      NodeList welcomeFiles = webxmlDoc.getElementsByTagName("welcome-file");
      StringBuilder lp = null;
      for (int i=0; i<welcomeFiles.getLength(); i++) {
        String welcomeFile = welcomeFiles.item(i).getFirstChild().getTextContent();
        if (welcomeFile.endsWith("html") || welcomeFile.endsWith("htm")) {
          // Found it!
          lp = loadWelcomeFile(logger, webappDir, welcomeFile, triedFiles);
          if (lp != null) {
            return lp;
          }
        }
      }
      
      // Try standard welcome files:
      lp = loadWelcomeFile(logger, webappDir, "index.html", triedFiles);
      if (lp != null) {
        return lp;
      }
      lp = loadWelcomeFile(logger, webappDir, "index.htm", triedFiles);
      if (lp != null) {
        return lp;
      }
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "web.xml file '" + webxmlFile.toString() + "' exists, but cannot be read!", e);
      throw new UnableToCompleteException();
    } catch (SAXException e) {
      logger.log(TreeLogger.ERROR, "web.xml file '" + webxmlFile.toString() + "' exists, but cannot be parsed as XML!", e);
      throw new UnableToCompleteException();
    } catch (ParserConfigurationException e) {
      logger.log(TreeLogger.ERROR, "web.xml file '" + webxmlFile.toString() + "' exists, but XML parser fails!", e);
      throw new UnableToCompleteException();
    }
    
    // No HTML landing page found:
    logger.log(TreeLogger.ERROR, "Unable to find the HTML landing page!");
    throw new UnableToCompleteException();
  }

  private StringBuilder loadWelcomeFile(TreeLogger logger, File webappDir, String welcomeFile, Set<String> triedFiles) throws UnableToCompleteException {
    if (triedFiles.contains(welcomeFile)) {
      return null;
    }
    File landingPageFile = new File(webappDir, welcomeFile);
    if (!landingPageFile.exists()) {
      logger.log(TreeLogger.WARN, "HTML landing page '" + landingPageFile.toString() + "' is a <welcome-file> but does not exist!");
      triedFiles.add(welcomeFile);
      return null;
    }
    try {
      logger.log(TreeLogger.INFO, "HTML landing page '" + landingPageFile.toString() + "' found in webapp directory.");
      landingPageName = landingPageFile.toString();
      return loadResource(logger, new FileInputStream(landingPageFile));
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "HTML landing page '" + landingPageFile.toString() + "' exists, but cannot be read!", e);
      throw new UnableToCompleteException();
    }
  }

  private StringBuilder loadResource(TreeLogger logger, InputStream input) throws UnableToCompleteException {
    InputStreamReader reader = new InputStreamReader(input);
    StringBuilder sb = new StringBuilder();
    char[] buf = new char[1024];
    try {
      int read;
      while ((read = reader.read(buf, 0, 1024)) > -1) {
        sb.append(buf, 0, read);
      }
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to read HTML landing page!", e);
      throw new UnableToCompleteException();
    } finally {
      try {
        reader.close();
        input.close();
      } catch (IOException e) {
        logger.log(TreeLogger.WARN, "Unable to close HTML landing page stream (ignoring)", e);
      }
    }
    return sb;
  }

  @Override
  protected String getCompilationExtension(TreeLogger logger,
      LinkerContext context) throws UnableToCompleteException {
    return ".cache.js";
  }

  @Override
  protected String getModulePrefix(TreeLogger logger, LinkerContext context)
      throws UnableToCompleteException {
    DefaultTextOutput out = new DefaultTextOutput(context.isOutputCompact());

    // Setup the well-known variables.
    //
    out.print("<script><!--");
    out.newlineOpt();
    out.print("var $gwt_version = \"" + About.GWT_VERSION_NUM + "\";");
    out.newlineOpt();
    out.print("var $wnd = window;");
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
    out.newline();
    return out.toString();
  }

  @Override
  protected String getModuleSuffix(TreeLogger logger, LinkerContext context)
      throws UnableToCompleteException {
    DefaultTextOutput out = new DefaultTextOutput(context.isOutputCompact());

    out.print("$stats && $stats({moduleName:'" + context.getModuleName()
        + "',subSystem:'startup',evtGroup:'moduleStartup'"
        + ",millis:(new Date()).getTime(),type:'moduleEvalEnd'});");

    // Generate the call to tell the bootstrap code that we're ready to go.
    out.newlineOpt();
    out.print("if ($wnd." + context.getModuleFunctionName() + ") $wnd."
        + context.getModuleFunctionName() + ".onScriptLoad();");
    out.newline();
    out.print("--></script>");
    out.newlineOpt();

    return out.toString();
  }

  @Override
  protected String getSelectionScriptTemplate(TreeLogger logger,
      LinkerContext context) throws UnableToCompleteException {
    return "com/google/code/gwt/appcache/linker/RedirectTemplate.js";
  }
}
