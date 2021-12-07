package HW07;

import java.io.*;
import java.util.Scanner;

public class Parser {

    public static final char NO_COMMAND = 'N';
    public static final char A_COMMAND = 'A';
    public static final char C_COMMAND = 'C';
    public static final char L_COMMAND = 'L';

    private Scanner inputFile;

    private int lineNumber;
    private String rawLine;
    private String cleanLine;

    private char commandType;
    private String symbol;
    private String destMnemonic;
    private String compMnemonic;
    private String jumpMnemonic;

    public Parser(String fileName) {
        try {
            inputFile = new Scanner(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found!");
            System.exit(0);
        }
        this.lineNumber = 0;
    }

    public boolean hasMoreCommands() {
        if (inputFile.hasNextLine()) {
            return true;
        } else {
            inputFile.close();
            return false;
        }
    }

    public void advance() {
        if (hasMoreCommands()) {
            this.rawLine = inputFile.nextLine();
            cleanLine();
            parseCommandType();
            parse();
        }

        if (commandType != 'N' && commandType != 'L') {
            lineNumber++;
        }
    }

    private void cleanLine() {
        this.cleanLine = getRawLine().replaceAll(" ", "");
        this.cleanLine = getCleanLine().replaceAll("\t", "");
        int commentLoc = getCleanLine().indexOf("//");

        if (commentLoc != -1) {
            this.cleanLine = getCleanLine().substring(0, commentLoc);
        }
    }

    private void parseCommandType() {
        if (this.cleanLine == null || getCleanLine().isEmpty()) {
            this.commandType = NO_COMMAND;
        } else if (getCleanLine().contains("@")) {
            this.commandType = A_COMMAND;
        } else if (getCleanLine().contains("(") || getCleanLine().contains(")")) {
            this.commandType = L_COMMAND;
        } else {
            this.commandType = C_COMMAND;
        }
    }

    private void parse() {
        if (this.commandType == NO_COMMAND) {
            //Not sure what I should put here. Nothing?
        } else if ((this.commandType == A_COMMAND) || (this.commandType == L_COMMAND)) {
            this.parseSymbol();
        } else if (this.commandType == C_COMMAND) {
            this.parseDest();
            this.parseComp();
            this.parseJump();
        }
    }

    private void parseSymbol() {
        String name = getCleanLine();

        if (this.commandType == A_COMMAND) {
            this.symbol = name.replaceAll("@", "");
        } else if (this.commandType == L_COMMAND) {
            name = name.replaceAll("[()]", "");
            this.symbol = name;
        }
    }

    private void parseComp() {
        String line = getCleanLine();

        if (this.commandType == C_COMMAND) {
            if (line.contains("=")) {
                this.compMnemonic = line.substring((line.indexOf('=')) + 1);
            } else if (line.contains(";")) {
                this.compMnemonic = line.substring(0, line.indexOf(';'));
            }
        }
    }

    private void parseDest() {
        if (this.getCleanLine().contains("=")) {
            this.destMnemonic = getCleanLine().substring(0, getCleanLine().indexOf('='));
        } else {
            this.destMnemonic = "null";
        }
    }

    private void parseJump() {
        if (this.commandType == C_COMMAND) {
            if (this.getCleanLine().contains(";")) {
                this.jumpMnemonic = getCleanLine().substring((getCleanLine().indexOf(";")) + 1);
            } else {
                this.jumpMnemonic = "null";
            }
        }
    }

    private String getCleanLine() {
        return this.cleanLine;
    }

    public char getCommandType() {
        return this.commandType;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getDest() {
        return this.destMnemonic;
    }

    public String getComp() {
        return this.compMnemonic;
    }

    public String getJump() {
        return this.jumpMnemonic;
    }

    public String getCommandTypeString() {
        return Character.toString(this.commandType);
    }

    public String getRawLine() {
        return this.rawLine;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }
}