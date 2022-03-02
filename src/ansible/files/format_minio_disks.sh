#!/bin/bash

for duids in `cat /etc/miniodiskuid`
do
        dhost=`hostname`
        did=`blkid | grep $duids | cut -d":" -f1`
        echo $dhost:$did >> /tmp/diskhostmapping
done
