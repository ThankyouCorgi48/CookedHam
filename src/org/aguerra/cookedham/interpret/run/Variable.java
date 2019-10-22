package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Type;

public class Variable {
    private Object value;
    private Type type;

    public Variable(Object value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
}