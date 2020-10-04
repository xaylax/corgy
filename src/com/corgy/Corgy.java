package com.corgy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//Interpreter Framework:

public class Corgy {
    static boolean hadError = false; // error flagging boolean.

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jcorgy [script]");
            System.exit(64); //UNIX 'sysexits.h' header exit code convention
        } else if (args.length == 1) {
            runFile(args[0]); //UNIX 'sysexits.h' header exit code convention
        } else {
            runPrompt();
        }
    }

    // corgy is a scripting language, so it runs directly from source
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65); // if there's an error, exit with non-zero exit code.
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.print("> ");
            // readLine() reads a line of input from the user on the command line and returns the result. To kill an interactive command-line app, a user usually types CMD-D. When this 'end-of-file' condition happens, readLine() returns null, so we check for that to exit the loop.
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false; // reset this flag in the interactive loop to make sure that the user's entire session isn't killed when they make a mistake.
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens the forthcoming scanner will emit.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // error function to tell the user some syntax error occurred on a given line.
    static void error(int line, String message) {
        report(line, "", message);
    }

    // helper function for error function.
    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "RIP my dude, it looks like there's an error! [Line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}

