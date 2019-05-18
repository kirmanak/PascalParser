package ru.ifmo.compilers;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

class Lexer {
    /**
     * Reads the characters from the provided stream until the input is over.
     * Returns the list of lexemes which were found in the stream.
     *
     * @param stream the stream to be read from
     * @return the list of lexemes
     * @throws IOException if failed to read from the stream
     */
    List<Lexeme> readToEnd(InputStream stream) throws IOException {
        var foundLexemes = new LinkedList<Lexeme>();
        var stringBuilder = new StringBuilder();
        LexemeClass newClass = null;
        var lineNumber = 1;

        int character;
        while ((character = stream.read()) != -1) {
            var symbol = String.valueOf((char) character);

            var oldString = stringBuilder.toString();
            stringBuilder.append(symbol);
            var newString = stringBuilder.toString();

            var oldLexemeClass = LexemeClass.determine(oldString);
            newClass = LexemeClass.determine(newString);

            var isLineSeparator = newString.endsWith(System.lineSeparator());

            if (LexemeClass.determine(symbol) == LexemeClass.Separator || isLineSeparator) {
                if (!oldString.isBlank()) {
                    if (oldLexemeClass == LexemeClass.Undefined)
                        System.err.printf("Undefined sequence found on %d-th line: %s\n", lineNumber, oldString);
                    else
                        foundLexemes.add(new Lexeme(oldLexemeClass, oldString, lineNumber));
                }
                if (isLineSeparator)
                    lineNumber++;
                else
                    foundLexemes.add(new Lexeme(LexemeClass.Separator, symbol, lineNumber));
                stringBuilder = new StringBuilder();
                continue;
            }

            if (oldLexemeClass != LexemeClass.Undefined && newClass == LexemeClass.Undefined) {
                foundLexemes.add(new Lexeme(oldLexemeClass, oldString, lineNumber));
                stringBuilder = new StringBuilder().append(symbol);
            }
        }

        final var lastCharacters = stringBuilder.toString();
        if (!lastCharacters.isBlank())
            foundLexemes.add(new Lexeme(newClass, lastCharacters, lineNumber));

        return foundLexemes;
    }
}
