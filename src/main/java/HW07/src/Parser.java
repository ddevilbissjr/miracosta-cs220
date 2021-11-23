package HW07.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private Scanner cmds;
    private String currentCmd;
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
    private int argType;
    private String argument1;
    private int argument2;

    static {
        arithmeticCmds.add("add");arithmeticCmds.add("sub");arithmeticCmds.add("neg");arithmeticCmds.add("eq");arithmeticCmds.add("gt");
        arithmeticCmds.add("lt");arithmeticCmds.add("and");arithmeticCmds.add("or");arithmeticCmds.add("not");
    }

    public Parser(File fileIn) {
        argType = -1;
        argument1 = "";
        argument2 = -1;

        try {
            cmds = new Scanner(fileIn);

            String preprocessed = "";
            String line = "";

            while(cmds.hasNext()){
                line = noComments(cmds.nextLine()).trim();

                if (line.length() > 0) {
                    preprocessed += line + "\n";
                }
            }

            cmds = new Scanner(preprocessed.trim());
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public boolean hasMoreCommands(){
       return cmds.hasNextLine();
    }

    public void advance(){
        currentCmd = cmds.nextLine();
        argument1 = "";//initialize arg1
        argument2 = -1;//initialize arg2

        String[] segs = currentCmd.split(" ");

        if (segs.length > 3){
            throw new IllegalArgumentException("Too much arguments!");
        }

        if (arithmeticCmds.contains(segs[0])){
            argType = ARITHMETIC;
            argument1 = segs[0];

        } else if (segs[0].equals("return")) {
            argType = RETURN;
            argument1 = segs[0];

        } else {
            argument1 = segs[1];

            if(segs[0].equals("push")){
                argType = PUSH;
            } else if(segs[0].equals("pop")){
                argType = POP;
            } else if(segs[0].equals("label")){
                argType = LABEL;
            } else if(segs[0].equals("if-goto")){
                argType = IF;
            } else if (segs[0].equals("goto")){
                argType = GOTO;
            } else if (segs[0].equals("function")){
                argType = FUNCTION;
            } else if (segs[0].equals("call")){
                argType = CALL;
            } else {
                throw new IllegalArgumentException("Unknown Command Type!");
            }

            if (argType == PUSH || argType == POP || argType == FUNCTION || argType == CALL){
                try {
                    argument2 = Integer.parseInt(segs[2]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Argument2 is not an integer!");
                }
            }
        }
    }

    public int commandType(){
        if (argType != -1) {
            return argType;
        } else {
            throw new IllegalStateException("No command!");
        }
    }

    public String arg1(){
        if (commandType() != RETURN){
            return argument1;
        } else {
            throw new IllegalStateException("Can not get arg1 from a RETURN type command!");
        }
    }

    public int arg2(){
        if (commandType() == PUSH || commandType() == POP || commandType() == FUNCTION || commandType() == CALL){
            return argument2;
        } else {
            throw new IllegalStateException("Can not get arg2!");
        }
    }

    public static String noComments(String strIn){
        int position = strIn.indexOf("//");
        if (position != -1){
            strIn = strIn.substring(0, position);
        }

        return strIn;
    }

    public static String noSpaces(String strIn){
        String result = "";
        if (strIn.length() != 0){
            String[] segs = strIn.split(" ");

            for (String s: segs){
                result += s;
            }
        }

        return result;
    }

    public static String getExt(String fileName){
        int index = fileName.lastIndexOf('.');
        if (index != -1){
            return fileName.substring(index);
        } else {
            return "";
        }
    }
}
