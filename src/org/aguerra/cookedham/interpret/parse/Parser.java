package org.aguerra.cookedham.interpret.parse;

import org.aguerra.cookedham.interpret.lex.Lexer;
import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private List<Token> tokens;
    private int currToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Expression expression() {
        return assignment();
    }

    private Statement declaration() {
        try {
            if (match(Type.INT, Type.DECIMAL, Type.STRING, Type.CHAR, Type.BOOLEAN, Type.ARRAY)) {
                if(peek(1).getType() != Type.LEFT_PAREN) {
                    return varDeclaration(getVariableType());
                }
                return function("function", previous().getType());
            }

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement statement() {
        if (match(Type.INT, Type.DECIMAL, Type.STRING, Type.CHAR, Type.BOOLEAN, Type.VOID)) return function("function", previous().getType());
        if (match(Type.FOR)) return forStatement();
        if(match(Type.IF)) return ifStatement();
        if (match(Type.PRINT)) return printStatement();
        if (match(Type.RETURN)) return returnStatement();
        if (match(Type.WHILE)) return whileStatement();
        if(match(Type.LEFT_BRACE)) return new Statement.Block(block());
        return expressionStatement();
    }

    private Statement forStatement() {
        Expression array = null;
        Statement initializer = null;
        Expression condition = null;
        Expression increment = null;

        consume("Expect '(' after 'for'.", Type.LEFT_PAREN);

        if (match(Type.SEMICOLON)) {
            initializer = null;
        } else if (match(Type.INT, Type.DECIMAL, Type.CHAR, Type.STRING, Type.BOOLEAN)) {
            initializer = varDeclaration(previous().getType());
        } else {
            initializer = expressionStatement();
        }

        if(match(Type.COLON)) {
            array = expression();
        } else {
            if (!check(Type.SEMICOLON)) {
                condition = expression();
            }
            consume("Expect ';' after loop condition.", Type.SEMICOLON);

            if (!check(Type.RIGHT_PAREN)) {
                increment = expression();
            }
            if (condition == null) condition = new Expression.Literal(true);
        }


        consume("Expect ')' after for clauses.", Type.RIGHT_PAREN);

        //consume("Expect '{' after ')'.", Type.LEFT_BRACE);
        Statement body = statement();

        return array == null ? new Statement.For(initializer, condition, increment, body) : new Statement.ForEach(initializer, array, body);
    }

    private Statement ifStatement() {
        consume("Expected '(' after 'if'.", Type.LEFT_PAREN);
        Expression condition = expression();
        consume("Expected ')' after if condition.", Type.RIGHT_PAREN);

        Statement thenBranch = statement();
        Statement elseBranch = null;

        if(match(Type.ELSE)) {
            elseBranch = statement();
        }

        return new Statement.If(condition, thenBranch, elseBranch);
    }

    private Statement printStatement() {
        Expression value = expression();
        consume("Expect ';' after value.", Type.SEMICOLON);
        return new Statement.Print(value);
    }

    private Statement returnStatement() {
        Token keyword = previous();
        Expression value = null;
        if (!check(Type.SEMICOLON)) {
            value = expression();
        }

        consume("Expect ';' after return value.", Type.SEMICOLON);
        return new Statement.Return(keyword, value);
    }

    private Statement varDeclaration(Type expectedType) {
        Type arrayType = null;

        if(expectedType == Type.ARRAY) {
            consume("Expect '<' after array type in array declaration.", Type.LEFT_ANGLE_BRACE);
            arrayType = getVariableType(consume("Expect type after array declaration.", Type.INT, Type.DECIMAL, Type.CHAR, Type.STRING, Type.ARRAY));
            consume("Expected '>' after type in array declaration.", Type.RIGHT_ANGLE_BRACE);
        }

        Token name = consume("Expect variable name.", Type.IDENTIFIER);

        Expression initializer = null;
        if (match(Type.ASSIGN)) {
            initializer = expression();
        }

        if(peek().getType() != Type.COLON) {
            consume("Expect ';' after variable declaration.", Type.SEMICOLON);
        }

        return new Statement.Variable(name, expectedType, arrayType, initializer);
    }

    private Statement function(String kind, Type expectedType) {
        //if(previous().getType() != Type.IDENTIFIER) error(previous(), "Expected identifier after type.");
        Token name = consume("Expect " + kind + " name.", Type.IDENTIFIER);

        consume("Expect '(' after " + kind + " name.", Type.LEFT_PAREN);
        List<Type> parametersTypes = new ArrayList<>();
        List<Token> parameters = new ArrayList<>();
        if (!check(Type.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Cannot have more than 255 parameters.");
                }

                parametersTypes.add(getVariableType(consume("Expect type.", Type.INT, Type.DECIMAL, Type.CHAR, Type.STRING, Type.ARRAY)));
                parameters.add(consume("Expect parameter name.", Type.IDENTIFIER));
            } while (match(Type.COMMA));
        }
        consume("Expect ')' after parameters.", Type.RIGHT_PAREN);

        consume("Expect '{' before " + kind + " body.", Type.LEFT_BRACE);
        List<Statement> body = block();
        return new Statement.Function(name, parameters, parametersTypes, body, expectedType);
    }

    private Statement whileStatement() {
        consume("Expect '(' after 'while'.", Type.LEFT_PAREN);
        Expression condition = expression();
        consume("Expect ')' after condition.", Type.RIGHT_PAREN);
        Statement body = statement();

        return new Statement.While(condition, body);
    }

    private Statement expressionStatement() {
        Expression expression = expression();
        consume("Expect ';' after expression.", Type.SEMICOLON);
        return new Statement.LineExpression(expression);
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while(!check(Type.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume("Expected '}' after block.", Type.RIGHT_BRACE);
        return statements;
    }

    private Expression assignment() {
        Expression expression = ternary();
        Expression arrayIndex = null;

        if (expression instanceof Expression.ArrayAccess) {
            arrayIndex = ((Expression.ArrayAccess)expression).index;
        }
        Token equals = null;

        if (match(Type.ASSIGN)) {
            equals = previous();

            if (expression instanceof Expression.Variable || expression instanceof Expression.ArrayAccess) {
                Token name = expression instanceof Expression.Variable ?((Expression.Variable)expression).name : ((Expression.ArrayAccess)expression).identifier;

                Expression value = assignment();
                return new Expression.Assign(name, value, arrayIndex);
            }

            error(equals, "Invalid assignment target.");
        } /*else if (deadvance() != null) {
            //TODO: Finish OP-Assignments
        }*/

        return expression;
    }

    private Expression ternary() {
        Expression expression = or();

        while(match(Type.QUESTION)) {
            Expression truthExpression = expression();
            consume("Expected ':' after ternary operation.", Type.COLON);
            Expression falseExpression = expression();
            expression = new Expression.Ternary(expression, truthExpression, falseExpression);
        }

        return expression;
    }

    private Expression or() {
        Expression expression = and();

        while(match(Type.OR)) {
            Token operator = previous();
            Expression right = and();
            expression = new Expression.Logical(expression, operator, right);
        }

        return expression;
    }

    private Expression and() {
        Expression expression = bitwiseCompare();

        while(match(Type.AND)) {
            Token operator = previous();
            Expression right = bitwiseCompare();
            expression = new Expression.Logical(expression, operator, right);
        }

        return expression;
    }

    private Expression bitwiseCompare() {
        Expression expression = equality();

        while(match(Type.PIPE, Type.XOR, Type.AMPERSAND)) {
            Token operator = previous();
            Expression right = equality();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression equality() {
        Expression expression = comparison();

        while(match(Type.NOT_EQUAL, Type.EQUALS)) {
            Token op = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression comparison() {
        Expression expression = shift();

        while(match(Type.LEFT_ANGLE_BRACE, Type.GREATER_EQUAL, Type.RIGHT_ANGLE_BRACE, Type.LESSER_EQUAL)) {
            Token op = previous();
            Expression right = shift();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression shift() {
        Expression expression = addition();

        while(match(Type.LEFT_SHIFT, Type.RIGHT_SHIFT)) {
            Token op = previous();
            Expression right = addition();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression addition() {
        Expression expression = multiplication();

        while(match(Type.PLUS, Type.MINUS)) {
            Token op = previous();
            Expression right = multiplication();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression multiplication() {
        Expression expression = pow();

        while(match(Type.STAR, Type.SLASH, Type.MOD)) {
            Token op = previous();
            Expression right = pow();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression pow() {
        Expression expression = unary();

        while(match(Type.POW)) {
            Token op = previous();
            Expression right = unary();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression unary() {
        if(match(Type.TILDA, Type.NOT, Type.MINUS)) {
            Token op = previous();
            Expression right = unary();
            return new Expression.Unary(op, right);
        }

        return call();
    }

    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(Type.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Cannot have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(Type.COMMA));
        }

        Token paren = consume("Expect ')' after arguments.", Type.RIGHT_PAREN);

        return new Expression.Call(callee, paren, arguments);
    }

    private Expression call() {
        Expression expression = len();

        while (true) {
            if (match(Type.LEFT_PAREN)) {
                expression = finishCall(expression);
            } else {
                break;
            }
        }

        return expression;
    }

    private Expression len() {
        if(match(Type.LEN)) {
            Token keyword = previous();
            Expression expression = expression();

            return new Expression.Len(expression, keyword);
        }

        return primary();
    }

    private Expression primary() {
        if(match(Type.FALSE)) return new Expression.Literal(false);
        if(match(Type.TRUE)) return new Expression.Literal(true);
        if(match(Type.NULL)) return new Expression.Literal(null);

        if(match(Type.INT_LITERAL, Type.DECIMAL_LITERAL, Type.STRING_LITERAL, Type.CHAR_LITERAL)) {
            return new Expression.Literal(previous().getLiteral());
        }

        if (match(Type.IDENTIFIER)) {
            if(peek().getType() != Type.LEFT_BRACKET) {
                return new Expression.Variable(previous());
            }

            Token identifier = previous();
            advance();
            Expression index = expression();

            if(!match(Type.RIGHT_BRACKET)) throw error(previous(), "Expected ']' in array access.");
            return new Expression.ArrayAccess(identifier, index);
        }

        if(match(Type.LEFT_PAREN)) {
            Expression expression = expression();
            consume("Expected \")\" after expression", Type.RIGHT_PAREN);
            return new Expression.Grouping(expression);
        }

        if(match((Type.LEFT_BRACE))) {
            ArrayList<Expression> elements = new ArrayList<>();
            while(peek().getType() != Type.RIGHT_BRACE) {
                elements.add(expression());
                if(tokens.get(currToken).getType() == Type.COMMA) advance();
            }
            advance();
            return new Expression.ArrayBlock(elements);
        }

        throw error(peek(), "Expect expression.");
    }

    private Type getVariableType() {
        return getVariableType(previous());
    }

    private Type getVariableType(Token token) {
        switch (token.getToken()) {
            case "int"     : return Type.INT;
            case "decimal" : return Type.DECIMAL;
            case "char"    : return Type.CHAR;
            case "string"  : return Type.STRING;
            case "boolean" : return Type.BOOLEAN;
            case "array" : return Type.ARRAY;
            default        : return Type.NULL; //Replace with class object

        }
    }

    private Token consume(String msg, Type... types) {
        for(Type type : types) {
            if(check(type)) return advance();
        }

        throw error(peek(), msg);
    }

    private ParseError error(Token token, String msg) {
        Lexer.error(token, msg);
        return new ParseError();
    }

    private boolean match(Type... types) {
        for(Type type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(Type type) {
        if(isAtEnd()) return false;
        return peek().getType() == type;
    }

    /*private Token deadvance() {
        if(!isAtEnd()) currToken--;
        return peek();
    }*/

    private Token advance() {
        if(!isAtEnd()) currToken++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == Type.EOF;
    }

    private Token peek() {
        return tokens.get(currToken);
    }

    private Token peek(int numAhead) {
        return tokens.get(currToken + numAhead);
    }

    private Token previous() {
        return tokens.get(currToken - 1);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == Type.SEMICOLON) return;

            switch (peek().getType()) {
                case CLASS:
                case CONST:
                case THIS:
                case FOR:
                case IF:
                case WHILE:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}