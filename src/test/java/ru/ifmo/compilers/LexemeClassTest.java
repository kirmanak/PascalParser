package ru.ifmo.compilers;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexemeClassTest {

    @Test
    void leftBracketSeparator() {
        assertEquals(LexemeClass.Separator, LexemeClass.determine("("));
    }

    @Test
    void plusSign() {
        assertEquals(LexemeClass.ArithmeticOperator, LexemeClass.determine("+"));
    }

    @Test
    void number() {
        assertEquals(LexemeClass.Const, LexemeClass.determine("123"));
    }
}