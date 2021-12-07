package HW07;

import java.io.*;

public class Assembler {

    public static void main(String[] args) {

        String inputFileName, outputFileName;
        PrintWriter outputFile = null;
        SymbolTable symbolTable;

        if (args.length == 0) {
            System.out.println("Assembler for .asm files, outputs as .hack file. Usage: Assembler.sh <filename>.asm");
            return;
        } else if (args.length == 1){
            inputFileName = args[0];
        } else {
            System.out.println("Invalid arguments! Usage: Assembler.sh <filename>.asm");
            return;
        }

        outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".hack";

        try {
            outputFile = new PrintWriter(new File(outputFileName));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        symbolTable = new SymbolTable();

        firstPass(inputFileName, symbolTable);
        System.out.println("50%...");

        secondPass(inputFileName, symbolTable, outputFile);
        System.out.println("100%!");

        outputFile.close();
    }

    private static void firstPass(String inputFileName, SymbolTable symbolTable) {
        Parser parse = new Parser(inputFileName);
        String symbol = "";
        int romAddress;

        while (parse.hasMoreCommands()) {
            parse.advance();
            romAddress = parse.getLineNumber();

            if (parse.getCommandType() == 'L') {
                symbol = parse.getSymbol();
                symbolTable.addEntry(symbol, romAddress);
            }
        }
    }

    private static void secondPass(String inputFileName, SymbolTable symbolTable, PrintWriter outputFile) {
        Parser parse = new Parser(inputFileName);
        Code codeTable = new Code();

        String outputCode = "";
        int ramAddress = 16;

        while (parse.hasMoreCommands()) {
            parse.advance();

            if (parse.getCommandType() == 'C') {
                if (codeTable.getComp(parse.getComp()) == null || codeTable.getDest(parse.getDest()) == null || codeTable.getJump(parse.getJump()) == null) {
                    System.out.println("Syntax error at line " + parse.getLineNumber());
                    System.exit(0);
                } else {
                    outputCode = "111" + codeTable.getComp(parse.getComp()) + codeTable.getDest(parse.getDest()) + codeTable.getJump(parse.getJump());
                    outputFile.println(outputCode);
                }
            } else if (parse.getCommandType() == 'A') {
                int num = 0;
                String numbers = "0123456789";
                String symbol = parse.getSymbol();

                if (numbers.indexOf(symbol.charAt(0)) != -1) {
                    try {
                        num = Integer.parseInt(symbol);
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        System.out.println("Incorrect variable at line " + parse.getLineNumber());
                        System.exit(0);
                    }

                    outputCode = "" + codeTable.decimalToBinary(num);
                    outputFile.println(outputCode);
                } else {
                    if (symbolTable.contains(symbol)) {
                        num = symbolTable.getAddress(symbol);

                        outputCode = "" + codeTable.decimalToBinary(num);
                        outputFile.println(outputCode);
                    } else {
                        if (!symbolTable.addEntry(symbol, ramAddress)) {
                            System.out.println("Syntax error at line " + parse.getLineNumber());
                            System.exit(0);
                        }
                    }

                    outputCode = "" + codeTable.decimalToBinary(ramAddress);
                    outputFile.println(outputCode);
                    ramAddress++;
                }
            }
        }
    }
}
