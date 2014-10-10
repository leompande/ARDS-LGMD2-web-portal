#!/bin/sh

cd ../..

for p in local/tz/*.patch; do
	patch -p0 < $p 
done

cd local/tz

