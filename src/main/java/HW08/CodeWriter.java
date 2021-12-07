package HW08;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CodeWriter {
    private int arthJumpFlag;
    private PrintWriter outputStream;

    public CodeWriter(File fileOut) {
        try {
            outputStream = new PrintWriter(fileOut);
            arthJumpFlag = 0;
        } catch (FileNotFoundException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {
        switch (command) {
            case "add":
                outputStream.print(arithmeticTemplate1() + "M=M+D\n");
                break;
            case "sub":
                outputStream.print(arithmeticTemplate1() + "M=M-D\n");
                break;
            case "and":
                outputStream.print(arithmeticTemplate1() + "M=M&D\n");
                break;
            case "or":
                outputStream.print(arithmeticTemplate1() + "M=M|D\n");
                break;
            case "gt":
                outputStream.print(arithmeticTemplate2("JLE"));
                arthJumpFlag++;
                break;
            case "lt":
                outputStream.print(arithmeticTemplate2("JGE"));
                arthJumpFlag++;
                break;
            case "eq":
                outputStream.print(arithmeticTemplate2("JNE"));
                arthJumpFlag++;
                break;
            case "not":
                outputStream.print("@SP\nA=M-1\nM=!M\n");
                break;
            case "neg":
                outputStream.print("D=0\n@SP\nA=M-1\nM=D-M\n");
                break;
            default:
                throw new IllegalArgumentException("ERROR: Call writeArithmetic() for a non-arithmetic command!");
        }
    }

    public void writePushPop(int command, String fragment, int index) {
        if (command == Parser.PUSH) {
            if (fragment.equals("constant")) {
                outputStream.print("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (fragment.equals("local")) {
                outputStream.print(pushTemplate("LCL", index, false));
            } else if (fragment.equals("argument")) {
                outputStream.print(pushTemplate("ARG", index, false));
            } else if (fragment.equals("this")) {
                outputStream.print(pushTemplate("THIS", index, false));
            } else if (fragment.equals("that")) {
                outputStream.print(pushTemplate("THAT", index, false));
            } else if (fragment.equals("temp")) {
                outputStream.print(pushTemplate("R5", index + 5, false));
            } else if (fragment.equals("pointer") && index == 0) {
                outputStream.print(pushTemplate("THIS", index, true));
            } else if (fragment.equals("pointer") && index == 1) {
                outputStream.print(pushTemplate("THAT", index, true));
            } else if (fragment.equals("static")) {
                outputStream.print(pushTemplate(String.valueOf(16 + index), index, true));
            }
        } else if (command == Parser.POP) {
            if (fragment.equals("local")) {
                outputStream.print(popTemplate("LCL", index, false));
            } else if (fragment.equals("argument")) {
                outputStream.print(popTemplate("ARG", index, false));
            } else if (fragment.equals("this")) {
                outputStream.print(popTemplate("THIS", index, false));
            } else if (fragment.equals("that")) {
                outputStream.print(popTemplate("THAT", index, false));
            } else if (fragment.equals("temp")) {
                outputStream.print(popTemplate("R5", index + 5, false));
            } else if (fragment.equals("pointer") && index == 0) {
                outputStream.print(popTemplate("THIS", index, true));
            } else if (fragment.equals("pointer") && index == 1) {
                outputStream.print(popTemplate("THAT", index, true));
            } else if (fragment.equals("static")) {
                outputStream.print(popTemplate(String.valueOf(16 + index), index, true));
            }
        } else {
            throw new IllegalArgumentException("ERROR: Called writePushPop() for a non-pushpop command!");
        }
    }

    private String arithmeticTemplate1() {
        return "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n";
    }

    private String arithmeticTemplate2(String type) {
        return "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" +
                "@FALSE" + arthJumpFlag + "\n" + "D;" + type + "\n" +
                "@SP\n" + "A=M-1\n" + "M=-1\n" + "@CONTINUE" + arthJumpFlag + "\n" +
                "0;JMP\n" + "(FALSE" + arthJumpFlag + ")\n" + "@SP\n" + "A=M-1\n" +
                "M=0\n" + "(CONTINUE" + arthJumpFlag + ")\n";
    }

    private String pushTemplate(String fragment, int index, boolean isDirect) {
        String noPointerCode = (isDirect) ? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + fragment + "\n" + "D=M\n" + noPointerCode + "@SP\n" + "A=M\n" +
                "M=D\n" + "@SP\n" + "M=M+1\n";
    }

    private String popTemplate(String fragment, int index, boolean isDirect) {
        String noPointerCode = (isDirect) ? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

        return "@" + fragment + "\n" + noPointerCode + "@R13\n" + "M=D\n" + "@SP\n" +
                "AM=M-1\n" + "D=M\n" + "@R13\n" + "A=M\n" + "M=D\n";
    }

    public void close() {
        outputStream.close();
    }
}