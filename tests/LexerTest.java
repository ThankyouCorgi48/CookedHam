import org.aguerra.cookedham.interpret.lex.Lexer;
import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.lex.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    private Lexer lexer;

    @BeforeEach
    void init() {

    }

    @Test
    void testIntegerLiterals() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\integers.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("15", Type.INT_LITERAL, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));
        tokensExpected.add(new Token("23", Type.INT_LITERAL, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));
        tokensExpected.add(new Token("89", Type.INT_LITERAL, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));
        tokensExpected.add(new Token("890", Type.INT_LITERAL, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));
        tokensExpected.add(new Token("4567", Type.INT_LITERAL, 5));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 5));
        tokensExpected.add(new Token("9786", Type.INT_LITERAL, 6));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 6));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    @Test
    void testDecimalLiterals() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\decimals.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("14.5", Type.DECIMAL_LITERAL, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));
        tokensExpected.add(new Token("3.14", Type.DECIMAL_LITERAL, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));
        tokensExpected.add(new Token("243.", Type.DECIMAL_LITERAL, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));
        tokensExpected.add(new Token("0.324", Type.DECIMAL_LITERAL, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));
        tokensExpected.add(new Token("5325.876", Type.DECIMAL_LITERAL, 5));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 5));
        tokensExpected.add(new Token("54.7676", Type.DECIMAL_LITERAL, 6));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 6));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    @Test
    void testCharacterLiteral() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\characters.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("\'a\'", Type.CHAR_LITERAL, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));
        tokensExpected.add(new Token("\'b\'", Type.CHAR_LITERAL, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));
        tokensExpected.add(new Token("\'e\'", Type.CHAR_LITERAL, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));
        tokensExpected.add(new Token("\'0\'", Type.CHAR_LITERAL, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));
        tokensExpected.add(new Token("\'1\'", Type.CHAR_LITERAL, 5));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 5));
        tokensExpected.add(new Token("\'+\'", Type.CHAR_LITERAL, 6));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 6));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    @Test
    void testStringLiteral() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\strings.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("\"hello\"", Type.STRING_LITERAL, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));
        tokensExpected.add(new Token("\"world\"", Type.STRING_LITERAL, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));
        tokensExpected.add(new Token("\"hello world\"", Type.STRING_LITERAL, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));
        tokensExpected.add(new Token("\"1234jskldf;f 3\"", Type.STRING_LITERAL, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));
        tokensExpected.add(new Token("\"klpfew][32][\"", Type.STRING_LITERAL, 5));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 5));
        tokensExpected.add(new Token("\"mfewmfklewfew+--__]<>\"", Type.STRING_LITERAL, 6));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 6));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    @Test
    void testBoolLiterals() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\bools.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("true", Type.TRUE, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));
        tokensExpected.add(new Token("false", Type.FALSE, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));
        tokensExpected.add(new Token("false", Type.FALSE, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));
        tokensExpected.add(new Token("true", Type.TRUE, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    @Test
    void testVariables() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\variables.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("int", Type.INT, 1));
        tokensExpected.add(new Token("year", Type.IDENTIFIER, 1));
        tokensExpected.add(new Token("=", Type.ASSIGN, 1));
        tokensExpected.add(new Token("2019", Type.INT_LITERAL, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));

        tokensExpected.add(new Token("decimal", Type.DECIMAL, 2));
        tokensExpected.add(new Token("pi_three", Type.IDENTIFIER, 2));
        tokensExpected.add(new Token("=", Type.ASSIGN, 2));
        tokensExpected.add(new Token("3.14", Type.DECIMAL_LITERAL, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));

        tokensExpected.add(new Token("string", Type.STRING, 3));
        tokensExpected.add(new Token("greeting", Type.IDENTIFIER, 3));
        tokensExpected.add(new Token("=", Type.ASSIGN, 3));
        tokensExpected.add(new Token("\"hello world\"", Type.STRING_LITERAL, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));

        tokensExpected.add(new Token("char", Type.CHAR, 4));
        tokensExpected.add(new Token("grade", Type.IDENTIFIER, 4));
        tokensExpected.add(new Token("=", Type.ASSIGN, 4));
        tokensExpected.add(new Token("\'c\'", Type.CHAR_LITERAL, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    @Test
    void testOperators() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\operators.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("+", Type.PLUS, 1));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 1));
        tokensExpected.add(new Token("-", Type.MINUS, 2));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 2));
        tokensExpected.add(new Token("*", Type.STAR, 3));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 3));
        tokensExpected.add(new Token("/", Type.SLASH, 4));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 4));
        tokensExpected.add(new Token("%", Type.MOD, 5));
        tokensExpected.add(new Token(";", Type.SEMICOLON, 5));

        checkTokenNumMismatch(tokensExpected, tokens);
        assertEqualsTokens(tokensExpected, tokens);
    }

    void assertEqualsTokens(ArrayList<Token> tokensExpected, ArrayList<Token> tokens) {
        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i).getToken(), tokens.get(i).getToken());
            assertEquals(tokensExpected.get(i).getType(), tokens.get(i).getType());
        }
    }

    void checkTokenNumMismatch(ArrayList<Token> tokensExpected, ArrayList<Token> tokens) {
        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());
    }

    @AfterEach
    void teardown() {
        lexer = null;
    }
}