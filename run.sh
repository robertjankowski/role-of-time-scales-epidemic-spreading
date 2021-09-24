#!/bin/bash

config_file=$1
prefix=$2

mvn compile exec:java -Dexec.mainClass="pl.wf.Main" -Dexec.args="$config_file $prefix"
