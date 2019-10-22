package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Token;

class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}