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
                if (!oldString.isBlank())
                    foundLexemes.add(new Lexeme(oldClass, oldString, lineNumber));
                if (isLineSeparator)
                    lineNumber++;
                else
                    foundLexemes.add(new Lexeme(LexemeClass.Separator, symbol, lineNumber));
                stringBuilder = new StringBuilder();
                continue;
            }

            if (oldClass == LexemeClass.Ident || oldClass == LexemeClass.Keyword || newClass == LexemeClass.Ident || newClass == LexemeClass.Keyword) {
                if (newClass == LexemeClass.Undefined) {
                    foundLexemes.add(new Lexeme(oldClass, oldString, lineNumber));
                    stringBuilder = new StringBuilder().append(symbol);
                }
                continue;
            }

            if (oldClass == LexemeClass.Const || newClass == LexemeClass.Const) {
                if (newClass != LexemeClass.Const) {
                    foundLexemes.add(new Lexeme(LexemeClass.Const, oldString, lineNumber));
                    stringBuilder = new StringBuilder();
                }
                continue;
            }

            if (newClass != LexemeClass.Undefined) {
                foundLexemes.add(new Lexeme(newClass, newString, lineNumber));
                stringBuilder = new StringBuilder();
            } else if (oldClass != LexemeClass.Undefined) {
                foundLexemes.add(new Lexeme(oldClass, oldString, lineNumber));
                stringBuilder = new StringBuilder().append(symbol);
            }
        }

        return foundLexemes;
    }
}
