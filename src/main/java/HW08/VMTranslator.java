package HW08;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {
    public static void main(String[] args) {
        ArrayList<File> vmFiles = new ArrayList<File>();

        String inputFileName, outputFileName;
        String fileOutPath = "";

        File inputFile = null;
        File outputFile = null;

        CodeWriter writer;

        if (args.length == 1) {
            System.out.println("command line arg = " + args[0]);
            inputFileName = args[0];
        } else {
            Scanner keyboard = new Scanner(System.in);

            System.out.println("Please enter assembly file name you would like to assemble.");
            System.out.println("Don't forget the .vm extension: ");
            inputFileName = keyboard.nextLine();

            keyboard.close();

            inputFile = new File(inputFileName);

            if (inputFile.isFile()) {
                String path = inputFile.getAbsolutePath();

                if (!Parser.getExtension(path).equals(".vm")) {
                    throw new IllegalArgumentException(".vm file is required!");
                }

                vmFiles.add(inputFile);

                fileOutPath = inputFile.getAbsolutePath().substring(0, inputFile.getAbsolutePath().lastIndexOf(".")) + ".asm";

            } else if (inputFile.isDirectory()) {
                vmFiles = getVMFiles(inputFile);

                if (vmFiles.size() == 0) {
                    throw new IllegalArgumentException("ERROR: No .vm file in this directory");
                }
                fileOutPath = inputFile.getAbsolutePath() + "/" + inputFile.getName() + ".asm";
            }

            outputFile = new File(fileOutPath);
            writer = new CodeWriter(outputFile);

            for (File f : vmFiles) {
                Parser parser = new Parser(f);

                int type = -1;

                while (parser.hasMoreCommands()) {
                    parser.advance();

                    type = parser.getCommandType();

                    if (type == Parser.ARITHMETIC) {
                        writer.writeArithmetic(parser.getArgument1());
                    } else if (type == Parser.POP || type == Parser.PUSH) {
                        writer.writePushPop(type, parser.getArgument1(), parser.getArgument2());
                    }
                }
            }
            writer.close();

            System.out.println("\nDone!\n" + "\n...\n");
            System.out.println("File created @ " + fileOutPath);
        }
    }

    public static ArrayList<File> getVMFiles(File directory) {
        File[] files = directory.listFiles();

        ArrayList<File> result = new ArrayList<File>();

        for (File index : files) {
            if (index.getName().endsWith(".vm")) {
                result.add(index);
            }
        }
        return result;
    }
}