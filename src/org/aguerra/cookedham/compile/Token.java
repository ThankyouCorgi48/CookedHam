package org.aguerra.cookedham.compile;

import java.util.Objects;

public class Token {
    private String token;
    private Type type;

    public Token(String token, Type type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
}
