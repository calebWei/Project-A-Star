package org.softeng306.fileio;

import org.softeng306.exception.InvalidInputException;

import java.io.*;

public class IOImp implements IO{
    private String outputFilePath;
    private String outputFileName;
    private String inputFileName;
    //input after dotFile
    private int numberOfProcessor;
    // -p
    private int numberOfCores = 1;
    private String inputFilePath;

    private DisplayMode visualisationState = DisplayMode.COMMAND_LINE;

    public IOImp(String[] inputArgs) throws InvalidInputException, FileNotFoundException {
        initialise(inputArgs);
    }

    private void initialise(String[] inputArgs) throws InvalidInputException, FileNotFoundException {
        // the minimum number of parameters is 2
        if(inputArgs.length< 2){
            throw new InvalidInputException();
        }

        String parentPath = "";
        // check if input file exist
        File inputFile = new File(inputArgs[0]);
        inputFilePath = inputFile.getAbsolutePath();

        if (inputFile.exists()) {
            parentPath = inputFile.getAbsoluteFile().getParent();

            String[] fileNameSplit = inputFile.getName().split("\\.");
            String basename = fileNameSplit[0];
            inputFileName = basename + ".dot";
            outputFileName = basename + "-output.dot";
            outputFilePath = parentPath + File.separator + outputFileName;
        } else {
            throw new FileNotFoundException("File not found - please verify name");
        }

        // check if the input of processor is valid
        numberOfProcessor = Integer.parseInt(inputArgs[1]);
        if (numberOfProcessor < 1) {
            throw new InvalidInputException();
        }
        // check the optional values of the user input
        int i = 2;
        while (i < inputArgs.length) {
            switch (inputArgs[i]) {
                //using parallel
                case "-p" -> {
                    numberOfCores = Integer.parseInt(inputArgs[i + 1]);
                    if (numberOfCores < 1 || numberOfCores > Runtime.getRuntime().availableProcessors()) {
                        throw new InvalidInputException();
                    }
                    i++;
                }
                //Use visualization GUI with given file
                case "-v" -> visualisationState = DisplayMode.VISUALISE;

                //change the output file path if the user change the default name
                case "-o" -> {
                    outputFileName = inputArgs[i + 1].contains(".") ? inputArgs[i + 1] : inputArgs[i + 1] + ".dot";
                    if (!inputArgs[i + 1].contains("/")) {
                        outputFilePath = parentPath + File.separator + outputFileName;
                    }
                    i++;
                }
                default -> throw new InvalidInputException();
            }
            i++;
        }
    }

    @Override
    public String getInputFilePath() {
        return inputFilePath;
    }

    @Override
    public String getOutputFilePath() {
        return outputFilePath;
    }

    @Override
    public int getNumberOfProcessor() {
        return numberOfProcessor;
    }

    @Override
    public int getNumberOfCores() {
        return numberOfCores;
    }

    @Override
    public DisplayMode getVisualisationState() {
        return visualisationState;
    }

    @Override
    public String getOutputFileName() {
        return outputFileName;
    }

    @Override
    public String getInputFileName() { return inputFileName; }
}
