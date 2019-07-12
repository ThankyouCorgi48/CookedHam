import org.aguerra.cookedham.compile.*;
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

        tokensExpected.add(new Token("15", Type.INTEGER_LITERAL));
        tokensExpected.add(new Token("23", Type.INTEGER_LITERAL));
        tokensExpected.add(new Token("89", Type.INTEGER_LITERAL));
        tokensExpected.add(new Token("890", Type.INTEGER_LITERAL));
        tokensExpected.add(new Token("4567", Type.INTEGER_LITERAL));
        tokensExpected.add(new Token("9786", Type.INTEGER_LITERAL));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @Test
    void testDecimalLiterals() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\decimals.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("14.5", Type.DECIMAL_LITERAL));
        tokensExpected.add(new Token("3.14", Type.DECIMAL_LITERAL));
        tokensExpected.add(new Token("243.", Type.DECIMAL_LITERAL));
        tokensExpected.add(new Token("0.324", Type.DECIMAL_LITERAL));
        tokensExpected.add(new Token("5325.876", Type.DECIMAL_LITERAL));
        tokensExpected.add(new Token("54.7676", Type.DECIMAL_LITERAL));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @Test
    void testCharacterLiteral() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\characters.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("\'a\'", Type.CHARACTER_LITERAL));
        tokensExpected.add(new Token("\'b\'", Type.CHARACTER_LITERAL));
        tokensExpected.add(new Token("\'e\'", Type.CHARACTER_LITERAL));
        tokensExpected.add(new Token("\'0\'", Type.CHARACTER_LITERAL));
        tokensExpected.add(new Token("\'1\'", Type.CHARACTER_LITERAL));
        tokensExpected.add(new Token("\'+\'", Type.CHARACTER_LITERAL));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @Test
    void testStringLiteral() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\strings.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("\"hello\"", Type.STRING_LITERAL));
        tokensExpected.add(new Token("\"world\"", Type.STRING_LITERAL));
        tokensExpected.add(new Token("\"hello world\"", Type.STRING_LITERAL));
        tokensExpected.add(new Token("\"1234jskldf;f 3\"", Type.STRING_LITERAL));
        tokensExpected.add(new Token("\"klpfew][32][\"", Type.STRING_LITERAL));
        tokensExpected.add(new Token("\"mfewmfklewfew+--__]<>\"", Type.STRING_LITERAL));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @Test
    void testBoolLiterals() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\bools.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("true", Type.BOOL_LITERAL));
        tokensExpected.add(new Token("false", Type.BOOL_LITERAL));
        tokensExpected.add(new Token("false", Type.BOOL_LITERAL));
        tokensExpected.add(new Token("true", Type.BOOL_LITERAL));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @Test
    void testVariables() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\variables.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("var", Type.KEYWORD));
        tokensExpected.add(new Token("year", Type.IDENTIFIER));
        tokensExpected.add(new Token("=", Type.ASSIGNMENT));
        tokensExpected.add(new Token("2019", Type.INTEGER_LITERAL));

        tokensExpected.add(new Token("var", Type.KEYWORD));
        tokensExpected.add(new Token("pi_three", Type.IDENTIFIER));
        tokensExpected.add(new Token("=", Type.ASSIGNMENT));
        tokensExpected.add(new Token("3.14", Type.DECIMAL_LITERAL));

        tokensExpected.add(new Token("var", Type.KEYWORD));
        tokensExpected.add(new Token("greeting", Type.IDENTIFIER));
        tokensExpected.add(new Token("=", Type.ASSIGNMENT));
        tokensExpected.add(new Token("\"hello world\"", Type.STRING_LITERAL));

        tokensExpected.add(new Token("var", Type.KEYWORD));
        tokensExpected.add(new Token("grade", Type.IDENTIFIER));
        tokensExpected.add(new Token("=", Type.ASSIGNMENT));
        tokensExpected.add(new Token("\'c\'", Type.CHARACTER_LITERAL));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @Test
    void testOperators() {
        lexer = new Lexer(new File("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\operators.ch"));

        ArrayList<Token> tokens = lexer.getTokens();
        ArrayList<Token> tokensExpected = new ArrayList<>();

        tokensExpected.add(new Token("+", Type.OPERATOR));
        tokensExpected.add(new Token("-", Type.OPERATOR));
        tokensExpected.add(new Token("*", Type.OPERATOR));
        tokensExpected.add(new Token("/", Type.OPERATOR));
        tokensExpected.add(new Token("%", Type.OPERATOR));
        tokensExpected.add(new Token("^", Type.OPERATOR));

        if(tokens.size() != tokensExpected.size()) fail("Token Number Mismatch: expected "
                + tokensExpected.size() + " received " + tokens.size());

        for (int i = 0; i < tokensExpected.size(); i++) {
            assertEquals(tokensExpected.get(i), tokens.get(i));
        }
    }

    @AfterEach
    void teardown() {
        lexer = null;
    }
}