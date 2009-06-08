package com.google.code.gwt.appcache.sample.helloappcache.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Defines just some demo service. The point is that the specified
 * RemoteServiceRelativePath will result in a NETWORK entry in the cache
 * manifest.
 * 
 * @author bguijt
 */
@RemoteServiceRelativePath("hello")
public interface HelloService extends RemoteService {

  String sayHello(String name);
}
