#/bin/sh
export JAVA_HOME=/usr/share/java/jdk1.6.0_19/
export PATH=$PATH:/home/jason/apache-maven-2.2.1/bin/
export BITROCK_HOME=/home/jason/installbuilder-6.3.1/
export BIRT_WAR="/usr/local/apache-tomcat-6.0.18/webapps/"
export DHIS2_DOCS="/home/jason/dhis2/dhis2-docbook-docs/"
export DHIS2_SRC="/home/jason/dhis2/dhis2/"
export MAVEN_OPTS="-Xms256m -Xmx512m"

if [ -z $1 ]
	then
	echo "Please provide an option: all, dhis2, installer, docs"
elif [ -n $1 ]
 	then
	OPTIONS=$1
fi
case "$OPTIONS" in

all) 

echo "Building DHIS 2 Core..."
cd $DHIS2_SRC/dhis-2
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Building DHIS 2 Web..."
cd $DHIS2_SRC/dhis-2/dhis-web
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Builidng DHIS2 Live Package"
cd $DHIS2_SRC/dhis-live/
mvn clean package -Dtest=skip -DfailIfNoTests=false
echo "Building documentation"
cd $DHIS2_DOCS
mvn package
echo "Building installer"
cd $DHIS2_SRC/dhis2-live-installer
mvn package ;;

docs)

cd $DHIS2_DOCS
mvn package ;;

live)
echo "Builidng DHIS2 Live Package"
cd $DHIS2_SRC/dhis-live/
mvn clean package -Dtest=skip -DfailIfNoTests=false;;

installer)
echo "Building installer"
cd $DHIS2_SRC/dhis2-live-installer
mvn package ;;

dhis2)

echo "Building DHIS 2 Core..."
cd $DHIS2_SRC/dhis-2
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Building DHIS 2 Web..."
cd $DHIS2_SRC/dhis-2/dhis-web
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Builidng DHIS2 Live Package"
cd $DHIS2_SRC/dhis-live/
mvn clean package -Dtest=skip -DfailIfNoTests=false ;;

esac
