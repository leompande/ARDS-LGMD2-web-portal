#!/bin/bash

# get ready to catch errors
trap ctrl-c INT
trap unexpected-exit ERR

function ctrl-c () {
    echo
    echo "============================================"
    echo "Keyboard interrupt"
    echo "DHIS2 live exited"
    echo "============================================"
    exit
}

function unexpected-exit () {
    echo "============================================"
    echo "DHIS2 live exited with an error"
    echo "Make sure you have a java runtime in your path"
    echo "============================================"
    read -p "Press any key to exit"
}

echo "Starting DHIS2 live ..."
# java -jar dhis2-live.jar
$JAVA_HOME/bin/java  -jar dhis2-live.jar
echo "DHIS2 live exited normally"

