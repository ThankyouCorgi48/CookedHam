package org.aguerra.cookedham.interpret.lex;

public class TokenNode {
    private Token token;
    private TokenNode left;
    private TokenNode right;

    public TokenNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public TokenNode getLeft() {
        return left;
    }

    public void setLeft(TokenNode left) {
        this.left = left;
    }

    public TokenNode getRight() {
        return right;
    }

    public void setRight(TokenNode right) {
        this.right = right;
    }
}