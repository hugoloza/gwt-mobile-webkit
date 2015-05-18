This library contains [GWT](http://code.google.com/webtoolkit/) API's addressing the new [HTML5](http://www.w3.org/TR/html5/) features which are supported by Mobile [WebKit](http://webkit.org/).

# API Libraries #
There are two categories of GWT API libraries this project's going to deliver: HTML5 oriented and WebKit oriented. The former is about leveraging the latest developments in the HTML5 space (which is closely followed by the WebKit implementation), and the latter category is UI related stuff.

## HTML5 libs ##
The following libraries are suitable not just for the Mobile WebKit browser, also the latest desktop browsers offer these HTML5 capabilities:
| **API Library** | **W3C Spec** | **Browser Support** | **Documentation** | **Code Sample** |
|:----------------|:-------------|:--------------------|:------------------|:----------------|
| [Database API](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database) | [W3C Web Database](http://www.w3.org/TR/webdatabase) | iPhone2.0, Android2.0, Safari3.1, Chrome4, Opera10.50 | [Wiki](http://code.google.com/p/gwt-mobile-webkit/w/list?can=2&q=label:API-Database) / [Issues](http://code.google.com/p/gwt-mobile-webkit/issues/list?q=label:API-Database) / [Javadoc](http://hudson.purpleware.org/job/GWT-HTML5-Database/javadoc/) | [HelloDatabase](http://code.google.com/p/gwt-mobile-webkit/source/browse/trunk/gwt-mobile-webkit/database/samples/hellodatabase/src/com/google/code/gwt/database/sample/hellodatabase/client/HelloDatabase.java) |
| [Storage API](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Storage) | [W3C Web Storage](http://www.w3.org/TR/webstorage) | iPhone3.0, Android2.0, webOS1.4, Safari4.0, Chrome4, Firefox3.0, IE8, Opera10.50 | [Wiki](http://code.google.com/p/gwt-mobile-webkit/w/list?can=2&q=label:API-Storage) / [Issues](http://code.google.com/p/gwt-mobile-webkit/issues/list?q=label:API-Storage) / [Javadoc](http://hudson.purpleware.org/job/GWT-HTML5-Storage/javadoc/) | [HelloStorage](http://code.google.com/p/gwt-mobile-webkit/source/browse/trunk/gwt-mobile-webkit/storage/samples/hellostorage/src/com/google/code/gwt/storage/sample/hellostorage/client/HelloStorage.java) |
| [Geolocation API](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Geolocation) | [W3C Geolocation API](http://www.w3.org/TR/geolocation-API/) | iPhone3.0, Android, Safari5, Chrome5, Firefox3.5, Opera10.60, Google Gears | [Wiki](http://code.google.com/p/gwt-mobile-webkit/w/list?can=2&q=label:API-Geolocation) / [Issues](http://code.google.com/p/gwt-mobile-webkit/issues/list?q=label:API-Geolocation) / [Javadoc](http://hudson.purpleware.org/job/GWT-HTML5-Geolocation/javadoc/) | [HelloGeolocation](http://code.google.com/p/gwt-mobile-webkit/source/browse/trunk/gwt-mobile-webkit/geolocation/samples/hellogeolocation/src/com/google/code/gwt/geolocation/sample/hellogeolocation/client/HelloGeolocation.java) |
| Application Cache API | [W3C Offline Web applications](http://www.w3.org/TR/html5/offline.html) | iPhone2.1, Safari3.1, Firefox3.5 | [Wiki](http://code.google.com/p/gwt-mobile-webkit/w/list?can=2&q=label:API-AppCache) / [Issues](http://code.google.com/p/gwt-mobile-webkit/issues/list?q=label:API-AppCache) | _in progress_   |

## WebKit libs ##
| **API Library** | **Browser Support** | **Documentation** | **Code Sample** |
|:----------------|:--------------------|:------------------|:----------------|
| Widgets API     | iPhone, Android     | [Wiki](http://code.google.com/p/gwt-mobile-webkit/w/list?can=2&q=label:API-Widgets) / [Issues](http://code.google.com/p/gwt-mobile-webkit/issues/list?q=label:API-Widgets) | _in progress_   |

## GWT version compatibility ##
All current (HTML5 API) releases work with GWT versions 1.6, 1.7 and 2.0.

# Current Status and Developments #
Currently we have three HTML5 features covered: Database, Storage and Geolocation. Right now we're working on [Application Cache](AppCacheDesign.md) support, as an additional HTML5 feature.

After that it is time to address the real WebKit features. I would very much like to start working on adapting the Dashcode Parts library to GWT Widgets.

Next to all this, we need to think about how we'd like to map GWT's default permutations (mapped by `user.agent` and `locale`) to the libraries. The problem is that we need to distinguish a larger set of user agents. There's a lot of `safari` (WebKit) user agents [which differ greatly because of their selective support for HTML5 features](http://www.quirksmode.org/webkit.html). So, we could introduce additional `user.agent` values for each mobile device and OS version, and provide our own set of [DOMImpl class](http://code.google.com/p/google-web-toolkit/source/browse/trunk/user/src/com/google/gwt/user/client/impl/DOMImpl.java) support. Or, we provide permutations based on browser capability, which results in a potentially huge amount of permutations. Maybe both (it is just a module XML file which defines these permutations, not the Java sourcecode).

# Updates #
## April 30, 2010 ##
Released Database API version 1.5.1. Get it [here](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database) or [here (maven)](http://gwt-mobile-webkit.googlecode.com/svn/repo/com/google/code/gwt-mobile-webkit/gwt-html5-database/1.5.1/). Fixes [Issue #12](https://code.google.com/p/gwt-mobile-webkit/issues/detail?id=#12) and [Issue #16](https://code.google.com/p/gwt-mobile-webkit/issues/detail?id=#16). Also moved non-public API parts to other packages. Recommended for all Database API users.

## March 28, 2010 ##
Created a Maven repository at http://gwt-mobile-webkit.googlecode.com/svn/repo/ - you can find the current releases of  the Geolocation API, Database API and Storage API there.

## January 26, 2010 ##
Released an updated version of the [Storage API](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Storage). Fixes [Issue #12](https://code.google.com/p/gwt-mobile-webkit/issues/detail?id=#12) and [Issue #13](https://code.google.com/p/gwt-mobile-webkit/issues/detail?id=#13).

## January 15, 2010 ##
Just released [Geolocation API](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Geolocation) version 0.9.5. Fixes [Issue #10](https://code.google.com/p/gwt-mobile-webkit/issues/detail?id=#10) and [Issue #11](https://code.google.com/p/gwt-mobile-webkit/issues/detail?id=#11).

## January 10, 2010 ##
`*`Sigh`*`. The 0.9.3 release of the Geolocation API is fighting again with Android. The API would only work from within the `onModuleLoad()` call. Please download 0.9.4, the fix.

## January 10, 2010 ##
**Important:** Yesterday's release of the Geolocation API (0.9.2) is **borked**. Please download 0.9.3. This one is tested to work in Safari+Gears, Firefox3.5, Android (1.5, 1.6 and 2.0) and iPhone.

## January 9, 2010 ##
Released Geolocation API version 0.9.2. It seemed that the API failed to work properly in Android's browser. This is fixed now. Also hardened the Geolocation methods, so caught exceptions are routed to the Error callback handler.

## December 30, 2009 ##
Released Geolocation API version 0.9.1. Biggest feature is Google Gears support. Now the Geolocation API can be used in Firefox 3.5, iPhone Safari 3 and any browser with the [Google Gears plugin](http://gears.google.com/) installed.

## November 15, 2009 ##
Released Database API version 1.5.0! Get it [here](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database). Biggest new feature: JDBC-EoD like [DataService API](DataServiceUserGuide.md) to make Web Database programming (much) easier and maintainable.

## November 12, 2009 ##
Updated DataService API to the Database library. Please see [DataServiceUserGuide](DataServiceUserGuide.md) for details. Download a prerelease of version 1.5 [here](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database). Also added tests which revealed bug in SQL error handling, which in turn is fixed. This is probably the last RC for a final 1.5 release.

## October 26, 2009 ##
Added DataService API to the Database library. Please see [DataServiceUserGuide](DataServiceUserGuide.md) for details. Download a prerelease of version 1.5 [here](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database).

## June 24, 2009 ##
The GWT API for the [Geolocation](http://www.w3.org/TR/geolocation-API/) specification is (pre)released! It works on Safari-iPhone3.0, Opera Mobile 9.7 and FF3.5. See the [sample](http://code.google.com/p/gwt-mobile-webkit/source/browse/trunk/gwt-mobile-webkit/geolocation/samples/hellogeolocation/src/com/google/code/gwt/geolocation/sample/hellogeolocation/client/HelloGeolocation.java) included in the download.

## June 23, 2009 ##
The GWT API for [Web Storage](http://www.w3.org/TR/webstorage/#storage) is released! It works on all platforms supporting this W3 recommendation, including (but not limited to) Safari-iPhone3.0, Safari4, FF3.5 and IE8.
StorageEvents work on all mentioned platforms.

## June 7, 2009 ##
The next API is (pre-)released: the [HTML5 Storage API](http://www.w3.org/TR/webstorage/#storage). Everything works **except** [the StorageEvents](http://code.google.com/p/gwt-mobile-webkit/issues/detail?id=1)! The JSNI code is somehow not able to give me any StorageEvents.
Otherwise, the rest of the Storage API is working. See the [sample](http://code.google.com/p/gwt-mobile-webkit/source/browse/tags/storage/0.9.0/samples/hellostorage/src/com/google/code/gwt/storage/sample/hellostorage/client/HelloStorage.java) included in the download.

## June 5, 2009 ##
The first API is released: the [HTML5 Database API](http://www.w3.org/TR/webstorage/#sql)! The download is [here](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database). It includes sources, javadoc and a small [sample application](http://code.google.com/p/gwt-mobile-webkit/source/browse/tags/database/1.0.0/samples/hellodatabase/src/com/google/code/gwt/database/sample/hellodatabase/client/HelloDatabase.java) to show you around.