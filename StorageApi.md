# Introduction #
The Storage API ([download](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Storage)) leverages the [W3C Web Storage API](http://www.w3.org/TR/webstorage/#storage). See [this article](http://www.nczonline.net/blog/2009/07/21/introduction-to-sessionstorage/) for a nice introduction to the `sessionStorage`.

With this API you are able to store data on the client using a Java [Map](http://java.sun.com/javase/6/docs/api/java/util/Map.html)-like interface (key/values).

# Browser support #
Not every browser supports this feature, and currently there are no fallback scenario's to rely on Flash, Java or Gears plugins. The following browsers are supported:

  1. iPhone Safari (OS3.0)
  1. Android (2.0)
  1. webOS (1.4)
  1. Desktop Safari (4.0)
  1. Chrome (4.0)
  1. Firefox (3.5)
  1. Internet Explorer (8)
  1. Opera (10.50)

Other browsers might also support Web Storage, we just didn't test them nor found any feature support documentation.

# Usage #
## Get the Storage API using Maven ##
[Add the repository](MavenUserGuide.md) and the following to your `pom.xml` file:

```
<dependencies>
  ...
  <dependency>
    <groupId>com.google.code.gwt-mobile-webkit</groupId>
    <artifactId>gwt-html5-storage</artifactId>
  </dependency>
</dependencies>
```

## Add the Storage API to your project ##
If you don't use Maven, copy the `gwt-html5-storage-x.x.x.jar` file from the downloaded distribution to your project classpath. This jar contains everything you need to use the Storage API.

Next, in your GWT module `gwt.xml` file (which uses the Storage API), add the following entry:
```
<inherits name="com.google.code.gwt.storage.Html5Storage" />
```
This imports the Storage API into your module. Now you are ready to use the API!

## is API supported? ##
The following will test whether the API is supported in your browser:
```
if (Storage.isSupported()) {
    // Interact with the storage...
}
```

## Getting a Storage instance ##
There are two Storage types available. Both have exactly the same API, they just persist differently:
  1. `localStorage` actually persists data permanently;
  1. `sessionStorage` keeps the data available for the duration of the Session.

A Storage is obtained like this:
```
Storage localStorage = Storage.getLocalStorage();
Storage sessionStorage = Storage.getSessionStorage();
```

## Writing in the Storage ##
Values can be written as follows:
```
localStorage.setItem("key", "value");
```
All keys and values must be Strings.

## Reading the Storage ##
```
String value = localStorage.getItem("key");
```

## Iterating over the Storage ##
```
for (int i=0; i<localStorage.getLength(); i++) {
    String key = localStorage.key(i);
    String value = localStorage.getItem(key);
    // ...
}
```

## Accessing the Storage using a Java Map ##
For convenience sake we added a Java Map implementation over the Storage API. Using this Map you can read and write the Storage using familiar methods:
```
// Creating a Map instance using the localStorage:
Map<String, String> map = new StorageMap(localStorage);

// Invoke some Map method:
map.putAll(otherMap);
// ...
```
Word of advice: Please note that the Java Map interface is way bigger than the Storage interface, and introducing the StorageMap to your code implies some overhead in accessing the Storage data. Only use the StorageMap if absolutely necessary.