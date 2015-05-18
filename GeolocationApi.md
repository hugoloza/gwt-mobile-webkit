# Introduction #
The Geolocation API ([download](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Geolocation)) leverages the [W3C Geolocation API](http://www.w3.org/TR/geolocation-API/).

As of June 2009 some browsers started to add support for this API, specifically Firefox 3.5 and iPhone Safari 3.0. This API gives you the [WGS84 coordinates](http://en.wikipedia.org/wiki/World_Geodetic_System) of the location of the browser.

# Browser support #
Not every browser supports this feature. Fortunately, there is a fallback scenario to rely on the Gears plugin - that API is very similar. We built support for the following browsers:

  1. iPhone Safari (OS3.0)
  1. Android (all versions)
  1. Firefox (3.5)
  1. Any browser with the Google Gears plugin

# Usage #
## Get the Geolocation API using Maven ##
[Add the repository](MavenUserGuide.md) and the following to your `pom.xml` file:

```
<dependencies>
  ...
  <dependency>
    <groupId>com.google.code.gwt-mobile-webkit</groupId>
    <artifactId>gwt-html5-geolocation</artifactId>
  </dependency>
</dependencies>
```

## Add the Geolocation API to your project ##
If you don't use Maven, copy the `gwt-html5-geolocation-x.x.x.jar` file to your project classpath. This jar contains everything you need to use the Geolocation API.

Next, in your GWT module `gwt.xml` file (which uses the Geolocation API), add the following entry:
```
<inherits name="com.google.code.gwt.geolocation.Html5Geolocation" />
```
This imports the Geolocation API into your module. Now you are ready to use the API!

## is API supported? ##
The following code will test whether the API is supported in your browser:
```
if (Geolocation.isSupported()) {
    // get your Geo location...
}
```

## Getting a Geolocation instance ##
```
Geolocation geo = Geolocation.getGeolocation();
```

## Obtaining your position ##
```
geo.getCurrentPosition(new PositionCallback() {
    public void onFailure(PositionError error) {
        // Handle failure
    }
    public void onSuccess(Position position) {
        Coordinates coords = position.getCoords();
        // ...
    }
});
```
The location is enclosed in the `Position` instance returned in the `onSuccess()` callback method:
```
//...
Coordinates coords = position.getCoords();
double latitude = coords.getLatitude();
double longitude = coords.getLongitude();
double accuracy = coords.getAccuracy();
```

# Beware #
## Android: "Null or undefined passed for required argument 1." ##
The Gears API in the Android browser is implemented differently from the 'regular' (desktop) plugin. Due to some weird API error, you could get an error message whenever you try to call a Geolocation method (like `getCurrentPosition()`). This is only for pre-2.0 Android versions, as of 2.0 Android uses the regular HTML5 API.
If the error pops up, and you use the Geolocation API version 0.9.4 or higher, please report!

The 0.9.4 release should mitigate any Android Gears error.