package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;
import org.aguerra.cookedham.interpret.parse.Expression;
import org.aguerra.cookedham.interpret.parse.Statement;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

    Interpreter() {
        globals.define("clock", new Callable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });
    }

    void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            CookedHam.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLogicalExpression(Expression.Logical expression) {
        Object left = evaluate(expression.left);

        if(expression.operator.getType() == Type.AND) {
            return isTruthy(left);
        }

        return isTruthy(left) || isTruthy(evaluate(expression.right));
    }

    private Object evaluate(Expression expression) {
        Object evaluated = expression.accept(this);

        return evaluated instanceof Variable ? ((Variable)evaluated).getValue() : evaluated;
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }

    public void executeBlock(List<Statement> statements, Environment environment) {
        Environment previousEnvironment = this.environment;
        try {
            this.environment = environment;

            for(Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previousEnvironment;
        }
    }
    @Override
    public Void visitBlockStatement(Statement.Block statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitLineExpressionStatement(Statement.LineExpression statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitFunctionStatement(Statement.Function statement) {
        Function function = new Function(statement);
        environment.define(statement.name.getToken(), function);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.Print statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVariableStatement(Statement.Variable statement) {
        Object value = null;
        if(statement.init != null) {
            value = evaluate(statement.init);
        }

        environment.checkType(statement.type, value, statement.name);
        environment.define(statement.name.getToken(), value);

        return null;
    }

    @Override
    public Void visitIfStatement(Statement.If statement) {
        if(isTruthy(evaluate(statement.condition))) {
            execute(statement.thenBranch);
        } else if(statement.elseBranch != null) {
            execute(statement.elseBranch);
        }

        return null;
    }

    @Override
    public Object visitAssignExpression(Expression.Assign expression) {
        Object value = evaluate(expression.value);

        environment.assign(expression.name, value);
        return value;
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.right);

        switch (expression.operator.getType()) {
            case NOT:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return -(double)right;
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitVariableExpression(Expression.Variable expression) {
        return environment.get(expression.name);
    }

    @Override
    public Void visitForStatement(Statement.For statement) {
        execute(statement.initializer);
        while (isTruthy(evaluate(statement.condition))) {
            execute(statement.body);
            evaluate(statement.increment);
        }
        return null;
    }

    @Override
    public Void visitWhileStatement(Statement.While statement) {
        while (isTruthy(evaluate(statement.condition))) {
            execute(statement.body);
        }
        return null;
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.getType()) {
            case MINUS:
                return handleSubtraction(left, right);
            case PLUS:
                if (isNumber(left) && isNumber(right)) {
                    return handleAddition(left, right);
                }

                else if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                else {
                    throw new RuntimeError(expression.operator,
                            "Operands must be two numbers or two strings.");
                }

            case MOD:
                return handleMod(left, right);
            case SLASH:
                return handleDivision(left, right);
            case STAR:
                return handleMultiplication(left, right);
            case RIGHT_ANGLE_BRACE:
                checkNumberOperands(expression.operator, left, right);
                return (int)left > (int)right;
            case GREATER_EQUAL:
                checkNumberOperands(expression.operator, left, right);
                return (int)left >= (int)right;
            case LEFT_ANGLE_BRACE:
                checkNumberOperands(expression.operator, left, right);
                return (int)left < (int)right;
            case LESSER_EQUAL:
                checkNumberOperands(expression.operator, left, right);
                return (int)left <= (int)right;
            case NOT_EQUAL: return !isEqual(left, right);
            case EQUALS: return isEqual(left, right);
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitCallExpression(Expression.Call expression) {
        Object callee = evaluate(expression.calle);

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof Callable)) {
            throw new RuntimeError(expression.paren,
                    "Can only call functions and classes.");
        }

        Callable function = (Callable)callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expression.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // nil is only equal to nil.
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "null";

        if(object instanceof Variable) return ((Variable) object).getValue()+"";

        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private void checkNumberOperands(Token operator,
                                     Object left, Object right) {
        if ((isNumber(left) || isVariable(left)) && (isNumber(right) || isVariable(right))) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isVariable(Object object) {
        return object instanceof Variable;
    }

    private boolean isDouble(Object object) {
        return object instanceof Double;
    }

    private boolean isInteger(Object object) {
        return object instanceof Integer;
    }

    private boolean isNumber(Object object) {
        return isInteger(object) || isDouble(object);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private Object handleSubtraction(Object left, Object right) {
        if(isDouble(left)|| isDouble(right)) return new Double(left + "") - new Double(right + "");
        return (int)left - (int)right;
    }

    private Object handleAddition(Object left, Object right) {
        if(isDouble(left)|| isDouble(right)) return new Double(left + "") + new Double(right + "");
        return (int)left + (int)right;
    }

    private Object handleMultiplication(Object left, Object right) {
        if(isDouble(left)|| isDouble(right)) return new Double(left + "") * new Double(right + "");
        return (int)left * (int)right;
    }

    private Object handleDivision(Object left, Object right) {
        if(isDouble(left)|| isDouble(right)) return new Double(left + "") / new Double(right + "");
        return (int)left / (int)right;
    }

    private Object handleMod(Object left, Object right) {
        if(isDouble(left)|| isDouble(right)) return new Double(left + "") % new Double(right + "");
        return (int)left % (int)right;
    }
}