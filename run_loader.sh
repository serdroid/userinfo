#!/bin/bash
LIB_DIR=lib
CP=conf
for jar in `find $LIB_DIR -follow -name "*.jar" -print`
do
   CP="$CP":"$jar"
done
java -Xmx4G -XX:MaxDirectMemorySize=8G -cp $CP info.serdroid.userinfo.grid.GridLoader $*


