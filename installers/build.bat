

REM Adjust these environment variables to suit your system.
REM They are needed by this build script as well as maven during the build.

REM Path to the Java SDK
JAVA_HOME="C:\Program Files\Java\jdk1.6.0_10\"

REM Be sure maven is in your path
PATH=%PATH%;"C:\apache-maven-2.2.1\bin\"

REM Be sure that the Bitrock installer is in your path
BITROCK_HOME="C:\Program Files\BitRock InstallBuilder Professional 6.2.7"

REM If you are including Birt in the build, be sure it is available here
BIRT_WAR="C:\apache-tomcat-6.0.18\webapps\birt\"

REM Path to the root of the documentation branch source tree
DHIS2_DOCS="C:\src\dhis2-docbook-docs"

REM Path to the root of the DHIS2 branch source tree
DHIS2_SRC="C:\src\dhis2"

REM Path to the root of the DHIS2 Live installer source tree
DHIS2_INSTALL="C:\src\dhis2-live-installer"

SET %JAVA_HOME%
SET %PATH%
SET %BITROCK_HOME%
SET %BIRT_WAR%
SET %DHIS2_DOCS%
SET %DHIS2_SRC%
SET %DHIS2_INSTALL%

echo "Building DHIS 2 Core..."
cd %DHIS2_SRC%\dhis-2
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Building DHIS 2 Web..."
cd dhis-web
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Packing DHIS 2 Web Portal..."
cd dhis-web-portal
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Builidng DHIS2 Live Package"
cd %DHIS2_SRC%\dhis-live
mvn clean package -Dtest=skip -DfailIfNoTests=false
echo "Building documentation"
cd %DHIS2_DOCS%
mvn package
echo "Building installer"
cd %DHIS2_INSTALL%
mvn exec:exec
