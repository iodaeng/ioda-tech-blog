#!/bin/bash

if [ ! -d target/sitegen ];
  then git clone git@github.com:iodaeng/iodaeng.github.com.git target/sitegen;
fi
mvn install
