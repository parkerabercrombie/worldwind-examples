#!/bin/bash

# Run a WorldWind Demo

java -Xmx512m -Dsun.java2d.noddraw=true -classpath ./build:./lib/worldwind.jar:./lib/worldwindx.jar:./lib/jogl-all.jar:./lib/gluegen-rt.jar:./lib/gdal.jar com.pabercrombie.worldwindx.GeoServerElevations
