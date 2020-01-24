package org.aguerra.cookedham.interpret.lex;

import org.aguerra.cookedham.interpret.run.CookedHam;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token("", Type.EOF, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(Type.LEFT_PAREN); break;
            case ')': addToken(Type.RIGHT_PAREN); break;
            case '{': addToken(Type.LEFT_BRACE); break;
            case '}': addToken(Type.RIGHT_BRACE); break;
            case ',': addToken(Type.COMMA); break;
            case '.': addToken(Type.DOT); break;
            case '-': addToken(Type.MINUS); break;
            case '+': addToken(Type.PLUS); break;
            case ';': addToken(Type.SEMICOLON); break;
            case '*':addToken(Type.STAR); break;
            case '!': addToken(match('=') ? Type.NOT_EQUAL : Type.NOT); break;
            case '=': addToken(match('=') ? Type.EQUALS : Type.ASSIGN); break;
            case '<': addToken(match('=') ? Type.LESSER_EQUAL : Type.LEFT_ANGLE_BRACE); break;
            case '>': addToken(match('=') ? Type.GREATER_EQUAL : Type.RIGHT_ANGLE_BRACE); break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(Type.SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    //number();
                } else {
                    CookedHam.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        // Unterminated string.
        if (isAtEnd()) {
            CookedHam.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(Type.STRING_LITERAL);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(Type type) {
        String text = source.substring(start, current);
        tokens.add(new Token(text, type, line));
    }
}