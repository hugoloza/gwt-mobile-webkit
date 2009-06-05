package com.google.code.gwt.html5.applicationcache.linker;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.code.gwt.html5.applicationcache.rebind.NetworkSectionArtifact;
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
 * Creates an Application Cache Manifest file <code>cache-manifest.txt</code> as per HTML5 spec.
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
    public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
        // Create a new ArtifactSet to return:
        ArtifactSet artifactSet = new ArtifactSet(artifacts);
        
        /*
        // Collect the paths for all of the emitted artifacts so that we can check later
        // that the path calculation is working for the compilation results.
        Set<String> emittedArtifactPaths = new HashSet<String>();
        for (Artifact artifact : artifacts) {
            if (artifact instanceof EmittedArtifact) {
                emittedArtifactPaths.add(((EmittedArtifact) artifact).getPartialPath());
            }
        }
        */
        // Create a list of cacheable resources:
        SortedSet<String> cachePaths = new TreeSet<String>();
        SortedSet<String> networkPaths = new TreeSet<String>();
        
        StringBuilder sb = new StringBuilder("CACHE MANIFEST\n\n");
        
        if (!context.isOutputCompact()) {
            sb.append("# Add the following line to your index.html file:\n#\n");
            sb.append("#    <html manifest=\"" + context.getModuleName() + "/cache-manifest.txt\">\n#\n");
            sb.append("# Also, make sure the webserver serves this resource using Content-Type text/cache-manifest\n\n");
        }
        
        for (Artifact artifact : artifacts) {
            if (artifact instanceof CompilationResult) {
                final CompilationResult compilationResult = (CompilationResult) artifact;
                // The path for this compilation result.
                String artifactPath = ssl.getPath(logger, context, compilationResult);
                // Check that the path matches one of the EmittedArtifacts. This is to give us
                // warning if the path calculation is changed.
                /*
                if (!emittedArtifactPaths.contains(artifactPath)) {
                    System.out.println("No EmittedArtifact with path " + artifactPath);
                    throw new UnableToCompleteException();
                }
                */
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
        artifactSet.add(emitString(logger, sb.toString(), "cache-manifest.txt"));
        
        return artifactSet;
    }

    private void emitPaths(StringBuilder sb, String sectionName, SortedSet<String> paths) {
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
     * An extension of IFrameLinker that provides a method to get the path that a compilation result should
     * be emitted under.
     */
    private static class MySelectionScriptLinker extends IFrameLinker {
        private String getPath(TreeLogger treeLogger, LinkerContext context, CompilationResult compilationResult) throws UnableToCompleteException {
            EmittedArtifact ea = doEmitCompilation(treeLogger, context, compilationResult);
            return ea.getPartialPath();
        }
    }
}
