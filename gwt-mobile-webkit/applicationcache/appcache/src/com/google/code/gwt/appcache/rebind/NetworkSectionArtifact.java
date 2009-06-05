package com.google.code.gwt.html5.applicationcache.rebind;

import com.google.code.gwt.html5.applicationcache.linker.ApplicationCacheManifestLinker;
import com.google.gwt.core.ext.linker.Artifact;

public class NetworkSectionArtifact extends Artifact<NetworkSectionArtifact> {
    
    private String url;

    protected NetworkSectionArtifact(String url) {
        super(ApplicationCacheManifestLinker.class);
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
    
    /**
     * Returns the (partial) URL to be put in the NETWORK: section of the cache manifest.
     */
    public String getUrl() {
        return url;
    }
}
