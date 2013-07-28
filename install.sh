#!/bin/sh
TOM_HOME=/Users/jilong/Downloads/dev/apache-tomcat-6.0.16
echo "rm -rf $TOM_HOME/webapps/transmit"
rm -rf $TOM_HOME/webapps/transmit
echo "cp -r WebRoot $TOM_HOME/webapps/transmit"
cp -r WebRoot $TOM_HOME/webapps/transmit
