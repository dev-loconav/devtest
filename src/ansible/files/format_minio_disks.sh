#!/bin/bash

for duids in `cat /etc/miniodiskuid`
do
        dhost=`hostname`
        did=`blkid | grep $duids | cut -d":" -f1`
        dcount=`kubectl directpv drives ls | grep -w $did | grep -w $dhost | grep "Available" | wc -l`
        echo $d
        echo $dcount
        if [ $dcount == 1 ]
        then
                kubectl directpv drives format --drives $did --nodes $dhost --force
        fi
done
