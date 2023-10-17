#!/bin/bash

# Run PowerShell commands in the background
./mvnw package;
java -jar scheduler.jar src/main/resources/e1.dot 2
java -jar scheduler.jar src/main/resources/Nodes_7_OutTree.dot 2
java -jar scheduler.jar src/main/resources/Nodes_8_Random.dot 2
java -jar scheduler.jar src/main/resources/Nodes_9_SeriesParallel.dot 2
java -jar scheduler.jar src/main/resources/Nodes_10_Random.dot 2
java -jar scheduler.jar src/main/resources/Nodes_11_OutTree.dot 2

# Wait for all background jobs to finish
wait