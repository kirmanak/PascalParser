package ru.ifmo.compilers;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class LexerTest {

    List<Lexeme> getResult(final String code) {
        final var reader = new BufferedReader(new StringReader(code));
        try (reader) {
            return new Lexer().readToEnd(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void emptyProgram() {
        final var code = "Begin\nEnd.";

        final var expected = List.of(
                new Lexeme(LexemeClass.Keyword, "Begin", 1),
                new Lexeme(LexemeClass.Keyword, "End.", 2)
        );

        assertIterableEquals(expected, getResult(code));
    }

    @Test
    void noText() {
        final String code = "";

        final var expected = Collections.emptyList();

        assertIterableEquals(expected, getResult(code));
    }

    @Test
    void assignmentNoSeparator() {
        final var code = "a:=10;";

        final var expected = List.of(
                new Lexeme(LexemeClass.Ident, "a", 1),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 1),
                new Lexeme(LexemeClass.Const, "10", 1),
                new Lexeme(LexemeClass.Separator, ";", 1)
        );

        assertIterableEquals(expected, getResult(code));
    }

    @Test
    void invalidCharacter() {
        final var code = "ы :=10;";

        final var expected = List.of(
                new Lexeme(LexemeClass.Separator, " ", 1),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 1),
                new Lexeme(LexemeClass.Const, "10", 1),
                new Lexeme(LexemeClass.Separator, ";", 1)
        );

        assertIterableEquals(expected, getResult(code));
    }

    @Test
    void invalidCharacterNoSeparator() {
        final var code = "ы:=10;";

        final var expected = List.of(
                new Lexeme(LexemeClass.Separator, ";", 1)
        );

        assertIterableEquals(expected, getResult(code));
    }

    @Test
    void endsWithLineSeparator() {
        final var code = "Begin\nы:=10;";

        final var expected = List.of(
                new Lexeme(LexemeClass.Keyword, "Begin", 1),
                new Lexeme(LexemeClass.Separator, ";", 2)
        );

        assertIterableEquals(expected, getResult(code));
    }

    @Test
    void bigProgram() {
        final var code =
                "Var i, abc, d;\n" + // 1
                        "Begin\n" + // 2
                        "  i:=10;\n" + // 3
                        "  abc := i;\n" + // 4
                        "  d:=abc,i:=i-10;\n" + // 5
                        "  WHILE i<5 DO\n" + // 6
                        "  Begin\n" + // 7
                        "    i:=i*2;\n" + // 8
                        "  End\n" + // 9
                        "  abc:= d/(i+2);\n" + // 10
                        "End."; // 11

        final var expected = List.of(
                new Lexeme(LexemeClass.Keyword, "Var", 1),
                new Lexeme(LexemeClass.Separator, " ", 1),
                new Lexeme(LexemeClass.Ident, "i", 1),
                new Lexeme(LexemeClass.Separator, ",", 1),
                new Lexeme(LexemeClass.Separator, " ", 1),
                new Lexeme(LexemeClass.Ident, "abc", 1),
                new Lexeme(LexemeClass.Separator, ",", 1),
                new Lexeme(LexemeClass.Separator, " ", 1),
                new Lexeme(LexemeClass.Ident, "d", 1),
                new Lexeme(LexemeClass.Separator, ";", 1),

                new Lexeme(LexemeClass.Keyword, "Begin", 2),

                new Lexeme(LexemeClass.Separator, " ", 3),
                new Lexeme(LexemeClass.Separator, " ", 3),
                new Lexeme(LexemeClass.Ident, "i", 3),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 3),
                new Lexeme(LexemeClass.Const, "10", 3),
                new Lexeme(LexemeClass.Separator, ";", 3),

                new Lexeme(LexemeClass.Separator, " ", 4),
                new Lexeme(LexemeClass.Separator, " ", 4),
                new Lexeme(LexemeClass.Ident, "abc", 4),
                new Lexeme(LexemeClass.Separator, " ", 4),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 4),
                new Lexeme(LexemeClass.Separator, " ", 4),
                new Lexeme(LexemeClass.Ident, "i", 4),
                new Lexeme(LexemeClass.Separator, ";", 4),

                new Lexeme(LexemeClass.Separator, " ", 5),
                new Lexeme(LexemeClass.Separator, " ", 5),
                new Lexeme(LexemeClass.Ident, "d", 5),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 5),
                new Lexeme(LexemeClass.Ident, "abc", 5),
                new Lexeme(LexemeClass.Separator, ",", 5),
                new Lexeme(LexemeClass.Ident, "i", 5),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 5),
                new Lexeme(LexemeClass.Ident, "i", 5),
                new Lexeme(LexemeClass.ArithmeticOperator, "-", 5),
                new Lexeme(LexemeClass.Const, "10", 5),
                new Lexeme(LexemeClass.Separator, ";", 5),

                new Lexeme(LexemeClass.Separator, " ", 6),
                new Lexeme(LexemeClass.Separator, " ", 6),
                new Lexeme(LexemeClass.Keyword, "WHILE", 6),
                new Lexeme(LexemeClass.Separator, " ", 6),
                new Lexeme(LexemeClass.Ident, "i", 6),
                new Lexeme(LexemeClass.ComparisonOperator, "<", 6),
                new Lexeme(LexemeClass.Const, "5", 6),
                new Lexeme(LexemeClass.Separator, " ", 6),
                new Lexeme(LexemeClass.Keyword, "DO", 6),

                new Lexeme(LexemeClass.Separator, " ", 7),
                new Lexeme(LexemeClass.Separator, " ", 7),
                new Lexeme(LexemeClass.Keyword, "Begin", 7),

                new Lexeme(LexemeClass.Separator, " ", 8),
                new Lexeme(LexemeClass.Separator, " ", 8),
                new Lexeme(LexemeClass.Separator, " ", 8),
                new Lexeme(LexemeClass.Separator, " ", 8),
                new Lexeme(LexemeClass.Ident, "i", 8),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 8),
                new Lexeme(LexemeClass.Ident, "i", 8),
                new Lexeme(LexemeClass.ArithmeticOperator, "*", 8),
                new Lexeme(LexemeClass.Const, "2", 8),
                new Lexeme(LexemeClass.Separator, ";", 8),

                new Lexeme(LexemeClass.Separator, " ", 9),
                new Lexeme(LexemeClass.Separator, " ", 9),
                new Lexeme(LexemeClass.Keyword, "End", 9),

                new Lexeme(LexemeClass.Separator, " ", 10),
                new Lexeme(LexemeClass.Separator, " ", 10),
                new Lexeme(LexemeClass.Ident, "abc", 10),
                new Lexeme(LexemeClass.AssignmentOperator, ":=", 10),
                new Lexeme(LexemeClass.Separator, " ", 10),
                new Lexeme(LexemeClass.Ident, "d", 10),
                new Lexeme(LexemeClass.ArithmeticOperator, "/", 10),
                new Lexeme(LexemeClass.Separator, "(", 10),
                new Lexeme(LexemeClass.Ident, "i", 10),
                new Lexeme(LexemeClass.ArithmeticOperator, "+", 10),
                new Lexeme(LexemeClass.Const, "2", 10),
                new Lexeme(LexemeClass.Separator, ")", 10),
                new Lexeme(LexemeClass.Separator, ";", 10),

                new Lexeme(LexemeClass.Keyword, "End.", 11)
        );

        assertIterableEquals(expected, getResult(code));
    }
}