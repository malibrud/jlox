package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException
    {
        if (args.length != 1){
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary: Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal: Object value",
            "Unary : Token operator, Expr right"
        ));
    }
    
    private static void defineAst( String outputDir, String baseName, List<String> types) throws IOException
    {
        String path = outputDir + "/" + baseName + ".java";
        var writer = new PrintWriter(path, "UTF-8");

        writer.println("import java.util.list;");
        writer.println();
        writer.format("abstract class %s {\n", baseName);

        defineVisitor(writer, baseName, types);

        for (var type : types)
        {
            var className = type.split(":")[0].trim();
            var fields    = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineType(
        PrintWriter writer,
        String baseName,
        String className,
        String fieldList
    ) {
        writer.println();
        writer.format("  static class %s extends %s {\n", className, baseName);
        writer.format("     %s (%s) {\n", className, fieldList);
        var fields = fieldList.split(", ");
        for (var field : fields)
        {
            var name = field.split(" ")[1];
            writer.format("      this.%s = %s;\n", name, name);
        }
        writer.format("    }\n");
        writer.println();

        // Visitor pattern
        writer.format("    @Override\n");
        writer.format("    <R> R accept(Visitor<R> visitor) {\n");
        writer.format("      return visitor.visit%s%s(this);\n", className, baseName);
        writer.format("    }\n");

        // Fields
        for (var field : fields)
        {
            writer.format("    final %s;\n", field);
        }
        writer.format("  }\n");
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types)
    {
        writer.format("  interface Visitor<R> {\n");
        for (var type : types)
        {   
            var typeName = type.split(":")[0].trim();
            writer.format("    R visit%s%s(%s %s);\n", typeName, baseName, typeName, baseName.toLowerCase());
        }
        writer.format("  }\n");
    }
}