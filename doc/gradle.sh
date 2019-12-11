#!/bin/sh
mv user.properties  user.properties.orig
./gradlew $*
mv user.properties.orig  user.properties
