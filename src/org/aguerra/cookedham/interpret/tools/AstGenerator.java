package org.aguerra.cookedham.interpret.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class AstGenerator {
    public static void defineAst(String dir, String baseClass, List<String> types) throws IOException {
        String path = dir + "/" + baseClass + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package org.aguerra.cookedham.interpret.parse;");
        writer.println();
        writer.println("import org.aguerra.cookedham.interpret.lex.Token;");
        writer.println("import org.aguerra.cookedham.interpret.lex.Type;");
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseClass + " {");

        //define visitor class
        defineVisitor(writer, baseClass, types);
        writer.println("    }");

        //define accept
        writer.println();
        writer.println("    public abstract <R> R accept(Visitor<R> visitor);");

        for(String type : types) {
            String className = type.split(":")[0].trim();
            String fieldList = type.split(":")[1].trim();
            defineType(writer, baseClass, className, fieldList);
        }
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseClass, List<String> types) {
        //TODO FINISH list implementation
        writer.println("    public interface Visitor<R> {");


        for(String field : types) {
            String typeName = field.split(":")[0].trim();
            writer.println("        public R visit" + typeName + baseClass + "(" + typeName + " " + baseClass.toLowerCase() + ");");

        }
    }

    private static void defineType(PrintWriter writer, String baseClass, String className, String fieldList) {
        writer.println("    public static class " + className + " extends " + baseClass + " {");

        //constructor
        writer.println("        public " + className + "(" + fieldList + ") {");

        //define parameters
        String[] fields = fieldList.split(",");
        for(String field : fields) {
            String name = field.trim().split("\\s+")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        //define accept
        writer.println();
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseClass + "(this);");
        writer.println("        }");
        //fields
        writer.println();
        for(String field : fields) {
            writer.println("        public final " + field.trim() + ";");
        }

        writer.println("    }");

    }

    public static void main(String[] args) {
        String outputDir = "C:\\Users\\andre\\IdeaProjects\\CookedHam\\src\\org\\aguerra\\cookedham\\interpret\\parse";
        try {
            defineAst(outputDir, "Expression", Arrays.asList(
                    "Assign      : Token name, Expression value, Expression arrayIndex",
                    "ArrayAccess : Token identifier, Expression index",
                    "ArrayBlock  : List<Expression> elements",
                    "Binary      : Expression left, Token operator, Expression right",
                    "Break       : Statement loop",
                    "Call        : Expression calle, Token paren, List<Expression> arguments",
                    "Grouping    : Expression expression",
                    "Len         : Expression expression, Token keyword",
                    "Literal     : Object value",
                    "Logical     : Expression left, Token operator, Expression right",
                    "Ternary     : Expression condition, Expression truthExpression, Expression falseExpression",
                    "Unary       : Token operator, Expression right",
                    "Variable    : Token name"
            ));

            defineAst(outputDir, "Statement", Arrays.asList(
                    "Block          : List<Statement> statements",
                    "LineExpression : Expression expression",
                    "If             : Expression condition, Statement thenBranch, Statement elseBranch",
                    "Function       : Token name, List<Token> params, List<Type> paramTypes, List<Statement> body, Type returnType",
                    "Print          : Expression expression",
                    "Return         : Token keyword, Expression value",
                    "Variable       : Token name, Type type, Type arrayType, Expression init",
                    "For            : Statement initializer, Expression condition, Expression increment, Statement body",
                    "ForEach        : Statement definition, Expression array, Statement body",
                    "While          : Expression condition, Statement body"
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}