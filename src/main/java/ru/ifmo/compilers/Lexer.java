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

            if (LexemeClass.determine(symbol) == LexemeClass.Separator) {
                final String token = stringBuilder.toString();

                final LexemeClass lexemeClass = LexemeClass.determine(token);
                if (lexemeClass != LexemeClass.Undefined)
                    foundLexemes.add(new Lexeme(lexemeClass, token, lineNumber));

                stringBuilder.append(symbol);
                if (stringBuilder.toString().endsWith(System.lineSeparator()))
                    lineNumber++;

                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append(symbol);
            }
        }

        return foundLexemes;
    }
}
