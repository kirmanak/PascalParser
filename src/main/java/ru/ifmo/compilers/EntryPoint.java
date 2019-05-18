package ru.ifmo.compilers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryPoint {
    /**
     * The program entry point. Checks arguments for a file name, tries to open it if any present.
     * If none is present or the open attempt failed, reads from stdin. Passes the input to Lexer, prints the result.
     *
     * @param args arguments passed to program on start
     */
    public static void main(String[] args) {
        var openFiles = getOpenFiles(args);

        if (openFiles.isEmpty())
            openFiles.add(new BufferedInputStream(System.in));

        openFiles.stream()
                .map(EntryPoint::getLexemes)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(lexemes -> {
                    System.out.println("\nPrinting the result for next file:\n");
                    lexemes.forEach(System.out::println);
                });
    }

    private static Optional<InputStream> openFile(Path name) {
        try {
            return Optional.of(Files.newInputStream(name, StandardOpenOption.READ));
        } catch (IOException e) {
            System.err.printf("Unable to open file %s for reading: %s\n", name, e.toString());
        }

        return Optional.empty();
    }

    private static List<BufferedInputStream> getOpenFiles(String[] args) {
        return Stream.of(args)
                .map(Paths::get)
                .map(EntryPoint::openFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BufferedInputStream::new)
                .collect(Collectors.toList());
    }

    private static Optional<List<Lexeme>> getLexemes(BufferedInputStream inputStream) {
        try (inputStream) {
            return Optional.of(new Lexer().readToEnd(inputStream));
        } catch (IOException e) {
            System.err.printf("Unable to read input: %s\n", e.getMessage());
        }

        return Optional.empty();
    }
}
