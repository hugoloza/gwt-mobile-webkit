# Introduction #

This document explains how to get the gwt-mobile-webkit libraries in your Maven project.


# Details #

In your project's `pom.xml` file, you need to add a repository entry and a dependency entry for each gwt-mobile-webkit library you'd like to use.

The repository entry should look like this:

```
<repositories>
  ...
  <repository>
    <id>gwt-mobile-webkit</id>
    <url>http://gwt-mobile-webkit.googlecode.com/svn/repo</url>
  </repository>
</repositories>
```

Now you are ready to include any of the gwt-mobile-webkit libraries as Maven artifact dependencies. Add the following to your `pom.xml` file:

```
<dependencies>
  ...
  <dependency>
    <groupId>com.google.code.gwt-mobile-webkit</groupId>
    <artifactId>gwt-html5-storage</artifactId>
    <version>1.0.1</version>
  </dependency>
</dependencies>
```

The example above includes the Storage API version 1.0.1. The name and version to use in the `artifactId` and `version` elements are the same as the distributions at http://code.google.com/p/gwt-mobile-webkit/downloads/list.