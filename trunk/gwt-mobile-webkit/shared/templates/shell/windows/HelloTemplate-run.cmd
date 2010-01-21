@if "%GWT_HOME%"=="" goto needGWTHome
@java -Xmx128m -cp "$GWT_HOME\gwt-dev.jar;$GWT_HOME\gwt-dev-windows.jar;$GWT_HOME\gwt-servlet.jar;..\..\..\..\@JARNAME@" org.mortbay.jetty.Main 8080 -webapp "%~dp0\..\..\war"

@exit /B %ERRORLEVEL%

:needGWTHome
@echo The environment variable GWT_HOME is not defined, it should point to a valid GWT installation.
@exit /B 1
