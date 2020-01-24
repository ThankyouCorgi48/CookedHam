package org.aguerra.cookedham.interpret.run;

import org.aguerra.cookedham.interpret.lex.Lexer;
import org.aguerra.cookedham.interpret.lex.Token;
import org.aguerra.cookedham.interpret.parse.Parser;
import org.aguerra.cookedham.interpret.parse.Statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class CookedHam {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        /*if (args.length > 1) {
            System.out.println("Usage: jham [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\expressions.ch"); //args[0]
        } else {
            runPrompt();
        }*/

        runFile("C:\\Users\\andre\\IdeaProjects\\CookedHam\\test resources\\playground.ch"); //args[0]
        //runPrompt();
    }

    private static void runFile(String path) throws IOException {
        /*byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));*/
        run(path);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print(">> ");
            run(reader.readLine());
        }
    }

    private static void run(String path) {
        Lexer lexer = new Lexer(new File(path));

        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);
        //Expression expression = parser.parse();
        List<Statement> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;
        if (hadRuntimeError) System.exit(70);

        interpreter.interpret(statements);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.getLineNum() + "]");
        hadRuntimeError = true;
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}