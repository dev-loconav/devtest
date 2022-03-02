#!/bin/bash

dh=$1
dd=$2
dcount=`kubectl directpv drives ls | grep -w $dd | grep -w $dh | grep "Available" | wc -l`
if [ $dcount == 1 ]
then
                kubectl directpv drives format --drives $dd --nodes $dh --force
fi
