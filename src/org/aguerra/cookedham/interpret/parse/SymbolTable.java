package org.aguerra.cookedham.interpret.parse;

import org.aguerra.cookedham.interpret.lex.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> symbolTable;

    public SymbolTable(ArrayList<Symbol> symbols) {
        symbolTable = new HashMap<>();

        for (Symbol symbol : symbols) {
            symbolTable.put(symbol.getName(), symbol);
        }
    }

    public boolean isContained(String symbolName) {
        return symbolTable.containsKey(symbolName);
    }
}