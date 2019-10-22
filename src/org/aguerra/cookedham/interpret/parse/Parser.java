package org.aguerra.cookedham.interpret.parse;

import org.aguerra.cookedham.interpret.lex.Lexer;
import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;

import java.util.ArrayList;
import java.util.Arrays;
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
            //statements.add(statement());
        }

        return statements;
    }

    private Expression expression() {
        return assignment();
    }

    private Statement declaration() {
        try {
            if (match(Type.INT, Type.DECIMAL, Type.STRING, Type.CHAR, Type.BOOLEAN) && peek(1).getType() != Type.LEFT_PAREN) return varDeclaration(Type.NULL);

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement statement() {
        if (match(Type.INT, Type.DECIMAL, Type.STRING, Type.CHAR, Type.BOOLEAN)) return function("function", previous().getType());
        if (match(Type.FOR)) return forStatement();
        if(match(Type.IF)) return ifStatement();
        if (match(Type.PRINT)) return printStatement();
        if (match(Type.WHILE)) return whileStatement();
        if(match(Type.LEFT_BRACE)) return new Statement.Block(block());
        return expressionStatement();
    }

    private Statement forStatement() {
        consume("Expect '(' after 'for'.", Type.LEFT_PAREN);

        Statement initializer = null;
        if (match(Type.SEMICOLON)) {
            initializer = null;
        } else if (match(Type.INT, Type.DECIMAL, Type.CHAR, Type.STRING, Type.BOOLEAN)) {
            initializer = varDeclaration(previous().getType());
        } else {
            initializer = expressionStatement();
        }

        Expression condition = null;
        if (!check(Type.SEMICOLON)) {
            condition = expression();
        }
        consume("Expect ';' after loop condition.", Type.SEMICOLON);

        Expression increment = null;
        if (!check(Type.RIGHT_PAREN)) {
            increment = expression();
        }
        consume("Expect ')' after for clauses.", Type.RIGHT_PAREN);
        if (condition == null) condition = new Expression.Literal(true);

        //consume("Expect '{' after ')'.", Type.LEFT_BRACE);
        Statement body = statement();

        return new Statement.For(initializer, condition, increment, body);
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

    private Statement varDeclaration(Type expectedType) {
        Token name = consume("Expect variable name.", Type.IDENTIFIER);

        Expression initializer = null;
        if (match(Type.ASSIGN)) {
            initializer = expression();
        }

        consume("Expect ';' after variable declaration.", Type.SEMICOLON);
        return new Statement.Variable(name, expectedType, initializer);
    }

    private Statement function(String kind, Type expectedType) {
        if(previous().getType() != Type.IDENTIFIER) error(previous(), "Expected identifier after type.");
        Token name = consume("Expect " + kind + " name.", Type.IDENTIFIER);

        consume("Expect '(' after " + kind + " name.", Type.LEFT_PAREN);
        List<Token> parameters = new ArrayList<>();
        if (!check(Type.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Cannot have more than 255 parameters.");
                }

                parameters.add(consume("Expect parameter name.", Type.IDENTIFIER));
            } while (match(Type.COMMA));
        }
        consume("Expect ')' after parameters.", Type.RIGHT_PAREN);

        consume("Expect '{' before " + kind + " body.", Type.LEFT_BRACE);
        List<Statement> body = block();
        return new Statement.Function(name, parameters, body, expectedType);
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
        Expression expression = or();

        if (match(Type.ASSIGN)) {
            Token equals = previous();
            Expression value = assignment();

            if (expression instanceof Expression.Variable) {
                Token name = ((Expression.Variable)expression).name;
                return new Expression.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
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
        Expression expression = equality();

        while(match(Type.AND)) {
            Token operator = previous();
            Expression right = equality();
            expression = new Expression.Logical(expression, operator, right);
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
        Expression expression = addition();

        while(match(Type.LEFT_ANGLE_BRACE, Type.GREATER_EQUAL, Type.RIGHT_ANGLE_BRACE, Type.LESSER_EQUAL)) {
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
        Expression expression = unary();

        while(match(Type.STAR, Type.SLASH, Type.MOD)) {
            Token op = previous();
            Expression right = unary();
            expression = new Expression.Binary(expression, op, right);
        }

        return expression;
    }

    private Expression unary() {
        if(match(Type.MINUS, Type.NOT)) {
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

        Token paren = consume("Expect ')' after arguments.", Type.LEFT_PAREN);

        return new Expression.Call(callee, paren, arguments);
    }

    private Expression call() {
        Expression expression = primary();

        while (true) {
            if (match(Type.LEFT_PAREN)) {
                expression = finishCall(expression);
            } else {
                break;
            }
        }

        return expression;
    }

    private Expression primary() {
        if(match(Type.FALSE)) return new Expression.Literal(false);
        if(match(Type.TRUE)) return new Expression.Literal(true);
        if(match(Type.NULL)) return new Expression.Literal(null);

        if(match(Type.INT_LITERAL, Type.DECIMAL_LITERAL, Type.STRING_LITERAL, Type.CHAR_LITERAL)) {
            return new Expression.Literal(previous().getLiteral());
        }

        if (match(Type.IDENTIFIER)) {
            return new Expression.Variable(previous());
        }

        if(match(Type.LEFT_PAREN)) {
            Expression expression = expression();
            consume("Expected \")\" after expression", Type.RIGHT_PAREN);
            return new Expression.Grouping(expression);
        }

        throw error(peek(), "Expect expression.");
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