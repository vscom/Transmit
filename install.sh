#!/bin/sh
TOM_HOME=/Users/Hong/work/java/apache-tomcat-7.0.41
echo "rm -rf $TOM_HOME/webapps/transmit"
rm -rf $TOM_HOME/webapps/transmit
echo "cp -r WebRoot $TOM_HOME/webapps/transmit"
cp -r WebRoot $TOM_HOME/webapps/transmit
