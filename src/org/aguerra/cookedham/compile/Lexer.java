package org.aguerra.cookedham.compile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<Token> tokens;

    private final String OPERATORS = "+-*/%^";
    private final String[] KEYWORDS = {"var"};
    public Lexer(File file) {
        //TODO: Implement BufferedInputStream

        tokens = new ArrayList<>();
        try {
            Scanner inputScanner = new Scanner(file);
            String[] lineTokens;

            while (inputScanner.hasNextLine()) {
                lineTokens = splitLine(inputScanner.nextLine());
                createTokens(lineTokens);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] splitLine(String line) {
        //TODO: Clean code

        int cnt = 0;
        Pattern ptrn = Pattern.compile("\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"|\\S+");
        Matcher matcher = ptrn.matcher(line);
        while (matcher.find()) {
            cnt++;
        }

        String[] result = new String[cnt];
        matcher.reset();
        int idx = 0;
        while (matcher.find()) {
            result[idx] = matcher.group(0);
            idx++;
        }

        return result;
    }

    private void createTokens(String[] tokenList) {
        for(String token: tokenList) {
            tokens.add(createToken(token));
        }
    }

    private Token createToken(String token) {
        if(isInteger(token)) return new Token(token, Type.INTEGER_LITERAL);
        if(isDecimal(token)) return new Token(token, Type.DECIMAL_LITERAL);
        if(isBool(token)) return new Token(token, Type.BOOL_LITERAL);
        if(isCharacter(token)) return new Token(token, Type.CHARACTER_LITERAL);
        if(isString(token)) return new Token(token, Type.STRING_LITERAL);
        if(isKeyword(token)) return new Token(token, Type.KEYWORD);
        if(isIdentifier(token)) return new Token(token, Type.IDENTIFIER);
        if(isOperator(token)) return new Token(token, Type.OPERATOR);
        if(isAssignment(token)) return new Token(token, Type.ASSIGNMENT);

        return new Token(token, Type.UNKNOWN);
    }

    private boolean isInteger(String token) {
        if(token.charAt(0) == '0') return false;

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
        return Character.isAlphabetic(character) || character == '_';
    }

    private boolean isOperator(String token) {
        return OPERATORS.contains(token);
    }

    private boolean isAssignment(String token) {
        return (token.length() == 1 && token.charAt(0) == '=') || isOpAssignment(token);
    }

    private boolean isOpAssignment(String token) {
        return token.length() == 2 && OPERATORS.contains(token.charAt(1) + "");
    }

    private boolean isKeyword(String token) {
        return contains(KEYWORDS,token);
    }

    private boolean contains(String[] arr, String string) {
        for (String str : arr)
            if (str.equals(string)) return true;

        return false;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
