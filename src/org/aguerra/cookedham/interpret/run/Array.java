package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;

import java.util.ArrayList;

public class Array extends Variable {
    public Type arrayType;

    public Array(Object value, Type type, Type arrayType) {
        super(value, type);
        this.arrayType = arrayType;
    }
    public Array(Object value, Type type) {
        super(value, type);
        this.arrayType = null;
    }

    public Object getValue(int index) {
        if(((ArrayList<Object>)getValue()).size() <= index) CookedHam.runtimeError(new RuntimeError(new Token("", Type.NULL, 0), "Token outside array bounds."));
        return ((ArrayList<Object>)getValue()).get(index);
    }

    public void setValue(int index, Object value) {
        if(((ArrayList<Object>)getValue()).size() <= index) CookedHam.runtimeError(new RuntimeError(new Token("", Type.NULL, 0), "Token outside array bounds."));
        ((ArrayList<Object>)getValue()).set(index, value);
    }

    public int getLength() {
        return ((Object[])getValue()).length;
    }
}
