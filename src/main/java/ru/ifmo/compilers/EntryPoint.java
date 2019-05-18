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

    /**
     * Tries to open the file
     *
     * @param name the name of the target file
     * @return result if the file has been open successfully
     */
    private static Optional<InputStream> openFile(Path name) {
        try {
            return Optional.of(Files.newInputStream(name, StandardOpenOption.READ));
        } catch (IOException e) {
            System.err.printf("Unable to open file %s for reading: %s\n", name, e.toString());
        }

        return Optional.empty();
    }

    /**
     * Maps the arguments to list of open files
     *
     * @param args the arguments from the user
     * @return list of open files
     */
    private static List<BufferedInputStream> getOpenFiles(String[] args) {
        return Stream.of(args)
                .map(Paths::get)
                .map(EntryPoint::openFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BufferedInputStream::new)
                .collect(Collectors.toList());
    }

    /**
     * Tries to map an open file to list of the lexemes
     *
     * @param inputStream the file to be lexed
     * @return the list of the lexemes if successfully read the file
     */
    private static Optional<List<Lexeme>> getLexemes(BufferedInputStream inputStream) {
        try (inputStream) {
            return Optional.of(new Lexer().readToEnd(inputStream));
        } catch (IOException e) {
            System.err.printf("Unable to read input: %s\n", e.getMessage());
        }

        return Optional.empty();
    }
}
