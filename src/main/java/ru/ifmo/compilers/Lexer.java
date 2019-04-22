package ru.ifmo.compilers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class Lexer {
    List<Lexeme> readToEnd(final BufferedReader reader) throws IOException {
        final List<Lexeme> foundLexemes = new LinkedList<>();
        StringBuilder stringBuilder = new StringBuilder();
        int lineNumber = 1;

        int character;
        while ((character = reader.read()) != -1) {
            final String symbol = String.valueOf((char) character);

            final String oldString = stringBuilder.toString();
            stringBuilder.append(symbol);
            final String newString = stringBuilder.toString();

            final LexemeClass oldClass = LexemeClass.determine(oldString);
            final LexemeClass newClass = LexemeClass.determine(newString);

            final boolean isLineSeparator = newString.endsWith(System.lineSeparator());

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

        return foundLexemes;
    }
}
