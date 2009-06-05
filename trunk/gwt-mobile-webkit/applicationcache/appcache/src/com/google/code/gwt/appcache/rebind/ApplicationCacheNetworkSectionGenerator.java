// $Id$

package com.google.code.gwt.html5.applicationcache.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Generates the 'Network' section in for the Application Cache manifest.
 * 
 * @author bguijt
 * @see 
 */
public class ApplicationCacheNetworkSectionGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();
        assert (typeOracle != null);

        JClassType remoteService = typeOracle.findType(typeName);
        RemoteServiceRelativePath moduleRelativeUrl = remoteService.getAnnotation(RemoteServiceRelativePath.class);
        
        if (moduleRelativeUrl != null) {
            // add URL to network section:
            if (logger.isLoggable(Type.INFO)) {
                logger.log(Type.INFO, "Found URL for NETWORK: section in cache-manifest: '" + moduleRelativeUrl.value() + "'");
            }
            NetworkSectionArtifact artifact = new NetworkSectionArtifact(moduleRelativeUrl.value());
            context.commitArtifact(logger, artifact);
        }
        
        // Use the requested type itself - let the RPC generater take care of that:
        return null;
    }

}
