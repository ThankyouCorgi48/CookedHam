package org.aguerra.cookedham.interpret.lex;

import org.aguerra.cookedham.interpret.error.Error;

import java.io.File;
import java.util.ArrayList;

public class Lexer {
    private TokenScanner tokenScanner;
    private ArrayList<Token> tokens;

    public Lexer(File file) {
        //TODO: Implement BufferedInputStream

        tokens = new ArrayList<>();
        tokenScanner = new TokenScanner(file);

        while (tokenScanner.hasNext()) {
            String token = tokenScanner.nextToken();

            if(!token.equals("")) tokens.add(createToken(token));
        }

        tokens.add(new Token("", Type.EOF, tokenScanner.getLineNum()));
    }

    public Lexer(String line) {
        //TODO: Implement BufferedInputStream

        tokens = new ArrayList<>();
        tokenScanner = new TokenScanner(line);

        while (tokenScanner.hasNext()) {
            String token = tokenScanner.nextToken();

            if(!token.equals("")) tokens.add(createToken(token));
        }

        tokens.add(new Token("", Type.EOF, tokenScanner.getLineNum()));
    }

    private Token createToken(String token) {
        //Keywords and Special Characters
        switch (token) {
            case "(" : return new Token(token, Type.LEFT_PAREN, tokenScanner.getLineNum());
            case ")" : return new Token(token, Type.RIGHT_PAREN, tokenScanner.getLineNum());
            case "{" : return new Token(token, Type.LEFT_BRACE, tokenScanner.getLineNum());
            case "}" : return new Token(token, Type.RIGHT_BRACE, tokenScanner.getLineNum());
            case "[" : return new Token(token, Type.LEFT_BRACKET, tokenScanner.getLineNum());
            case "]" : return new Token(token, Type.RIGHT_BRACKET, tokenScanner.getLineNum());
            case "," : return new Token(token, Type.COMMA, tokenScanner.getLineNum());
            case "." : return new Token(token, Type.DOT, tokenScanner.getLineNum());
            case ";" : return new Token(token, Type.SEMICOLON, tokenScanner.getLineNum());
            case ":" : return new Token(token, Type.COLON, tokenScanner.getLineNum());
            case "=" : return new Token(token, Type.ASSIGN, tokenScanner.getLineNum());
            case "+" : return new Token(token, Type.PLUS, tokenScanner.getLineNum());
            case "-" : return new Token(token, Type.MINUS, tokenScanner.getLineNum());
            case "*" : return new Token(token, Type.STAR, tokenScanner.getLineNum());
            case "/" : return new Token(token, Type.SLASH, tokenScanner.getLineNum());
            case "%" : return new Token(token, Type.MOD, tokenScanner.getLineNum());
            case "<" : return new Token(token, Type.LEFT_ANGLE_BRACE, tokenScanner.getLineNum());
            case ">" : return new Token(token, Type.RIGHT_ANGLE_BRACE, tokenScanner.getLineNum());
            case "!" : return new Token(token, Type.NOT, tokenScanner.getLineNum());
            case "&" : return new Token(token, Type.AMPERSAND, tokenScanner.getLineNum());
            case "|" : return new Token(token, Type.PIPE, tokenScanner.getLineNum());
            case "^" : return new Token(token, Type.XOR, tokenScanner.getLineNum());
            case "~" : return new Token(token, Type.TILDA, tokenScanner.getLineNum());
            case "?" : return new Token(token, Type.QUESTION, tokenScanner.getLineNum());

            case "+=" : return new Token(token, Type.PLUS_ASSIGN, tokenScanner.getLineNum());
            case "-=" : return new Token(token, Type.MINUS_ASSIGN, tokenScanner.getLineNum());
            case "*=" : return new Token(token, Type.STAR_ASSIGN, tokenScanner.getLineNum());
            case "/=" : return new Token(token, Type.SLASH_ASSIGN, tokenScanner.getLineNum());
            case "%=" : return new Token(token, Type.MOD_ASSIGN, tokenScanner.getLineNum());
            case "**" : return new Token(token, Type.POW, tokenScanner.getLineNum());
            case "==" : return new Token(token, Type.EQUALS, tokenScanner.getLineNum());
            case "<=" : return new Token(token, Type.LESSER_EQUAL, tokenScanner.getLineNum());
            case ">=" : return new Token(token, Type.GREATER_EQUAL, tokenScanner.getLineNum());
            case "!=" : return new Token(token, Type.NOT_EQUAL, tokenScanner.getLineNum());
            case "&&" : return new Token(token, Type.AND, tokenScanner.getLineNum());
            case "||" : return new Token(token, Type.OR, tokenScanner.getLineNum());
            case "<<" : return new Token(token, Type.LEFT_SHIFT, tokenScanner.getLineNum());
            case ">>" : return new Token(token, Type.RIGHT_SHIFT, tokenScanner.getLineNum());

            case "int"     : return new Token(token, Type.INT, tokenScanner.getLineNum());
            case "decimal" : return new Token(token, Type.DECIMAL, tokenScanner.getLineNum());
            case "char"    : return new Token(token, Type.CHAR, tokenScanner.getLineNum());
            case "string"  : return new Token(token, Type.STRING, tokenScanner.getLineNum());
            case "boolean" : return new Token(token, Type.BOOLEAN, tokenScanner.getLineNum());
            case "array" : return new Token(token, Type.ARRAY, tokenScanner.getLineNum());
            case "void" : return new Token(token, Type.VOID, tokenScanner.getLineNum());

            case "print" : return new Token(token, Type.PRINT, tokenScanner.getLineNum());
            case "break" : return new Token(token, Type.BREAK, tokenScanner.getLineNum());
            case "class" : return new Token(token, Type.CLASS, tokenScanner.getLineNum());
            case "const" : return new Token(token, Type.CONST, tokenScanner.getLineNum());
            case "if" : return new Token(token, Type.IF, tokenScanner.getLineNum());
            case "else" : return new Token(token, Type.ELSE, tokenScanner.getLineNum());
            case "while" : return new Token(token, Type.WHILE, tokenScanner.getLineNum());
            case "for" : return new Token(token, Type.FOR, tokenScanner.getLineNum());
            case "null" : return new Token(token, Type.NULL, tokenScanner.getLineNum());
            case "return" : return new Token(token, Type.RETURN, tokenScanner.getLineNum());
            case "super" : return new Token(token, Type.SUPER, tokenScanner.getLineNum());
            case "this" : return new Token(token, Type.THIS, tokenScanner.getLineNum());
            case "false" : return new Token(token, Type.FALSE, tokenScanner.getLineNum());
            case "true" : return new Token(token, Type.TRUE, tokenScanner.getLineNum());
            case "len" : return new Token(token, Type.LEN, tokenScanner.getLineNum());

            case "" : return null;
        }

        //Literals and Identifiers
        if(isInteger(token)) return new Token(token, Type.INT_LITERAL, tokenScanner.getLineNum());
        else if(isDecimal(token)) return new Token(token, Type.DECIMAL_LITERAL, tokenScanner.getLineNum());
        else if(isCharacter(token)) return new Token(token, Type.CHAR_LITERAL, tokenScanner.getLineNum());
        else if(isString(token)) return new Token(token.substring(1,token.length()-1), Type.STRING_LITERAL, tokenScanner.getLineNum());
        else if(isIdentifier(token)) return new Token(token, Type.IDENTIFIER, tokenScanner.getLineNum());

        new Error("Unexpected Character").invoke();

        return null;
    }

