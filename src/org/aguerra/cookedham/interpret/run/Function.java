package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.parse.Statement;

import java.util.List;

public class Function implements Callable {
    private final Statement.Function declaration;
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

        interpreter.executeBlock(declaration.body, environment);
        return null;
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