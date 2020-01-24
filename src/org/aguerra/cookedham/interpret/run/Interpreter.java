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
        globals.define("displayln", new Function(null) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                System.out.println(arguments.get(0));
                return null;

            }

            @Override
            public String toString() { return "<native fn>"; }
        });
        globals.define("display", new Function(null) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                System.out.print(arguments.get(0));
                return null;

            }

            @Override
            public String toString() { return "<native fn>"; }
        });
        globals.define("size", new Function(null) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return ((ArrayList)arguments.get(0)).size();

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
    public Object visitLiteralExpression(Expression.Literal expression) { return expression.value; }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLogicalExpression(Expression.Logical expression) {
        Object left = evaluate(expression.left);

        if(expression.operator.getType() == Type.AND) {
            return isTruthy(left) && isTruthy(evaluate(expression.right));
        }

        return isTruthy(left) || isTruthy(evaluate(expression.right));
    }

    @Override
    public Object visitTernaryExpression(Expression.Ternary expression) {
        Object condition = evaluate(expression.condition);

        if((boolean)condition) {
            return evaluate(expression.truthExpression);
        }
        return evaluate(expression.falseExpression);
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
    public Void visitReturnStatement(Statement.Return statement) {
        Object value = null;
        if (statement.value != null) value = evaluate(statement.value);

        throw new Return(value);
    }

    @Override
    public Void visitVariableStatement(Statement.Variable statement) {
        Object value = null;
        if(statement.init != null) {
            value = evaluate(statement.init);
        } else {
            value = generateDefaultValue(statement.type, statement.arrayType);
        }

        //TODO: Check to see if all values match type of array

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

        if(expression.arrayIndex != null) {
            int index = compressToInt(evaluate(expression.arrayIndex));
            ((Array)environment.get(expression.name)).setValue(index, value);
        }
        else {
            environment.assign(expression.name, value);
        }

        return value;
    }

    @Override
    public Object visitArrayAccessExpression(Expression.ArrayAccess expression) {
        int index = (int)evaluate(expression.index);
        Object value = ((Array)environment.get(expression.identifier)).getValue(index);

        return value;
    }

    @Override
    public Object visitArrayBlockExpression(Expression.ArrayBlock expression) {
        ArrayList<Object> elements = new ArrayList<>();
        for(Expression express : expression.elements) {
            elements.add(evaluate(express));
        }

        return elements;
    }

    @Override
    public Object visitBreakExpression(Expression.Break expression) {
        return null;
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.right);

        switch (expression.operator.getType()) {
            case NOT:
                return !isTruthy(right);
            case TILDA:
                return ~(int)right;
            case MINUS:
                checkNumberOperand(expression.operator, right);

                Object value = -(right instanceof Integer ? (Integer) right : (Double)right);
                if(right instanceof Integer) {
                    return ((Double)value).intValue();
                } else {
                    return (Double)value;
                }
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
    public Void visitForEachStatement(Statement.ForEach statement) {
        execute(statement.definition);
        int index = 0;
        ArrayList<Object> array = (ArrayList<Object>)evaluate(statement.array);
        while(index < array.size()) {
            environment.assign(((Statement.Variable)statement.definition).name, array.get(index));
            execute(statement.body);
            index++;
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
                if (isNumber(left)&& isNumber(right)) {
                    return handleAddition(left, right);
                }

                else if (left instanceof String) {
                    return (String)left + right;
                }

                else if (left instanceof ArrayList && right instanceof ArrayList) {
                    ((ArrayList) left).addAll((ArrayList)right);
                    return left;
                }

                else {
                    throw new RuntimeError(expression.operator,
                            "Operands must be two numbers, two strings or two arrays.");
                }

            case MOD:
                return handleMod(left, right);
            case SLASH:
                return handleDivision(left, right);
            case STAR:
                return handleMultiplication(left, right);
            case POW:
                return handlePow(left, right);
            case AMPERSAND:
                return handleBitwiseAnd(left, right);
            case PIPE:
                return handleBitwiseOr(left, right);
            case XOR:
                return handleBitwiseXor(left, right);
            case LEFT_SHIFT:
                return handleBitwiseLeft(left, right);
            case RIGHT_SHIFT:
                return handleBitwiseRight(left, right);
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

    @Override
    public Object visitLenExpression(Expression.Len expression) {
        Object value = evaluate(expression.expression);

        if(!(value instanceof ArrayList))  {
            throw new RuntimeError(expression.keyword, "Expected array for len expression");
        }

        return ((ArrayList)value).size();
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

    private Object generateDefaultValue(Type type, Type arrayType) {
        switch (type) {
            case INT     : return 0;
            case DECIMAL : return 0.0d;
            case CHAR    : return '\0';
            case STRING  : return "";
            case BOOLEAN : return false;
            case ARRAY   : return new ArrayList<>();
        }
        return null;
    }

    public int compressToInt(Object value) {
        if(value instanceof Integer || value instanceof Double || value instanceof Float) return (int) value;
        //TODO: return current token
        throw new RuntimeError(null, "Expected integer as index.");
    }

    public Class getClassType(Type arrayType) {
        switch (arrayType) {
            case INT     : return Integer.class;
            case DECIMAL : return Double.class;
            case CHAR    : return Character.class;
            case STRING  : return String.class;
            case BOOLEAN : return Boolean.class;
            case ARRAY   : return ArrayList.class;
        }
        return null;
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
        if (operand instanceof Integer || operand instanceof Double) return;
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

    private Object handlePow(Object left, Object right) {
        if(isDouble(left)|| isDouble(right)) return Math.pow(new Double(left + ""), new Double(right + ""));
        return Math.pow((int)left, (int)right);
    }

    //TODO: fix compatibility with doubles
    private Object handleBitwiseAnd(Object left, Object right) {
        return (int)left & (int)right;
    }

    private Object handleBitwiseOr(Object left, Object right) {
        return (int)left | (int)right;
    }

    private Object handleBitwiseXor(Object left, Object right) {
        return (int)left ^ (int)right;
    }

    private Object handleBitwiseLeft(Object left, Object right) {
        return (int)left << (int)right;
    }

    private Object handleBitwiseRight(Object left, Object right) {
        return (int)left >> (int)right;
    }
}