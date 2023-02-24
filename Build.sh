#!/bin/bash

src_files=$(ls ./src/*.java)

javac -d . src_files