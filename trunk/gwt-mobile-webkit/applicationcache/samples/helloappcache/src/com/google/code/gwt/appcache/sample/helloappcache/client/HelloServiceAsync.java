package com.google.code.gwt.appcache.sample.helloappcache.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Defines just some demo service. The point is that the specified
 * RemoteServiceRelativePath will result in a NETWORK entry in the cache
 * manifest.
 * 
 * @author bguijt
 */
public interface HelloServiceAsync {

  void sayHello(String name, AsyncCallback<String> callback);
}
