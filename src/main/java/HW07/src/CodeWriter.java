package HW07.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeWriter {

    private int jumpFlag;
    private PrintWriter printer;
    private static final Pattern labelReg = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
    private static int label = 0;
    private static String fileName = "";

    public CodeWriter(File file) {
        try {
            fileName = file.getName();
            printer = new PrintWriter(file);
            jumpFlag = 0;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(File fileOut) {
        fileName = fileOut.getName();
    }

    public void writeArithmetic(String command) {
        if (command.equals("add")) {
            printer.print(arithmeticTemplate1() + "M=M+D\n");
        } else if (command.equals("sub")) {
            printer.print(arithmeticTemplate1() + "M=M-D\n");
        } else if (command.equals("and")) {
            printer.print(arithmeticTemplate1() + "M=M&D\n");
        } else if (command.equals("or")) {
            printer.print(arithmeticTemplate1() + "M=M|D\n");
        } else if (command.equals("gt")) {
            printer.print(arithmeticTemplate2("JLE"));//not <=
            jumpFlag++;
        } else if (command.equals("lt")) {
            printer.print(arithmeticTemplate2("JGE"));//not >=
            jumpFlag++;
        } else if (command.equals("eq")) {
            printer.print(arithmeticTemplate2("JNE"));//not <>
            jumpFlag++;
        } else if (command.equals("not")) {
            printer.print("@SP\nA=M-1\nM=!M\n");
        } else if (command.equals("neg")) {
            printer.print("D=0\n@SP\nA=M-1\nM=D-M\n");
        } else {
            throw new IllegalArgumentException("Call writeArithmetic() for a non-arithmetic command");
        }
    }

    public void writePushPop(int command, String segment, int index) {
        if (command == Parser.PUSH) {
            if (segment.equals("constant")) {
                printer.print("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (segment.equals("local")) {
                printer.print(pushTemplate1("LCL", index, false));
            } else if (segment.equals("argument")) {
                printer.print(pushTemplate1("ARG", index, false));
            } else if (segment.equals("this")) {
                printer.print(pushTemplate1("THIS", index, false));
            } else if (segment.equals("that")) {
                printer.print(pushTemplate1("THAT", index, false));
            } else if (segment.equals("temp")) {
                printer.print(pushTemplate1("R5", index + 5, false));
            } else if (segment.equals("pointer") && index == 0) {
                printer.print(pushTemplate1("THIS", index, true));
            } else if (segment.equals("pointer") && index == 1) {
                printer.print(pushTemplate1("THAT", index, true));
            } else if (segment.equals("static")) {
                printer.print("@" + fileName + index + "\n" + "D=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }

        } else if (command == Parser.POP) {
            if (segment.equals("local")) {
                printer.print(popTemplate1("LCL", index, false));
            } else if (segment.equals("argument")) {
                printer.print(popTemplate1("ARG", index, false));
            } else if (segment.equals("this")) {
                printer.print(popTemplate1("THIS", index, false));
            } else if (segment.equals("that")) {
                printer.print(popTemplate1("THAT", index, false));
            } else if (segment.equals("temp")) {
                printer.print(popTemplate1("R5", index + 5, false));
            } else if (segment.equals("pointer") && index == 0) {
                printer.print(popTemplate1("THIS", index, true));
            } else if (segment.equals("pointer") && index == 1) {
                printer.print(popTemplate1("THAT", index, true));
            } else if (segment.equals("static")) {
                printer.print("@" + fileName + index + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
            }

        } else {
            throw new IllegalArgumentException("Call writePushPop() for a non-pushpop command");
        }
    }

    public void writeLabel(String label) {
        Matcher m = labelReg.matcher(label);
        if (m.find()) {
            printer.print("(" + label + ")\n");
        } else {
            throw new IllegalArgumentException("Wrong label format!");
        }
    }

    public void writeGoto(String label) {
        Matcher m = labelReg.matcher(label);
        if (m.find()) {
            printer.print("@" + label + "\n0;JMP\n");
        } else {
            throw new IllegalArgumentException("Wrong label format!");
        }
    }

    public void writeIf(String label) {
        Matcher m = labelReg.matcher(label);
        if (m.find()) {
            printer.print(arithmeticTemplate1() + "@" + label + "\nD;JNE\n");
        } else {
            throw new IllegalArgumentException("Wrong label format!");
        }
    }

    public void writeInit() {
        printer.print("@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n"
        );
        writeCall("Sys.init", 0);
    }

    public void writeCall(String functionName, int numArgs) {
        String newLabel = "RETURN_LABEL" + (label++);

        printer.print("@" + newLabel + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        printer.print(pushTemplate1("LCL", 0, true));
        printer.print(pushTemplate1("ARG", 0, true));
        printer.print(pushTemplate1("THIS", 0, true));
        printer.print(pushTemplate1("THAT", 0, true));

        printer.print("@SP\n" +
                "D=M\n" +
                "@5\n" +
                "D=D-A\n" +
                "@" + numArgs + "\n" +
                "D=D-A\n" +
                "@ARG\n" +
                "M=D\n" +
                "@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +
                "@" + functionName + "\n" +
                "0;JMP\n" +
                "(" + newLabel + ")\n"
        );
    }

    public void writeReturn() {
        printer.print(returnTemplate());
    }

    public void writeFunction(String functionName, int numLocals) {
        printer.print("(" + functionName + ")\n");
        for (int i = 0; i < numLocals; i++) {
            writePushPop(Parser.PUSH, "constant", 0);
        }
    }

    public String preFrameTemplate(String position) {
        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + position + "\n" +
                "M=D\n";
    }

    public String returnTemplate() {
        return "@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n" +
                popTemplate1("ARG", 0, false) +
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n" +
                preFrameTemplate("THAT") +
                preFrameTemplate("THIS") +
                preFrameTemplate("ARG") +
                preFrameTemplate("LCL") +
                "@R12\n" +
                "A=M\n" +
                "0;JMP\n";
    }

    public void close() {
        printer.close();
    }

    private String arithmeticTemplate1() {
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n";
    }

    private String arithmeticTemplate2(String type) {
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@FALSE" + jumpFlag + "\n" +
                "D;" + type + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "@CONTINUE" + jumpFlag + "\n" +
                "0;JMP\n" +
                "(FALSE" + jumpFlag + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "(CONTINUE" + jumpFlag + ")\n";

    }

    private String pushTemplate1(String segment, int index, boolean isDirect) {
        String noPointerCode = (isDirect) ? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + segment + "\n" +
                "D=M\n" +
                noPointerCode +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";
    }

    private String popTemplate1(String segment, int index, boolean isDirect) {
        String noPointerCode = (isDirect) ? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

        return "@" + segment + "\n" +
                noPointerCode +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";
    }
}
