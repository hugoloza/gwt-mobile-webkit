@if "%GWT_HOME%"=="" goto needGWTHome
@java -Xmx128M -cp "%~dp0\..\..\src;%~dp0\..\..\bin;%GWT_HOME%\gwt-user.jar;%GWT_HOME%\gwt-dev.jar;%GWT_HOME%\gwt-dev-windows.jar;..\..\..\..\@JARNAME@" com.google.gwt.dev.Compiler -war "%~dp0\..\..\war" %* @ENTRY_MODULE@
@exit /B %ERRORLEVEL%

:needGWTHome
@echo The environment variable GWT_HOME is not defined, it should point to a valid GWT installation.
@exit /B 1
