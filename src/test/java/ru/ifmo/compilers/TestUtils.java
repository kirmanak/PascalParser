package ru.ifmo.compilers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

class TestUtils {
    static List<Lexeme> getResult(String code) {
        var lexer = new Lexer(new ByteArrayInputStream(code.getBytes()));

        try (lexer) {
            lexer.readToEnd();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lexer.getLexemes();
    }
}
