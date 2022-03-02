#!/bin/bash

for duids in `cat /etc/miniodiskuid`
do
        dhost=`hostname`
        did=`blkid | grep $duids | cut -d":" -f1`
        dcount=`kubectl directpv drives ls | grep -w $did | grep -w $dhost | grep "Available" | wc -l`
done
echo `cat /etc/miniodiskuid`
