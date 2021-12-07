package HW08;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    public static final int ARITHMETIC = 0;
    public static final int PUSH = 1;
    public static final int POP = 2;
    public static final int LABEL = 3;
    public static final int GOTO = 4;
    public static final int IF = 5;
    public static final int FUNCTION = 6;
    public static final int RETURN = 7;
    public static final int CALL = 8;

    public static final ArrayList<String> arithmeticCmds = new ArrayList<String>();

    static {
        arithmeticCmds.add("add");
        arithmeticCmds.add("sub");
        arithmeticCmds.add("neg");
        arithmeticCmds.add("eq");
        arithmeticCmds.add("gt");
        arithmeticCmds.add("lt");
        arithmeticCmds.add("and");
        arithmeticCmds.add("or");
        arithmeticCmds.add("not");
    }

    private Scanner commands;
    private String currentCommand;

    private int argumentType;
    private String argument1;
    private int argument2;

    public Parser(File inputFile) {
        argumentType = -1;
        argument1 = "";
        argument2 = -1;

        try {
            commands = new Scanner(inputFile);

            String preprocessed = "";
            String line = "";

            while (commands.hasNext()) {
                line = removeComments(commands.nextLine()).trim();

                if (line.length() > 0) {
                    preprocessed += line + "\n";
                }
            }
            commands = new Scanner(preprocessed.trim());
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found!");
            e.getMessage();
            e.printStackTrace();
        }
    }

    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');

        if (index != -1) {
            return fileName.substring(index);
        } else {
            System.out.println("ERROR: File extension could not be found!");
            return "";
        }
    }

    private static String removeComments(String line) {
        int position = line.indexOf("//");

        if (position != -1) {
            line = line.substring(0, position);
        }
        return line;
    }

    private static String removeSpaces(String line) {
        String result = "";

        if (line.length() != 0) {
            String[] segment = line.split(" ");

            for (String s : segment) {
                result += s;
            }
        }
        return result;
    }

    public boolean hasMoreCommands() {
        return commands.hasNextLine();
    }

    public void advance() {

        currentCommand = commands.nextLine();
        argument1 = "";
        argument2 = -1;

        String[] segment = currentCommand.split(" ");

        if (segment.length > 3) {
            throw new IllegalArgumentException("ERROR: Too many arguments!");
        }
        if (arithmeticCmds.contains(segment[0])) {
            argumentType = ARITHMETIC;
            argument1 = segment[0];
        } else if (segment[0].equals("return")) {
            argumentType = RETURN;
            argument1 = segment[0];
        } else {
            argument1 = segment[1];

            if (segment[0].equals("push")) {
                argumentType = PUSH;
            } else if (segment[0].equals("pop")) {
                argumentType = POP;
            } else if (segment[0].equals("label")) {
                argumentType = LABEL;
            } else if (segment[0].equals("if")) {
                argumentType = IF;
            } else if (segment[0].equals("goto")) {
                argumentType = GOTO;
            } else if (segment[0].equals("function")) {
                argumentType = FUNCTION;
            } else if (segment[0].equals("call")) {
                argumentType = CALL;
            } else {
                throw new IllegalArgumentException("Unknown Command Type!");
            }
            if (argumentType == PUSH || argumentType == POP || argumentType == FUNCTION || argumentType == CALL) {
                try {
                    argument2 = Integer.parseInt(segment[2]);
                } catch (Exception e) {
                    e.getMessage();
                    e.printStackTrace();
                    throw new IllegalArgumentException("ERROR: argument2 is not an integer!");
                }
            }
        }
    }

    public int getCommandType() {
        if (argumentType != -1) {
            return argumentType;
        } else {
            throw new IllegalStateException("ERROR: No command found!");
        }
    }

    public String getArgument1() {

        if (getCommandType() != RETURN) {
            return argument1;
        } else {
            throw new IllegalStateException("ERROR: Can not get arg1 from a 'RETURN' type command!");
        }
    }

    public int getArgument2() {
        if (getCommandType() == PUSH || getCommandType() == POP || getCommandType() == FUNCTION || getCommandType() == CALL) {
            return argument2;
        } else {
            throw new IllegalStateException("ERROR: Can not find argument2!");
        }
    }
}