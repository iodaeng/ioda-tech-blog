#!/bin/bash

cd target/sitegen
git add .
git commit -m "site generated by scalate"
git push origin master
