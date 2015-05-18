# Building the libraries from source #

This page details the instructions to build the libraries from source.

## Prerequisites ##
### Subversion 1.5+ ###
First, make sure you have [SVN version 1.5 or higher](http://subversion.tigris.org/getting.html) installed. Mac users have a SVN client installed, but is outdated - they should upgrade. The [CollabNet binary](http://www.open.collab.net/downloads/community/) seems to be the least obtrusive way to upgrade.

We need version 1.5+ because of its support for the `svn:externals` property. You need this for the build system to work properly.

### Ant 1.7+ ###
Second, make sure you have the [latest Apache Ant](http://ant.apache.org/bindownload.cgi) installed. The build supposedly works with Ant 1.6.5, and it surely works with version 1.7.1.

The build system in large part copied from the GWT distribution itself.

### Java 1.5+ ###
The build (or rather, GWT) needs at least Java 1.5 to build successfully on all platforms. Java 1.6 does not work on Mac OS X with GWT 1.7 (and earlier), because GWT uses SWT bindings which need a 32bit Java package. If you need Java 1.6 on Mac, use GWT 2.0 as well.

### GWT 1.6+ ###
Of course, we also need GWT itself. Although the libraries might work with earlier GWT versions, we only test against version 1.7.1/2.0 at this point.
Make sure you have the `GWT_HOME` environment variable set correctly.

## Checking out ##
Open a Command Prompt and `cd` to your workspace directory. Enter the following command:

`workspace>` **`svn checkout http://gwt-mobile-webkit.googlecode.com/svn/trunk/ gwt-mobile-webkit`**

This checks out the complete project with all libraries in your workspace.
Please note that some parts are repeatedly obtained from an external SVN repository - namely the `build-tools` directory containing the necessary Ant task sources, and the `eclipse/settings` directory containing some configuration files for checkstyle and Eclipse formatting.

## Building ##
To build all library projects:

`gwt-mobile-webkit>` **`ant`**

This will iterate over the (released) libraries and build each one of them to a binary distribution package,