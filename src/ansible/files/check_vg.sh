#!/bin/bash

vg=${1}

vg_count=`vgs | grep -w ${vg} | wc -l`
if [ ${vg_count} == 0 ]
then
        vg_check="no"
else
        vg_check="yes"
fi
echo $vg_check
