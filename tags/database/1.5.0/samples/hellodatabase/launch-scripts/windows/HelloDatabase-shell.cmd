@if "%GWT_HOME%"=="" goto needGWTHome
@java -Xmx128m -cp "%~dp0\..\..\src;%~dp0\..\..\bin;%GWT_HOME%\gwt-user.jar;%GWT_HOME%\gwt-dev-windows.jar;..\..\..\..\gwt-html5-database.jar" com.google.gwt.dev.HostedMode -war "%~dp0\..\..\war" %* -startupUrl HelloDatabase.html com.google.code.gwt.database.sample.database.HelloDatabase
@exit /B %ERRORLEVEL%

:needGWTHome
@echo The environment variable GWT_HOME is not defined, it should point to a valid GWT installation.
@exit /B 1
