package org.aguerra.cookedham.interpret.lex;

import java.util.Objects;

public class Token {
    private String token;
    private Type type;
    private Object literal;
    private int lineNum;

    public Token(String token, Type type, int lineNum) {
        this.token = token;
        this.type = type;
        this.lineNum = lineNum;

        this.literal = generateLiteral();
    }

    public String getToken() {
        return token;
    }

    public Type getType() {
        return type;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLineNum() {
        return lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return Objects.equals(getToken(), token1.getToken()) &&
                getType() == token1.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getType());
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", type=" + type +
                '}';
    }

    private Object generateLiteral() {
        if(type == Type.INT_LITERAL) return Integer.parseInt(token);
        else if(type == Type.DECIMAL_LITERAL) return Double.parseDouble(token);
        else if(type == Type.TRUE) return true;
        else if(type == Type.FALSE) return false;

        return token;
    }
}