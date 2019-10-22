package org.aguerra.cookedham.interpret.error;

public class Error {
    private String msg;

    public Error(String msg) {
        this.msg = msg;
    }

    public void invoke() {
        System.err.println(msg);
    }
}