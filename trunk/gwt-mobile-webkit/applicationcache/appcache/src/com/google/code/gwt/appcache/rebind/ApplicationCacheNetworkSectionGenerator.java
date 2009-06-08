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

package com.google.code.gwt.appcache.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.rebind.rpc.ServiceInterfaceProxyGenerator;

/**
 * Generates the entries in the 'Network' section in the Application Cache manifest.
 * 
 * @author bguijt
 * @see com.google.code.gwt.appcache.linker.ApplicationCacheManifestLinker
 */
public class ApplicationCacheNetworkSectionGenerator extends ServiceInterfaceProxyGenerator {

  @Override
  public String generate(TreeLogger logger, GeneratorContext context,
      String typeName) throws UnableToCompleteException {
    
    // Invoke the regular RPC generator:
    String result = super.generate(logger, context, typeName);
    
    TypeOracle typeOracle = context.getTypeOracle();
    JClassType remoteService = typeOracle.findType(typeName);
    RemoteServiceRelativePath moduleRelativeUrl = remoteService.getAnnotation(RemoteServiceRelativePath.class);

    if (moduleRelativeUrl != null) {
      // add URL to network section:
      if (logger.isLoggable(Type.INFO)) {
        logger.log(Type.INFO,
            "Found URL for NETWORK: section in cache-manifest: '"
                + moduleRelativeUrl.value() + "'");
      }
      NetworkSectionArtifact artifact = new NetworkSectionArtifact(
          moduleRelativeUrl.value());
      context.commitArtifact(logger, artifact);
    }

    return result;
  }
}
