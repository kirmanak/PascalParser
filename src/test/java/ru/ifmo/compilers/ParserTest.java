package ru.ifmo.compilers;

import lombok.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static void assertCorrect(String code, OutputTreeNode<Lexeme> expected) {
        var parser = getParser(code);
        assertTrue(parser.parseProgram());
        printResults(parser);
        assertEquals(expected, parser.getRoot());
        assertIterableEquals(Collections.emptyList(), parser.getErrorMessages());
    }

    private static void assertIncorrect(@NonNull String code, @NonNull OutputTreeNode<Lexeme> root,
                                        @NonNull List<String> errors) {
        var parser = getParser(code);
        assertFalse(parser.parseProgram());
        printResults(parser);
        assertEquals(root, parser.getRoot());
        assertIterableEquals(errors, parser.getErrorMessages());
    }

    private static Parser getParser(@NonNull String code) {
        var lexemes = TestUtils.getResult(code);
        lexemes.forEach(System.out::println);

        return new Parser(lexemes);
    }

    private static void printResults(@NonNull Parser parser) {
        parser.getRoot().print(System.out);
        System.out.println();
        parser.getErrorMessages().forEach(System.out::println);
    }

    @Test
    void disallowReuse() {
        assertThrows(IllegalStateException.class, () -> {
            var parser = new Parser(List.of(new Lexeme(LexemeClass.Keyword, "Var", 1)));
            parser.parseProgram();
            parser.parseProgram();
        });
    }

    private static OutputTreeNode<Lexeme> getRoot() {
        return new OutputTreeNode<>("\nAST");
    }

    @Test
    void parseProgram() {
        var code = "Var a;\n" +
                "Begin\n" +
                "  a := 1;\n" +
                "End.\n";

        var expected = getRoot();

        var Var = expected.addChild(new Lexeme(LexemeClass.Keyword, "Var", 1));
        Var.addChild(new Lexeme(LexemeClass.Ident, "a", 1));

        var Assigment = expected.addChild(new Lexeme(LexemeClass.AssignmentOperator, ":=", 3));
        Assigment.addChild(new Lexeme(LexemeClass.Ident, "a", 3));
        Assigment.addChild(new Lexeme(LexemeClass.Const, "1", 3));

        assertCorrect(code, expected);
    }

    @Test
    void handleLoops() {
        var code = "Var a;\n" +
                "Begin\n" +
                "  a:=5;\n" +
                "  WHILE a>1 DO\n" +
                "    a := a-1;\n" +
                "End.";

        var expected = getRoot();

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

        assertCorrect(code, expected);
    }

    @Test
    void noBegin() {
        var code = "Var a;\n" +
                "End.";

        var expected = getRoot();

        var Var = expected.addChild(new Lexeme(LexemeClass.Keyword, "Var", 1));
        Var.addChild(new Lexeme(LexemeClass.Ident, "a", 1));

        var expectedError = List.of(
                "On line 2 expected 'Begin', but found 'End.'"
        );

        assertIncorrect(code, expected, expectedError);
    }

    @Test
    void emptyBody() {

    }
}