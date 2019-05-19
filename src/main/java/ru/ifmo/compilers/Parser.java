package ru.ifmo.compilers;

import lombok.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Performs the syntax analysis
 */
class Parser {
    /**
     * The list of lexemes from {@link Lexer}
     */
    @NonNull
    private final List<Lexeme> lexemes;
    private OutputTreeNode<Lexeme> root = new OutputTreeNode<>(new Lexeme(LexemeClass.Undefined, "Program root", -1));
    private Deque<OutputTreeNode<Lexeme>> stack = new ArrayDeque<>();

    /**
     * Constructs a new instance of parser
     *
     * @param lexemes the list of lexemes to be parsed
     */
    Parser(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
        stack.push(root);
    }

    Parser parseProgram() {
        if (root != null)
            throw new IllegalStateException("AST was already parsed!");

        lexemes.forEach(this::onNewLexeme);

        return this;
    }

    Optional<OutputTreeNode> getRoot() {
        return Optional.ofNullable(root);
    }

    private void onNewLexeme(Lexeme lexeme) {
        root.addChild(lexeme);
        Optional.ofNullable(stack.peek()).ifPresent(head -> );
    }
}
