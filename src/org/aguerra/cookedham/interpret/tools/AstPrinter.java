package org.aguerra.cookedham.interpret.tools;

import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;
import org.aguerra.cookedham.interpret.parse.Expression;
import org.aguerra.cookedham.interpret.parse.Expression.Literal;

public class AstPrinter implements Expression.Visitor<String> {
    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.getToken(), expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("grouping", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if(expression.value == null) return "null";
        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return parenthesize(expression.operator.getToken(), expression.right);
    }

    @Override
    public String visitAssignExpression(Expression.Assign expression) {
        //TODO: FINISH print
        return null;
    }

    @Override
    public String visitBreakExpression(Expression.Break expression) {
        return null;
    }

    @Override
    public String visitArrayAccessExpression(Expression.ArrayAccess expression) {
        return null;
    }

    @Override
    public String visitArrayBlockExpression(Expression.ArrayBlock expression) {
        return null;
    }

    @Override
    public String visitLogicalExpression(Expression.Logical expression) {
        //TODO: finish logic print
        return null;
    }

    @Override
    public String visitTernaryExpression(Expression.Ternary expression) {
        return null;
    }

    @Override
    public String visitCallExpression(Expression.Call expression) {
        return null;
    }

    @Override
    public String visitLenExpression(Expression.Len expression) {
        return null;
    }

    @Override
    public String visitVariableExpression(Expression.Variable expression) {
        return parenthesize(expression.name.getToken(), expression);
    }

    public String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for(Expression expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    public static void main(String[] args) {
        Expression expression = new Expression.Binary(
                new Expression.Unary(
                        new Token("-", Type.MINUS, 1),
                        new Expression.Literal(123)
                ),
                new Token("*", Type.STAR, 1),
                new Expression.Grouping(new Expression.Literal(45.67))
        );

        System.out.println(new AstPrinter().print(expression));
    }
}