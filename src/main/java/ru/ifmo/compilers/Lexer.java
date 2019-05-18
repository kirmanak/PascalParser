package ru.ifmo.compilers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
class Lexer implements AutoCloseable, Closeable {
    @NonNull
    private final InputStream stream;
    private List<Lexeme> lexemes = null;
    private String currentString = "";
    private int lineNumber = 1;
    private LexemeClass currentClass;

    /**
     * Reads the characters from the provided stream until the input is over.
     *
     * @throws IOException           if failed to read from the stream
     * @throws IllegalStateException if already has read a file successfully
     */
    void readToEnd() throws IOException {
        if (lexemes != null)
            throw new IllegalStateException("You have to use a new instance of " + Lexer.class.getSimpleName());

        int character;
        while ((character = stream.read()) != -1) {
            onNewSymbol(Character.toString(character));
        }

        if (!currentString.isBlank())
            addLexeme(new Lexeme(currentClass, currentString, lineNumber));
    }

    /**
     * Checks each new symbol
     *
     * @param symbol the new symbol
     */
    // TODO: add support for comments
    private void onNewSymbol(String symbol) {
        String oldString = currentString;
        currentString += symbol;

        LexemeClass oldLexemeClass = LexemeClass.determine(oldString);
        currentClass = LexemeClass.determine(currentString);

        boolean isLineSeparator = currentString.endsWith(System.lineSeparator());

        if (LexemeClass.Separator.test(symbol) || isLineSeparator) {
            if (!oldString.isBlank()) {
                if (oldLexemeClass == LexemeClass.Undefined)
                    System.err.printf("Undefined sequence found on %d-th line: %s\n", lineNumber, oldString);
                else
                    addLexeme(new Lexeme(oldLexemeClass, oldString, lineNumber));
            }

            if (isLineSeparator)
                lineNumber++;
            else
                addLexeme(new Lexeme(LexemeClass.Separator, symbol, lineNumber));

            currentString = "";
        } else if (oldLexemeClass != LexemeClass.Undefined && currentClass == LexemeClass.Undefined) {
            addLexeme(new Lexeme(oldLexemeClass, oldString, lineNumber));
            currentString = symbol;
        }
    }

    /**
     * Gets the lexing result
     *
     * @return the list of found lexemes
     */
    List<Lexeme> getLexemes() {
        return lexemes == null ? Collections.emptyList() : lexemes;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    /**
     * Adds lexeme to result
     *
     * @param lexeme the new lexeme
     */
    private void addLexeme(Lexeme lexeme) {
        if (lexemes == null)
            lexemes = new LinkedList<>();

        lexemes.add(lexeme);
    }
}
