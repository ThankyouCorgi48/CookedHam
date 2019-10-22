package org.aguerra.cookedham.interpret.lex;

import org.aguerra.cookedham.interpret.error.Error;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TokenScanner {
    private Scanner fileInputStream;
    private String currLine;
    private int lineIndex, lineNumber, startToken, endToken;
    private char currChar;
    private Pattern stringPattern;

    public TokenScanner(String fileName) {
        try {
            //TODO: Fix pattern to start with space character
            stringPattern = Pattern.compile("[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\";*|\\S+");
            fileInputStream = new Scanner(new BufferedInputStream(new FileInputStream(fileName)));

            //currLine = fileInputStream.nextLine();
            lineNumber = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String nextToken() {
        //TODO: throw error if random characters after string, etc. "hello wor"ld
        //TODO: Fix lexing for single character lines

        return fileInputStream.next();
    }

    /*public String nextToken() {
        //TODO: throw error if random characters after string, etc. "hello wor"ld
        //TODO: Fix lexing for single character lines

        if(currLine.equals("")) {
            String toReturn = currLine;
            setNextLine();

            return toReturn;
        }

        if(currLine.length() == 1) {
            String toReturn = currLine;
            setNextLine();

            return toReturn;
        }
        currChar = currLine.charAt(lineIndex);

        if(isEndLine()) {
            setNextLine();
            return currLine.substring(startToken, endToken).trim();
        } else {
            //new Error("Missing semicolon").invoke();
        }

        startToken = endToken;

        do {
            if(currChar == '\"')  {
                handleString();
                break;
            }
            currChar = currLine.charAt(lineIndex);

            lineIndex++;
        } while (currChar != ' ' && !isEndLine());

        endToken = lineIndex;

        if(currChar == ' ') handleSpaces();

        return currLine.substring(startToken, endToken).trim();
    }*/

    public int getLineNum() {
        return lineNumber;
    }

    public boolean hasNext() {
        return fileInputStream.hasNext(); // || !currLine.equals("");
    }

    private boolean isEndLine() {
        return currLine.length() <= lineIndex + 1;
    }

    private void setNextLine() {
        lineIndex++;
        lineIndex = 0;
        startToken = 0;
        endToken = 0;

        if(fileInputStream.hasNextLine()) {
            currLine = fileInputStream.nextLine();
        } else {
            currLine = "";
        }
    }

    private void handleString() {
        //TODO: Add compatibility for escaped double and and single quote
        do {
            lineIndex++;
            currChar = currLine.charAt(lineIndex);
        } while(currChar != '"');
        endToken = lineIndex;
    }

    private void handleSpaces() {
        while(currLine.charAt(lineIndex) == ' ') {
            lineIndex++;
            currChar = currLine.charAt(lineIndex);
        }
        endToken = lineIndex - 1;
    }

    /*private boolean isSplitable(String tkn) {
    }*/
}