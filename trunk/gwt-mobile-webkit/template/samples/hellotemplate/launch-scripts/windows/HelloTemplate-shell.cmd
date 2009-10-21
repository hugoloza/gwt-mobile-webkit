@if "%GWT_HOME%"=="" goto needGWTHome
@java -Xmx128m -cp "%~dp0\..\..\src;%~dp0\..\..\bin;%GWT_HOME%\gwt-user.jar;%GWT_HOME%\gwt-dev-windows.jar;..\..\..\..\gwt-templtype-template.jar" com.google.gwt.dev.HostedMode -war "%~dp0\..\..\war" %* -startupUrl HelloTemplate.html com.google.code.gwt.template.sample.hellotemplate.HelloTemplate
@exit /B %ERRORLEVEL%

:needGWTHome
@echo The environment variable GWT_HOME is not defined, it should point to a valid GWT installation.
@exit /B 1
