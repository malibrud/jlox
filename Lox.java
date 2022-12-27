import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Lox 
{
    private static boolean hadError;

    public static void main(String args[]) throws IOException
    {
        if (args.length > 1)
        {
            System.out.println("Usage: jlox [script]")
        }
        else if (args.length == 1)
        {
            runFile(args[0]);
        }
        else
        {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException
    {
        var bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException
    {
        var input = new InputStreamReader(System.in);
        var reader = new BufferedReader(input);
        for (;;)
        {
            System.out.print("> ");
            var line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }

    private static void run(String source) 
    {
        var scanner = new Scanner(source)
        var tokens = scanner.scanTokens();

        for (Token token: tokens)
        {
            System.out.println(token);
        }
    }

    static void error( int line, String message )
    {
        report(line, "", message);
    }

    private static void report(int line, String where, String message)
    {
        System.err.println( String.format("[line %d] Error %s: %s", line, where, message));
        hadError = true;
    }
}