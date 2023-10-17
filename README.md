
![A star banner](https://github.com/UOASOFTENG306/project-2-team-19/assets/100410646/6e7709f0-566e-4c39-a646-eb43ea4d3669)
# Project A Star⭐
- A solution to generating optimal multi-processor schedules, based on the input task graph. This is an NP-hard problem.
- Utilizes JGraphT, GraphStream, JavaFx, AtlantaFx, JUnit libraries
- This project is developed by [Team 19](#-contributors---team-19) for project 2 of SOFTENG 306.
- Credits to [Oliver](https://profiles.auckland.ac.nz/o-sinnen) for giving us the oppotunity to work on this cool project, as well as his research papers which gave team 19 useful insights: [Scheduling task graphs optimally with A*](https://doi.org/10.1007/s11227-010-0395-1) and [Reducing the solution space of optimal task scheduling](https://doi.org/10.1016/j.cor.2013.09.004).

## 📑 Instructions
In order to run the scheduler, please first download the `scheduler.jar` in the latest release.
Given a task graph in DOT format, you can run the JAR file with command:

```
java -jar scheduler.jar INPUT.dot P [OPTION]
INPUT.dot    a task graph with integer weights in dot format
P            number of processors to schedule the INPUT graph on

Optional:
-p N        use N cores for execution in parallel (default is sequential)
-v          visualise the search
-o OUTPUT   output file is named OUTPUT (default is INPUT-output.dot)
```

An output file in dot format should be produced in the same directory as the input.

### 📊 Visualisation
The `-v` flag will provide additional visualisation and utilities tools to examine the A* search in detail. Each schedule from the search can be examined in three different ways:

#### 1. Graph View
![image](https://github.com/UOASOFTENG306/project-2-team-19/assets/100410646/f38b15a4-487c-4daf-be02-e44104ad4a17)

#### 2. Table View
![image](https://github.com/UOASOFTENG306/project-2-team-19/assets/100410646/51ba8831-ab8b-447f-8b8c-bf6aad610516)

#### 3. Gantt Chart View 
![image](https://github.com/UOASOFTENG306/project-2-team-19/assets/100410646/8c29b566-0d8c-40bc-beed-970fcec40d71)

Other than information about each individual schedule, you may also find additional information related to the search on the right-hand side in the `Performance Data` tab.

### 👏 Example
The input task graph `e1.dot` is given in DOT format, where:
- Each vertex represents a task, vertext weight represents its execution time
- Each edge shows a relationship where one task (origin) must be completed as a prerequisite to another task (destination) before they can start, the corresponding edge weight represents additional context-switch time cost if the two tasks are executed on different processors.
```
digraph "example" {
a [Weight=2];
b [Weight=3];
a -> b [Weight=1];
c [Weight=3];
a -> c [Weight=2];
d [Weight=2];
b -> d [Weight=2];
c -> d [Weight=1];
}
```

Now run the command `java -jar scheduler.jar e1.dot 2 -v`.

The returned output file `e1-output.dot` is generated in DOT format, where each vertex holds information for:
- Weight: the time taken to execute the task
- Start: the start time for the task relative to start of the schedule
- Processor: The ID of the processor for this task to be executed on
```
digraph "e1-output" {
	a -> b [Weight=1];	
	a -> c [Weight=2];	
	b -> d [Weight=2];	
	c -> d [Weight=1];	
	b	 [Weight=3,Start=3,Processor=2];
	c	 [Weight=3,Start=2,Processor=1];
	d	 [Weight=2,Start=6,Processor=2];
	a	 [Weight=2,Start=0,Processor=1];
}
```
This is what the Gantt chart tab looks like for the solution generated above.

![image](https://github.com/UOASOFTENG306/project-2-team-19/assets/100410646/7daee266-2717-4620-ae11-0c45d1b414f7)

## 🏆 Contributors - Team 19
- Caleb Wei - jwei578
- Matthew Stevens - mste900
- James Gai - jgai284
- Xinhuiqiang Xu - xxu511
- Daniel Eir - deir905

## GitHub workflow violation - James Gai
The following commits were pushed to main branch directly by this guy.
![image](https://github.com/UOASOFTENG306/project-2-team-19/assets/100669707/7625ed87-12db-4bee-9129-b74a1fec45e7)
