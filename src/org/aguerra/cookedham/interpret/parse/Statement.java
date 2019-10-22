package org.aguerra.cookedham.interpret.parse;

import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;

import java.util.List;

public abstract class Statement {
    public interface Visitor<R> {
        public R visitBlockStatement(Block statement);
        public R visitLineExpressionStatement(LineExpression statement);
        public R visitIfStatement(If statement);
        public R visitFunctionStatement(Function statement);
        public R visitPrintStatement(Print statement);
        public R visitVariableStatement(Variable statement);
        public R visitForStatement(For statement);
        public R visitWhileStatement(While statement);
    }

    public abstract <R> R accept(Visitor<R> visitor);
    public static class Block extends Statement {
        public Block(List<Statement> statements) {
            this.statements = statements;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }

        public final List<Statement> statements;
    }
    public static class LineExpression extends Statement {
        public LineExpression(Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLineExpressionStatement(this);
        }

        public final Expression expression;
    }
    public static class If extends Statement {
        public If(Expression condition, Statement thenBranch, Statement elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }

        public final Expression condition;
        public final Statement thenBranch;
        public final Statement elseBranch;
    }
    public static class Function extends Statement {
        public Function(Token name, List<Token> params, List<Statement> body, Type returnType) {
            this.name = name;
            this.params = params;
            this.body = body;
            this.returnType = returnType;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStatement(this);
        }

        public final Token name;
        public final List<Token> params;
        public final List<Statement> body;
        public final Type returnType;
    }
    public static class Print extends Statement {
        public Print(Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStatement(this);
        }

        public final Expression expression;
    }
    public static class Variable extends Statement {
        public Variable(Token name, Type type, Expression init) {
            this.name = name;
            this.type = type;
            this.init = init;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableStatement(this);
        }

        public final Token name;
        public final Type type;
        public final Expression init;
    }
    public static class For extends Statement {
        public For(Statement initializer, Expression condition, Expression increment, Statement body) {
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStatement(this);
        }

        public final Statement initializer;
        public final Expression condition;
        public final Expression increment;
        public final Statement body;
    }
    public static class While extends Statement {
        public While(Expression condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }

        public final Expression condition;
        public final Statement body;
    }
}
