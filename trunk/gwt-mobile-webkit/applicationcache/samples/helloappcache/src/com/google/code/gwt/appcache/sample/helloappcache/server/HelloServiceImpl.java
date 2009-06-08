package com.google.code.gwt.appcache.sample.helloappcache.server;

import com.google.code.gwt.appcache.sample.helloappcache.client.HelloService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class HelloServiceImpl extends RemoteServiceServlet implements HelloService {

  private static final long serialVersionUID = -4614450418634873375L;

  public String sayHello(String name) {
    return "Hello " + name;
  }

}
