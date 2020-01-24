package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Type;
import org.aguerra.cookedham.interpret.parse.Statement;

import java.math.*;

import java.util.ArrayList;
import java.util.List;

public class Function implements Callable {
    protected final Statement.Function declaration;
    Function(Statement.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).getToken(),
                    arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (checkType(returnValue.value)) {
                return returnValue.value;
            }
            CookedHam.runtimeError(new RuntimeError(declaration.name, "Expected return type " + declaration.returnType + "."));
        }

        return null;
    }

    private boolean checkType(Object value) {
        return (value instanceof Integer && declaration.returnType == Type.INT) ||
           (value instanceof Double && declaration.returnType == Type.DECIMAL) ||
           (value instanceof String && declaration.returnType == Type.STRING) ||
           (value instanceof Character && declaration.returnType == Type.CHAR) ||
                (value instanceof Boolean && declaration.returnType == Type.BOOLEAN) ||
                (value instanceof ArrayList && declaration.returnType == Type.ARRAY);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.getToken() + ">";
    }
}
