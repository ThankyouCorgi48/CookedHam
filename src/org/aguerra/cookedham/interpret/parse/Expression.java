package org.aguerra.cookedham.interpret.parse;

import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;
import java.util.List;

public abstract class Expression {
    public interface Visitor<R> {
        public R visitAssignExpression(Assign expression);
        public R visitArrayAccessExpression(ArrayAccess expression);
        public R visitArrayBlockExpression(ArrayBlock expression);
        public R visitBinaryExpression(Binary expression);
        public R visitBreakExpression(Break expression);
        public R visitCallExpression(Call expression);
        public R visitGroupingExpression(Grouping expression);
        public R visitLenExpression(Len expression);
        public R visitLiteralExpression(Literal expression);
        public R visitLogicalExpression(Logical expression);
        public R visitTernaryExpression(Ternary expression);
        public R visitUnaryExpression(Unary expression);
        public R visitVariableExpression(Variable expression);
    }

    public abstract <R> R accept(Visitor<R> visitor);
    public static class Assign extends Expression {
        public Assign(Token name, Expression value, Expression arrayIndex) {
            this.name = name;
            this.value = value;
            this.arrayIndex = arrayIndex;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpression(this);
        }

        public final Token name;
        public final Expression value;
        public final Expression arrayIndex;
    }
    public static class ArrayAccess extends Expression {
        public ArrayAccess(Token identifier, Expression index) {
            this.identifier = identifier;
            this.index = index;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitArrayAccessExpression(this);
        }

        public final Token identifier;
        public final Expression index;
    }
    public static class ArrayBlock extends Expression {
        public ArrayBlock(List<Expression> elements) {
            this.elements = elements;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitArrayBlockExpression(this);
        }

        public final List<Expression> elements;
    }
    public static class Binary extends Expression {
        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }

        public final Expression left;
        public final Token operator;
        public final Expression right;
    }
    public static class Break extends Expression {
        public Break(Statement loop) {
            this.loop = loop;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakExpression(this);
        }

        public final Statement loop;
    }
    public static class Call extends Expression {
        public Call(Expression calle, Token paren, List<Expression> arguments) {
            this.calle = calle;
            this.paren = paren;
            this.arguments = arguments;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpression(this);
        }

        public final Expression calle;
        public final Token paren;
        public final List<Expression> arguments;
    }
    public static class Grouping extends Expression {
        public Grouping(Expression expression) {
            this.expression = expression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }

        public final Expression expression;
    }
    public static class Len extends Expression {
        public Len(Expression expression, Token keyword) {
            this.expression = expression;
            this.keyword = keyword;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLenExpression(this);
        }

        public final Expression expression;
        public final Token keyword;
    }
    public static class Literal extends Expression {
        public Literal(Object value) {
            this.value = value;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }

        public final Object value;
    }
    public static class Logical extends Expression {
        public Logical(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpression(this);
        }

        public final Expression left;
        public final Token operator;
        public final Expression right;
    }
    public static class Ternary extends Expression {
        public Ternary(Expression condition, Expression truthExpression, Expression falseExpression) {
            this.condition = condition;
            this.truthExpression = truthExpression;
            this.falseExpression = falseExpression;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitTernaryExpression(this);
        }

        public final Expression condition;
        public final Expression truthExpression;
        public final Expression falseExpression;
    }
    public static class Unary extends Expression {
        public Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }

        public final Token operator;
        public final Expression right;
    }
    public static class Variable extends Expression {
        public Variable(Token name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpression(this);
        }

        public final Token name;
    }
}
