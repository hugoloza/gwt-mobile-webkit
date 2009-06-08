package com.google.code.gwt.appcache.sample.helloappcache.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HelloServiceAsync {

  void sayHello(String name, AsyncCallback<String> callback);
}
