package ru.ifmo.compilers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        var Assigment = expected.addChild(new Lexeme(LexemeClass.AssignmentOperator, ":=", 3));
        Assigment.addChild(new Lexeme(LexemeClass.Ident, "a", 3));
        Assigment.addChild(new Lexeme(LexemeClass.Const, "1", 3));

        doAssert(code, expected);
    }

    @Test
    void handleLoops() {
        var code = "Var a;\n" +
                "Begin\n" +
                "  a:=5;\n" +
                "  WHILE a>1 DO\n" +
                "    a := a-1;\n" +
                "End.";

        var expected = new OutputTreeNode<Lexeme>("\nAST");

        expected.addChild(new Lexeme(LexemeClass.Keyword, "Var", 1))
                .addChild(new Lexeme(LexemeClass.Ident, "a", 1));

        var assignment = expected.addChild(new Lexeme(LexemeClass.AssignmentOperator, ":=", 3));
        assignment.addChild(new Lexeme(LexemeClass.Ident, "a", 3));
        assignment.addChild(new Lexeme(LexemeClass.Const, "5", 3));

        var loop = expected.addChild(new Lexeme(LexemeClass.Keyword, "WHILE", 4));

        var comparison = loop.addChild(new Lexeme(LexemeClass.ComparisonOperator, ">", 4));
        comparison.addChild(new Lexeme(LexemeClass.Ident, "a", 4));
        comparison.addChild(new Lexeme(LexemeClass.Const, "1", 4));

        var loopContent = loop.addChild(new Lexeme(LexemeClass.Keyword, "DO", 4));

        assignment = loopContent.addChild(new Lexeme(LexemeClass.AssignmentOperator, ":=", 5));
        assignment.addChild(new Lexeme(LexemeClass.Ident, "a", 5));
        var arithmeticOperator = assignment.addChild(new Lexeme(LexemeClass.ArithmeticOperator, "-", 5));

        arithmeticOperator.addChild(new Lexeme(LexemeClass.Ident, "a", 5));
        arithmeticOperator.addChild(new Lexeme(LexemeClass.Const, "1", 5));

        doAssert(code, expected);
    }

    @Test
    void disallowReuse() {
        assertThrows(IllegalStateException.class, () -> {
            var parser = new Parser(List.of(new Lexeme(LexemeClass.Keyword, "Var", 1)));
            parser.parseProgram();
            parser.parseProgram();
        });
    }

    private void doAssert(String code, OutputTreeNode<Lexeme> expected) {
        var lexemes = TestUtils.getResult(code);
        var parser = new Parser(lexemes);
        lexemes.forEach(System.out::println);
        assertTrue(parser.parseProgram());
        assertEquals(expected, parser.getRoot());
    }
}