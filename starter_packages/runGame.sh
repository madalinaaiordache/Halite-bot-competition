#!/bin/bash

javac MyBot.java
#javac ../../bots/DBot_linux_64bit
./halite -d "30 30" "java MyBot" "../../bots/DBot_linux_64bit"
