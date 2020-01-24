package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class Environment {
    private Environment enclosingScope;
    private final Map<String, Array> values = new HashMap<>();

    public Environment() {
        enclosingScope = null;
    }

    public Environment(Environment enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public Object get(Token name) {
        if(values.containsKey(name.getToken())) {
            return values.get(name.getToken());
        }

        if(enclosingScope != null)  return enclosingScope.get(name);
        throw new RuntimeError(name, "Undefined variable '" + name.getToken() + "'.");
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.getToken())) {
            checkType(getTypeOfVariable(name.getToken()), value, name);
            values.put(name.getToken(), new Array(value, name.getType()));
            return;
        }

        if(enclosingScope != null) {
            enclosingScope.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.getToken() + "'.");
    }

    public void define(String name, Object value) {
        //TODO: define with proper type
        values.put(name, new Array(value, getType(value)));
    }

    private Type getTypeOfVariable(String key) {
        return getType(values.get(key).getValue());
    }

    private Type getType(Object value) {

        if(value instanceof Integer) return Type.INT;
        else if(value instanceof Double) return Type.DECIMAL;
        else if(value instanceof Boolean) return Type.BOOLEAN;
        else if(value instanceof Character) return Type.CHAR;
        else if(value instanceof String) return Type.STRING;
        else if(value instanceof ArrayList) return Type.ARRAY;

        //TODO: Finish error
        //throw new RuntimeError(name, "Invalid type: ");

        return null;
    }

    private boolean isCharater(String name) {
        Pattern charater = Pattern.compile("'.'");
        return name.equals("true") || name.equals("false");
    }

    private boolean isBoolean(String name) {
        return name.equals("true") || name.equals("false");
    }

    //TODO: utilize method
    public void checkType(Type type, Object value, Token name) {
        Type typeReceived = getType(value);
        if(type == typeReceived) return;

        throw new RuntimeError(name, "Type mismatch: expected type " + type + ", received type " + typeReceived);
    }
}