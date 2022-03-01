#!/bin/bash

disk=${1}

part_label=`parted ${disk} p | grep "Partition Table: unknown" | wc -l`
if [ $part_label == 0 ]
then
        part_init="yes"
else
        part_init="no"
fi
echo $part_init
