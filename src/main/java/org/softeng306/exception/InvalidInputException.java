package org.softeng306.exception;

public class InvalidInputException extends Exception {
    public InvalidInputException () {
        super("""
            Usage:
            java -jar scheduler.jar INPUT.dot P [OPTION]
            INPUT.dot    a task graph with integer weights in dot format
            P            number of processors to schedule the INPUT graph on
        
            Optional:
            -p N        use N cores for execution in parallel (default is sequential)
            -v          visualise the search
            -o OUTPUT   output file is named OUTPUT (default is INPUT-output.dot)
            """);

    }
}
