package ru.ifmo.compilers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class Lexer {
    List<Lexeme> readToEnd(final BufferedReader reader) throws IOException {
        final var foundLexemes = new LinkedList<Lexeme>();
        var stringBuilder = new StringBuilder();
        LexemeClass newClass = null;
        var lineNumber = 1;

        int character;
        while ((character = reader.read()) != -1) {
            final var symbol = String.valueOf((char) character);

            final var oldString = stringBuilder.toString();
            stringBuilder.append(symbol);
            final var newString = stringBuilder.toString();

            final var oldClass = LexemeClass.determine(oldString);
            newClass = LexemeClass.determine(newString);

            final var isLineSeparator = newString.endsWith(System.lineSeparator());

            if (LexemeClass.determine(symbol) == LexemeClass.Separator || isLineSeparator) {
                if (!oldString.isBlank()) {
                    if (oldClass == LexemeClass.Undefined)
                        System.err.printf("Undefined sequence found on %d-th line: %s\n", lineNumber, oldString);
                    else
                        foundLexemes.add(new Lexeme(oldClass, oldString, lineNumber));
                }
                if (isLineSeparator)
                    lineNumber++;
                else
                    foundLexemes.add(new Lexeme(LexemeClass.Separator, symbol, lineNumber));
                stringBuilder = new StringBuilder();
                continue;
            }

            if (oldClass != LexemeClass.Undefined && newClass == LexemeClass.Undefined) {
                foundLexemes.add(new Lexeme(oldClass, oldString, lineNumber));
                stringBuilder = new StringBuilder().append(symbol);
            }
        }

        final var lastCharacters = stringBuilder.toString();
        if (!lastCharacters.isBlank())
            foundLexemes.add(new Lexeme(newClass, lastCharacters, lineNumber));

        return foundLexemes;
    }
}
