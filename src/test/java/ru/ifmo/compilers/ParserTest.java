package ru.ifmo.compilers;

import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

    @Test
    void parseProgram() {
        var code = "Var a;\n" +
                "Begin\n" +
                "  a := 1;\n" +
                "End.\n";

        var expected = new OutputTreeNode<Lexeme>("\nAST");

        var Var = expected.addChild(new Lexeme(LexemeClass.Keyword, "Var", 1));
        Var.addChild(new Lexeme(LexemeClass.Ident, "a", 1));
        Var.addChild(new Lexeme(LexemeClass.Ident, "b", 1));

        var Begin = expected.addChild(new Lexeme(LexemeClass.Keyword, "Begin", 2));
        var Assigment = Begin.addChild(new Lexeme(LexemeClass.AssignmentOperator, ":=", 3));
        Assigment.addChild(new Lexeme(LexemeClass.Ident, "a", 3));
        Assigment.addChild(new Lexeme(LexemeClass.Const, "1", 3));

        expected.addChild(new Lexeme(LexemeClass.Keyword, "End.", 4));

        assertEquals(expected, getResult(code));
    }

    private OutputTreeNode<Lexeme> getResult(@NonNull String code) {
        return new Parser(TestUtils.getResult(code))
                .getRoot()
                .orElse(null);
    }
}