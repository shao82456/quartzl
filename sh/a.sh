#!/usr/bin/env bash
source /etc/profile
for i in $(seq 1 6)
do
    echo ${TESTENV}
    sleep 10
done