    private boolean isInteger(String token) {
        if(token.length() > 1 && token.charAt(0) == '0') return false;

        for(char character : token.toCharArray())
            if(!Character.isDigit(character)) return false;
        return true;
    }

    private boolean isDecimal(String token) {
        if(token.charAt(0) == '.') return false;

        boolean containsSinglePoint = false;

        //TODO: Resolve repetition
        for(char character : token.toCharArray()) {
            if(!Character.isDigit(character) && character != '.') return false;
            if (character == '.') containsSinglePoint = !containsSinglePoint;
        }

        return containsSinglePoint;
    }

    private boolean isCharacter(String token) {
        //Character includes empty character
        //TODO: Implement escaped characters and better functionally with empty char
        return (token.length() >= 2 && token.length() <= 3)&& token.charAt(0) == '\'' && token.charAt(2) == '\'';
    }

    private boolean isString(String token) {
        //TODO: ADD escape character functionality
        return token.charAt(0) == '\"' && token.charAt(token.length()-1) == '\"';
    }

    private boolean isBool(String token) {
        return token.equals("true") || token.equals("false");
    }

    private boolean isIdentifier(String token) {
        for(char character : token.toCharArray())
            if(!isValidIdentifierChar(character)) return false;

        return true;
    }

    private boolean isValidIdentifierChar(char character) {
        return Character.isAlphabetic(character) || Character.isDigit(character) || character == '-' || character == '_';
    }

    private boolean isOperator(String token) {
        return false; //OPERATORS.contains(token);
    }

    private boolean isAssignment(String token) {
        return (token.length() == 1 && token.charAt(0) == '=') || isOpAssignment(token);
    }

    private boolean isOpAssignment(String token) {
        return false; //token.length() == 2 && OPERATORS.contains(token.charAt(0) + "") && token.charAt(1) == '=';
    }

    private boolean isKeyword(String token) {
        return false; //contains(KEYWORDS,token);
    }

    private boolean contains(String[] arr, String string) {
        for (String str : arr)
            if (str.equals(string)) return true;

        return false;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public static void error(Token token, String msg) {
        if(token.getType() == Type.EOF) {
            report(token.getLineNum(), " at end", msg);
        } else {
            report(token.getLineNum(), " at '" + token.getToken() + "'", msg);
        }
    }

    public static void report(int lineNum, String where, String msg) {
        System.err.println("[Line " + lineNum + "] Error" + where + ": " + msg);
    }
}