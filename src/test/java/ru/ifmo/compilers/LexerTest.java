package ru.ifmo.compilers;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class LexerTest {

    List<Lexeme> getResult(final String code) {
        final var reader = new BufferedReader(new StringReader(code));
        try {
            return new Lexer().readToEnd(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void emptyProgram() {
        final var code = "Begin\nEnd.";

        final List<Lexeme> expected = List.of(
                new Lexeme(LexemeClass.Keyword, "Begin", 1),
                new Lexeme(LexemeClass.Keyword, "End.", 2)
        );

        assertIterableEquals(expected, getResult(code));
    }
}