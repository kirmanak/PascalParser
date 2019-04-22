package ru.ifmo.compilers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class EntryPoint {
    public static void main(final String[] args) {
        final BufferedReader reader;
        if (args.length > 0) {
            final var path = Paths.get(args[0]);
            BufferedReader tmp = null;
            try {
                tmp = Files.newBufferedReader(path);
            } catch (final IOException e) {
                System.err.printf("Unable to open file %s for reading: %s\n", path, e.toString());
            }

            reader = Objects.requireNonNullElseGet(tmp, () -> new BufferedReader(new InputStreamReader(System.in)));
        } else {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        List<Lexeme> foundLexemes = null;

        try (reader) {
            foundLexemes = new Lexer().readToEnd(reader);
        } catch (final IOException e) {
            System.err.printf("Unable to read input: %s\n", e.getMessage());
        }

        if (foundLexemes != null) {
            for (final var foundLexeme : foundLexemes)
                System.out.println(foundLexeme);
        }
    }
}
