#!/bin/bash

rm /tmp/diskhostmapping
for diskuids in `cat /etc/miniodiskuid`
do
        dhost=`hostname`
        did=`blkid | grep $diskuids | cut -d":" -f1`
        echo $dhost,$did >> /tmp/diskhostmapping
   
done
cat /tmp/diskhostmapping

