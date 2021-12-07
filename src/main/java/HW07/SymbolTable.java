package HW07;

import java.util.HashMap;

public class SymbolTable {

    private static final String INITIAL_VALID_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm_.$:";
    private static final String VALID_CHARS = INITIAL_VALID_CHARS + "0123456789";

    private HashMap<String, Integer> table;

    public SymbolTable() {
        table = new HashMap<>(30);

        table.put("R0", 0);
        table.put("R1", 1);
        table.put("R2", 2);
        table.put("R3", 3);
        table.put("R4", 4);
        table.put("R5", 5);
        table.put("R6", 6);
        table.put("R7", 7);
        table.put("R8", 8);
        table.put("R9", 9);
        table.put("R10", 10);
        table.put("R11", 11);
        table.put("R12", 12);
        table.put("R13", 13);
        table.put("R14", 14);
        table.put("R15", 15);

        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
        table.put("WRITE", 18);
        table.put("END", 22);
        table.put("i", 16);
        table.put("sum", 17);
    }

    private boolean isValidName(String symbol) {
        boolean valid = false;

        if (INITIAL_VALID_CHARS.contains(Character.toString(symbol.charAt(0)))) {
            for (int i = 1; i < symbol.length(); i++) {
                if (VALID_CHARS.contains(Character.toString(symbol.charAt(i)))) {
                    valid = true;
                } else {
                    System.out.println("Invalid symbol name! Contains invalid character. (" + symbol + ")");
                    valid = false;
                }
            }
            return valid;
        } else {
            System.out.println("Invalid symbol name! First character invalid. (" + symbol + ")");
            return false;
        }
    }

    public boolean addEntry(String symbol, int address) {
        if (this.contains(symbol)) {
            System.out.println("Invalid symbol name! Symbol name already used. (" + symbol + ")");
            return false;
        }

        if (this.isValidName(symbol)) {
            table.put(symbol, address);
        }

        return true;
    }

    public int getAddress(String symbol) {
        if (this.contains(symbol)) {
            return table.get(symbol);
        } else {
            System.out.println("Symbol not found! Returning null. (" + symbol + ")");
            return -1;
        }
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }
}