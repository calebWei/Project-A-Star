package org.softeng306.fileio;


public interface IO {
    /**
     * get input dot file reader to create the graph by calling the scheduleIOUtility function
     * @return a reader generated by the content of input dot file
     */
    String getInputFilePath();

    /**
     * get the output file path
     * @return a string of output file path
     */
    String getOutputFilePath();

    /**
     * get the number of processore which is the second arguments input in args
     * @return the number of processor
     */
    int getNumberOfProcessor();

    /**
     * get the number of cores which is the -p parameter
     * @return the number of cores which is the -p parameter
     */
    int getNumberOfCores();

    /**
     * get the mode which way to show the graph in  VISUALISE or COMMAND_LINE
     * @return VISUALISE or COMMAND_LINE
     */
    DisplayMode getVisualisationState();

    /**
     * get the name of the output file(i just create it to check the testcase not sure this function would be used)
     * @return name of output file
     */
    String getOutputFileName();

    /**
     * get name of the input file name.
     * @return name of input file
     */
    String getInputFileName();
}
