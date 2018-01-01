#!/bin/bash
mvn clean install
#rm -rf $MCS/plugins/Yearmarked
cp target/*dependencies.jar $MCS/plugins/Yearmarked.jar
echo $